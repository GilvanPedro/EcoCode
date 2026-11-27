package backend_ecocode.Controller;

import backend_ecocode.Instanciador;
import backend_ecocode.Domain.Entities.*;
import backend_ecocode.Domain.Enums.*;
import backend_ecocode.Exceptions.AcessoNegadoException;
import backend_ecocode.Persistency.Banco.PersistenciaBancoSessao;
import backend_ecocode.Persistency.Banco.ConexaoBanco;
import java.util.function.Supplier;
import backend_ecocode.Util.LogarUsuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping(value = "/api", headers = "X-EcoCode=web")
public class EcoCodeController {
    private Instanciador app = new Instanciador();
    private final PersistenciaBancoSessao sessoes = new PersistenciaBancoSessao();
    // O controlador serializa o acesso às estruturas compartilhadas. Se a operação
    // reverter no MySQL, descarte também as alterações feitas nas coleções Java.
    private <T> T gravar(Supplier<T> operacao) {
        try {
            return ConexaoBanco.emTransacao(operacao);
        } catch (RuntimeException erro) {
            app = new Instanciador();
            throw erro;
        }
    }

    record Perfil(String nome, String cpf, String email, CargoUsuario cargo, Bairro bairro) {
        Perfil(Usuario u) {
            this(u.getNomeCompleto(), u.getCpf(), u.getEmail(), u.getCargoUsuario(), u.getBairro());
        }
    }
    record Conta(String nome, String cpf, String email, String senha, String nascimento, Bairro bairro) {}
    record Acesso(String email, String senha, Boolean manter) {}
    record Material(TipoResiduo tipo, int quantidade, String descricao) {}
    record Ponto(String nome, String endereco, Bairro bairro) {}
    record Pedido(UUID residuoId) {}
    record Cargo(String cpf, CargoUsuario cargo) {}

    private String token(HttpServletRequest req) {
        if (req.getCookies() != null)
            for (Cookie cookie : req.getCookies())
                if ("ecocode_session".equals(cookie.getName())) return cookie.getValue();
        return null;
    }

    private Usuario atual(HttpServletRequest req) {
        String token = token(req);
        Sessao sessao = token == null ? null : sessoes.buscar(token);
        if (sessao == null || sessao.estaVencida())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Entre novamente");
        Usuario usuario = app.getUsuariosService().buscarPorId(sessao.getUsuarioId());
        if (usuario == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return usuario;
    }

    private void cookie(HttpServletRequest req, HttpServletResponse res, String valor, long segundos) {
        res.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from("ecocode_session", valor)
                .httpOnly(true).secure(req.isSecure()).sameSite("Lax").path("/api").maxAge(segundos).build().toString());
    }

    @GetMapping("/bairros")
    public Map<String,String> bairros() {
        Map<String,String> itens = new LinkedHashMap<>();
        for (Bairro b : Bairro.values()) itens.put(b.name(), b.getDescricao());
        return itens;
    }

    @PostMapping("/contas")
    public synchronized Perfil cadastrar(@RequestBody Conta c) {
        if (c.nascimento == null || c.senha == null || c.senha.length() < 8)
            throw new IllegalArgumentException("Informe nascimento e senha com pelo menos oito caracteres");
        return gravar(() -> new Perfil(app.getUsuariosService().cadastrar(c.cpf, c.nome, c.email, c.senha, c.nascimento, c.bairro)));
    }

    @PostMapping("/login")
    public synchronized Perfil entrar(@RequestBody Acesso a, HttpServletRequest req, HttpServletResponse res) {
        Usuario u;
        try { u = new LogarUsuario(app.getUsuariosService()).logarUsuario(a.email, a.senha); }
        catch (IllegalArgumentException | AcessoNegadoException erro) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }
        Sessao sessao = new Sessao(u.getId());
        sessoes.salvar(sessao);
        if (token(req) != null) sessoes.apagar(token(req));
        cookie(req, res, sessao.getToken(), Boolean.TRUE.equals(a.manter) ? Duration.ofDays(30).toSeconds() : -1);
        return new Perfil(u);
    }

    @PostMapping("/logout")
    public synchronized void sair(HttpServletRequest req, HttpServletResponse res) {
        if (token(req) != null) sessoes.apagar(token(req));
        cookie(req, res, "", 0);
    }

    @GetMapping("/perfil")
    public synchronized Perfil perfil(HttpServletRequest req) { return new Perfil(atual(req)); }

    @PutMapping("/perfil")
    public synchronized Perfil editar(@RequestBody Conta c, HttpServletRequest req) {
        if (c.senha != null && !c.senha.isEmpty() && c.senha.length() < 8)
            throw new IllegalArgumentException("A senha deve ter pelo menos oito caracteres");
        return gravar(() -> new Perfil(app.getUsuariosService().editar(atual(req), c.nome, c.email, c.senha, c.bairro)));
    }

    @GetMapping("/usuarios")
    public synchronized Perfil buscar(@RequestParam String cpf, HttpServletRequest req) {
        if (atual(req).getCargoUsuario() != CargoUsuario.GERENTE)
            throw new AcessoNegadoException("Consulta restrita ao gerente");
        return new Perfil(app.getUsuariosService().buscar(cpf));
    }

    @PutMapping("/cargo")
    public synchronized Perfil cargo(@RequestBody Cargo c, HttpServletRequest req) {
        return gravar(() -> new Perfil(app.getUsuariosService().promoverUsuario(atual(req), c.cpf, c.cargo)));
    }

    @GetMapping("/residuos")
    public synchronized List<Residuo> residuos(HttpServletRequest req) {
        return app.getResiduosService().listarDoUsuario(atual(req).getId());
    }

    @PostMapping("/residuos")
    public synchronized Residuo residuo(@RequestBody Material m, HttpServletRequest req) {
        return gravar(() -> app.getResiduosService().salvar(atual(req), m.tipo, m.quantidade, m.descricao));
    }

    @DeleteMapping("/residuos/{codigo}")
    public synchronized void remover(@PathVariable String codigo, HttpServletRequest req) {
        Usuario u = atual(req);
        Residuo r = app.getResiduosService().buscar(codigo);
        if (app.getSolicitacoesService().listarDoUsuario(u.getId()).stream()
                .anyMatch(p -> p.getResiduoId().equals(r.getId())))
            throw new IllegalArgumentException("O histórico de descarte utiliza este resíduo");
        gravar(() -> {
            app.getResiduosService().excluir(u, codigo);
            return null;
        });
    }

    @GetMapping("/pontos")
    public synchronized List<PontoColeta> pontos(HttpServletRequest req) {
        atual(req);
        return app.getPontoColetaService().listar();
    }

    @GetMapping("/pontos/proximo")
    public synchronized Optional<PontoColeta> proximo(HttpServletRequest req) {
        Usuario u = atual(req);
        if (app.getPontoColetaService().listar().isEmpty()) return Optional.empty();
        return Optional.of(app.getPontoColetaService().pontoMaisProximo(u));
    }

    @PostMapping("/pontos")
    public synchronized PontoColeta ponto(@RequestBody Ponto p, HttpServletRequest req) {
        return gravar(() -> app.getPontoColetaService().cadastrar(atual(req), p.nome, p.endereco, p.bairro));
    }

    @PutMapping("/pontos/{id}")
    public synchronized PontoColeta editarPonto(@PathVariable UUID id, @RequestBody Ponto p, HttpServletRequest req) {
        return gravar(() -> app.getPontoColetaService().editar(atual(req), id, p.nome, p.endereco, p.bairro));
    }

    record ResumoPedido(UUID id, UUID residuoId, LocalDateTime dataSolicitacao, StatusSolicitacao status,
                        String solicitante, String bairro, String material, Integer posicao) {}

    private ResumoPedido resumo(Solicitacao pedido) {
        Usuario dono = app.getUsuariosService().buscarPorId(pedido.getUsuarioId());
        Residuo r = app.getResiduosService().buscarPorId(pedido.getResiduoId());
        Integer posicao = pedido.getStatus() == StatusSolicitacao.PENDENTE
                ? app.getSolicitacoesService().posicaoNaFila(pedido.getId()) : null;
        return new ResumoPedido(pedido.getId(), r.getId(), pedido.getDataSolicitacao(), pedido.getStatus(),
                dono.getNomeCompleto(), dono.getBairro().getDescricao(), r.getCodigo() + " · " + r.getCategoriaResiduo(), posicao);
    }

    @GetMapping("/fila")
    public synchronized List<ResumoPedido> fila(HttpServletRequest req) {
        return app.getSolicitacoesService().listarParaEquipe(atual(req)).stream()
                .filter(p -> p.getStatus() == StatusSolicitacao.PENDENTE || p.getStatus() == StatusSolicitacao.EM_ROTA)
                .map(this::resumo).toList();
    }

    @GetMapping("/pedidos")
    public synchronized List<ResumoPedido> pedidos(HttpServletRequest req, @RequestParam(defaultValue="false") boolean equipe) {
        Usuario u = atual(req);
        if (equipe && u.getCargoUsuario() != CargoUsuario.GERENTE)
            throw new AcessoNegadoException("Histórico da equipe restrito ao gerente");
        List<Solicitacao> lista = equipe ? app.getSolicitacoesService().listarParaEquipe(u)
                : app.getSolicitacoesService().listarDoUsuario(u.getId());
        return lista.stream().map(this::resumo).toList();
    }

    @PostMapping("/pedidos")
    public synchronized Solicitacao solicitar(@RequestBody Pedido p, HttpServletRequest req) {
        return gravar(() -> app.getSolicitacoesService().solicitar(atual(req), p.residuoId));
    }

    @PostMapping("/pedidos/proximo")
    public synchronized Solicitacao pegar(HttpServletRequest req) {
        return gravar(() -> app.getSolicitacoesService().pegarProxima(atual(req)));
    }

    @PostMapping("/pedidos/{id}/concluir")
    public synchronized Solicitacao concluir(@PathVariable UUID id, HttpServletRequest req) {
        return gravar(() -> app.getSolicitacoesService().concluir(atual(req), id));
    }

    @PostMapping("/pedidos/{id}/cancelar")
    public synchronized Solicitacao cancelar(@PathVariable UUID id, HttpServletRequest req) {
        return gravar(() -> app.getSolicitacoesService().cancelar(atual(req), id));
    }

    @GetMapping("/log")
    public synchronized List<RegistroLog> log(HttpServletRequest req) { return app.getLogService().listar(atual(req)); }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> invalido(RuntimeException e) { return Map.of("mensagem", e.getMessage()); }

    @ExceptionHandler(AcessoNegadoException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String,String> negado(AcessoNegadoException e) { return Map.of("mensagem", e.getMessage()); }
}
