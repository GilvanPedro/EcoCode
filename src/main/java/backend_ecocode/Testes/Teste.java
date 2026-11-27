package backend_ecocode.Testes;

import backend_ecocode.Estrutura.ArvoreAVL;
import backend_ecocode.Estrutura.Dijkstra;
import backend_ecocode.Estrutura.FilaEncadeada;
import backend_ecocode.Estrutura.Grafo;
import backend_ecocode.Estrutura.PilhaEncadeada;
import backend_ecocode.Domain.Entities.PontoColeta;
import backend_ecocode.Domain.Entities.RegistroLog;
import backend_ecocode.Domain.Entities.Residuo;
import backend_ecocode.Domain.Entities.Solicitacao;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.*;
import backend_ecocode.Exceptions.AcessoNegadoException;
import backend_ecocode.Instanciador;
import backend_ecocode.Persistency.Banco.PersistenciaBancoUsuario;
import backend_ecocode.Repository.GerenciarLogRepository;
import backend_ecocode.Repository.GerenciarSolicitacoesRepository;
import backend_ecocode.Service.*;
import backend_ecocode.Util.Aritmetica;
import backend_ecocode.Util.CifraAfim;
import backend_ecocode.Util.GrafoBairros;
import backend_ecocode.Util.LogarUsuario;
import backend_ecocode.Util.Senhas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Teste {

    static int ok = 0, falhou = 0;

    public static void main(String[] args) {
        // Os quatro usuários precisam existir no banco antes dos resíduos e dos registros de auditoria.
        Usuario maria = new Usuario("Maria Souza", "529.982.247-25",
                "maria@teste.com", Senhas.proteger("senha123"), CargoUsuario.USUARIO, "10/05/2000",
                Bairro.SETOR_BUENO);
        Usuario joao = new Usuario("João Lima", "123.456.789-09",
                "joao@teste.com", Senhas.proteger("senha123"), CargoUsuario.USUARIO, "02/02/1998",
                Bairro.JARDIM_CURITIBA);
        Usuario coletor = new Usuario("Carlos Coletor", "111.444.777-35",
                "carlos@teste.com", Senhas.proteger("senha123"), CargoUsuario.FUNCIONARIO, "15/08/1995",
                Bairro.SETOR_CENTRAL);
        Usuario gerente = new Usuario("Ana Gerente", "390.533.447-05",
                "ana@teste.com", Senhas.proteger("senha123"), CargoUsuario.GERENTE, "20/03/1990",
                Bairro.PARQUE_AMAZONIA);

        PersistenciaBancoUsuario banco = new PersistenciaBancoUsuario();
        if (!banco.carregar().isEmpty())
            throw new IllegalStateException("Execute o teste geral em um banco de teste vazio");
        banco.salvar(List.of(maria, joao, coletor, gerente));
        Instanciador inst = new Instanciador();
        GerenciarResiduosService residuos = inst.getResiduosService();
        GerenciarSolicitacoesService solicitacoes = inst.getSolicitacoesService();
        GerenciarPontoColetaService pontos = inst.getPontoColetaService();

        // ================================================================
        System.out.println("=== RESÍDUOS ===");
        Residuo notebook = residuos.salvar(maria, TipoResiduo.COMPUTADORES, 1, "Notebook Dell Inspiron que não liga mais");
        Residuo celular  = residuos.salvar(maria, TipoResiduo.CELULARES, 2, "Dois celulares Samsung com tela quebrada");
        Residuo cabos    = residuos.salvar(maria, TipoResiduo.CABOS, 5, "Cabos USB, carregadores e um mouse com fio");
        Residuo pilhas   = residuos.salvar(joao, TipoResiduo.BATERIAS, 12, "Pilhas AA usadas de controle remoto");
        Residuo tv       = residuos.salvar(joao, TipoResiduo.OUTROS, 1, "TV de tubo 21 polegadas");
        Residuo tablet   = residuos.salvar(maria, TipoResiduo.CELULARES, 1, "Tablet antigo com bateria estufada");

        Residuo[] todos = {notebook, celular, cabos, pilhas, tv, tablet};
        for (Residuo r : todos) {
            conferir("cadastra com UUID: " + r.getDescricao(), r.getId() != null);
            conferir("começa DISPONIVEL: " + r.getDescricao(), r.getStatus() == StatusResiduo.DISPONIVEL);
        }

        conferir("buscarPorId acha o notebook",
                residuos.buscarPorId(notebook.getId()).getId().equals(notebook.getId()));
        conferir("buscar pelo código acha o notebook",
                residuos.buscar(notebook.getCodigo()).getId().equals(notebook.getId()));
        conferir("código do computador termina em 0 ou 5 (resto 0)",
                Integer.parseInt(notebook.getCodigo()) % 5 == TipoResiduo.COMPUTADORES.getResto());
        conferir("código do celular tem resto 1",
                Integer.parseInt(celular.getCodigo()) % 5 == TipoResiduo.CELULARES.getResto());
        conferir("os seis aparecem nos disponíveis", contemTodos(residuos, todos));

        esperaErro("quantidade 0 dá erro", () -> residuos.salvar(maria, TipoResiduo.CABOS, 0, "x"));
        esperaErro("quantidade negativa dá erro", () -> residuos.salvar(maria, TipoResiduo.CABOS, -3, "x"));
        esperaErro("tipo null dá erro", () -> residuos.salvar(maria, null, 1, "x"));
        esperaErro("código com letra dá erro", () -> residuos.buscar("12a"));
        esperaErro("código inexistente dá erro", () -> residuos.buscar("999999"));
        esperaErro("buscarPorId null dá erro", () -> residuos.buscarPorId(null));
        esperaErro("resíduo sem dono dá erro", () -> residuos.salvar(null, TipoResiduo.CABOS, 1, "x"));

        System.out.println("--- dono do resíduo ---");
        conferir("notebook é da Maria", maria.getId().equals(notebook.getUsuarioId()));
        conferir("Maria tem 4 resíduos", residuos.listarDoUsuario(maria.getId()).size() == 4);
        conferir("João tem 2 resíduos", residuos.listarDoUsuario(joao.getId()).size() == 2);
        conferir("Maria não vê o resíduo do João",
                residuos.listarDoUsuario(maria.getId()).stream().noneMatch(x -> x.getId().equals(pilhas.getId())));
        esperaAcessoNegado("João não edita resíduo da Maria",
                () -> residuos.editar(joao, cabos.getCodigo(), 3, "hack"));
        esperaAcessoNegado("João não exclui resíduo da Maria",
                () -> residuos.excluir(joao, cabos.getCodigo()));

        residuos.editar(maria, cabos.getCodigo(), 7, "Cabos USB, carregadores, mouse e teclado");
        conferir("editar muda a quantidade", residuos.buscarPorId(cabos.getId()).getQuantidade() == 7);
        esperaErro("editar com quantidade 0 dá erro", () -> residuos.editar(maria, cabos.getCodigo(), 0, "x"));

        // ================================================================
        System.out.println("=== SOLICITAÇÃO + RN13 ===");
        Solicitacao sNotebook = solicitacoes.solicitar(maria, notebook.getId());
        conferir("solicitação nasce PENDENTE", sNotebook.getStatus() == StatusSolicitacao.PENDENTE);
        conferir("resíduo vira EM_SOLICITACAO",
                residuos.buscarPorId(notebook.getId()).getStatus() == StatusResiduo.EM_SOLICITACAO);
        conferir("notebook some dos disponíveis", naoContem(residuos, notebook));
        conferir("notebook some dos disponíveis da Maria",
                residuos.listarDisponiveisDoUsuario(maria.getId()).stream().noneMatch(x -> x.getId().equals(notebook.getId())));
        esperaErro("editar resíduo em solicitação dá erro",
                () -> residuos.editar(maria, notebook.getCodigo(), 2, "x"));
        conferir("celular continua nos disponíveis", !naoContem(residuos, celular));

        esperaErro("pedir o mesmo resíduo de novo dá erro", () -> solicitacoes.solicitar(maria, notebook.getId()));
        esperaAcessoNegado("João não pede descarte do resíduo da Maria", () -> solicitacoes.solicitar(joao, celular.getId()));
        esperaErro("solicitante null dá erro", () -> solicitacoes.solicitar(null, celular.getId()));
        esperaErro("resíduo null dá erro", () -> solicitacoes.solicitar(maria, null));

        Solicitacao sCelular = solicitacoes.solicitar(maria, celular.getId());
        Solicitacao sPilhas  = solicitacoes.solicitar(joao, pilhas.getId());
        Solicitacao sTv      = solicitacoes.solicitar(joao, tv.getId());

        conferir("Maria tem pelo menos 2 solicitações", solicitacoes.listarDoUsuario(maria.getId()).size() >= 2);
        conferir("posição do celular vem depois do notebook",
                solicitacoes.posicaoNaFila(sCelular.getId()) > solicitacoes.posicaoNaFila(sNotebook.getId()));

        System.out.println("--- cancelar ---");
        esperaAcessoNegado("João não cancela pedido da Maria", () -> solicitacoes.cancelar(joao, sCelular.getId()));
        solicitacoes.cancelar(maria, sCelular.getId());
        conferir("dona cancela: vira CANCELADA", sCelular.getStatus() == StatusSolicitacao.CANCELADA);
        conferir("cancelar devolve o celular pra DISPONIVEL",
                residuos.buscarPorId(celular.getId()).getStatus() == StatusResiduo.DISPONIVEL);
        conferir("celular volta pros disponíveis", !naoContem(residuos, celular));

        solicitacoes.cancelar(gerente, sTv.getId());
        conferir("gerente cancela pedido de outro", sTv.getStatus() == StatusSolicitacao.CANCELADA);
        conferir("TV volta pra DISPONIVEL",
                residuos.buscarPorId(tv.getId()).getStatus() == StatusResiduo.DISPONIVEL);

        Solicitacao sCelular2 = solicitacoes.solicitar(maria, celular.getId());
        conferir("resíduo cancelado pode ser pedido de novo", sCelular2.getStatus() == StatusSolicitacao.PENDENTE);

        System.out.println("--- rota e conclusão ---");
        esperaAcessoNegado("usuário comum não pega da fila", () -> solicitacoes.pegarProxima(maria));
        esperaAcessoNegado("usuário comum não conclui", () -> solicitacoes.concluir(maria, sNotebook.getId()));
        esperaErro("concluir sem estar em rota dá erro", () -> solicitacoes.concluir(coletor, sPilhas.getId()));

        // a fila é FIFO: as pendentes antigas do JSON saem antes, então pega até chegar no notebook
        pegarAte(solicitacoes, coletor, sNotebook);
        conferir("pegarProxima põe o notebook EM_ROTA", sNotebook.getStatus() == StatusSolicitacao.EM_ROTA);

        solicitacoes.concluir(coletor, sNotebook.getId());
        conferir("concluir vira CONCLUIDA", sNotebook.getStatus() == StatusSolicitacao.CONCLUIDA);
        conferir("notebook vira DESCARTADO",
                residuos.buscarPorId(notebook.getId()).getStatus() == StatusResiduo.DESCARTADO);
        conferir("notebook descartado não volta pros disponíveis", naoContem(residuos, notebook));
        esperaErro("concluir duas vezes dá erro", () -> solicitacoes.concluir(coletor, sNotebook.getId()));
        esperaErro("pedir resíduo descartado dá erro", () -> solicitacoes.solicitar(maria, notebook.getId()));

        esperaErro("gerente não cancela solicitação concluída", () -> solicitacoes.cancelar(gerente, sNotebook.getId()));
        esperaErro("dona não cancela de novo uma cancelada", () -> solicitacoes.cancelar(maria, sCelular.getId()));

        pegarAte(solicitacoes, coletor, sPilhas);
        esperaErro("usuário não cancela pedido em rota", () -> solicitacoes.cancelar(joao, sPilhas.getId()));
        solicitacoes.cancelar(gerente, sPilhas.getId());
        conferir("gerente cancela pedido em rota", sPilhas.getStatus() == StatusSolicitacao.CANCELADA);
        conferir("pilhas voltam pra DISPONIVEL",
                residuos.buscarPorId(pilhas.getId()).getStatus() == StatusResiduo.DISPONIVEL);

        // ================================================================
        System.out.println("=== PONTOS DE COLETA (ecopontos da Comurg) ===");
        // os 5 ficam salvos no JSON; se já existirem, reaproveita em vez de duplicar
        List<PontoColeta> ecopontos = new ArrayList<>();
        ecopontos.add(garantirPonto(pontos, gerente, "Ecoponto Jardim Guanabara II",
                "Rua GB-5 com Rua GB-6", Bairro.JARDIM_GUANABARA));
        ecopontos.add(garantirPonto(pontos, gerente, "Ecoponto Setor Faiçalville",
                "Av. Nadra Bufaiçal com Av. Madri", Bairro.SETOR_FAICALVILLE));
        ecopontos.add(garantirPonto(pontos, gerente, "Ecoponto Jardim São José",
                "Rua Frei Nazareno Confaloni com Rua Irmã Maria Bernarda", Bairro.JARDIM_SAO_JOSE));
        ecopontos.add(garantirPonto(pontos, gerente, "Ecoponto Residencial Campos Dourados",
                "Rua São João Del Rei, APM 07", Bairro.RESIDENCIAL_CAMPOS_DOURADOS));
        ecopontos.add(garantirPonto(pontos, gerente, "Ecoponto Parque Eldorado Oeste",
                "Rua Elo, 43", Bairro.PARQUE_ELDORADO_OESTE));

        for (PontoColeta p : ecopontos) {
            conferir("gerente cadastra: " + p.getNome(), pontos.buscarPorId(p.getId()) != null);
        }
        conferir("listar traz os 5 ecopontos", contemTodosPontos(pontos, ecopontos));

        esperaAcessoNegado("usuário não cadastra",
                () -> pontos.cadastrar(maria, "X", "Y", Bairro.SETOR_CENTRAL));
        esperaAcessoNegado("funcionário não cadastra",
                () -> pontos.cadastrar(coletor, "X", "Y", Bairro.SETOR_CENTRAL));
        esperaErro("usuário null dá erro",
                () -> pontos.cadastrar(null, "X", "Y", Bairro.SETOR_CENTRAL));
        esperaErro("nome vazio dá erro",
                () -> pontos.cadastrar(gerente, " ", "Y", Bairro.SETOR_CENTRAL));
        esperaErro("endereço null dá erro",
                () -> pontos.cadastrar(gerente, "X", null, Bairro.SETOR_CENTRAL));
        esperaErro("bairro null dá erro",
                () -> pontos.cadastrar(gerente, "X", "Y", null));

        PontoColeta guanabara = ecopontos.get(0);
        pontos.editar(gerente, guanabara.getId(), null, "Rua GB-5, esquina com GB-6", null);
        PontoColeta editado = pontos.buscarPorId(guanabara.getId());
        conferir("editar só o endereço muda o endereço", editado.getEndereco().equals("Rua GB-5, esquina com GB-6"));
        conferir("editar só o endereço mantém o nome", editado.getNome().equals("Ecoponto Jardim Guanabara II"));
        conferir("editar só o endereço mantém o bairro", editado.getBairro() == Bairro.JARDIM_GUANABARA);

        pontos.editar(gerente, guanabara.getId(), "  Ecoponto Guanabara  ", " ", null);
        editado = pontos.buscarPorId(guanabara.getId());
        conferir("nome novo vem sem espaços nas pontas", editado.getNome().equals("Ecoponto Guanabara"));
        conferir("endereço em branco mantém o antigo", editado.getEndereco().equals("Rua GB-5, esquina com GB-6"));

        // volta o Guanabara pro nome e endereço reais
        pontos.editar(gerente, guanabara.getId(), "Ecoponto Jardim Guanabara II", "Rua GB-5 com Rua GB-6", null);
        conferir("Guanabara voltou ao nome real",
                pontos.buscarPorId(guanabara.getId()).getNome().equals("Ecoponto Jardim Guanabara II"));

        esperaAcessoNegado("usuário não edita",
                () -> pontos.editar(maria, guanabara.getId(), "Hack", null, null));
        esperaAcessoNegado("funcionário não exclui", () -> pontos.excluir(coletor, guanabara.getId()));
        esperaErro("buscar ponto com id null dá erro", () -> pontos.buscarPorId(null));

        // testa o excluir num ponto temporário, sem mexer nos 5 reais
        PontoColeta temporario = pontos.cadastrar(gerente, "Ponto Temporário de Teste", "Rua Teste, 1", Bairro.SETOR_CENTRAL);
        pontos.excluir(gerente, temporario.getId());
        esperaErro("depois de excluir não acha mais", () -> pontos.buscarPorId(temporario.getId()));
        esperaErro("excluir de novo dá erro", () -> pontos.excluir(gerente, temporario.getId()));

        esperaErro("usuário sem bairro dá erro", () -> new Usuario("Sem Bairro", "529.982.247-25",
                "sem@teste.com", "senha123", CargoUsuario.USUARIO, "10/05/2000", null));

        // ================================================================
        System.out.println("=== GRAFO ===");
        Grafo<Bairro> grafo = GrafoBairros.criar();
        conferir("grafo foi criado", grafo != null);

        Dijkstra<Bairro> dijkstra = new Dijkstra<>(grafo);
        Map<Bairro, Double> d = dijkstra.calcular(Bairro.PARQUE_AMAZONIA);
        conferir("Pq Amazônia -> Campos Dourados = 15.0 (não 15.3)",
                Math.abs(d.get(Bairro.RESIDENCIAL_CAMPOS_DOURADOS) - 15.0) < 0.01);

        Map<Bairro, Double> c = dijkstra.calcular(Bairro.SETOR_CENTRAL);
        conferir("Central -> Pedro Ludovico = 7.1 (pelo Marista)",
                Math.abs(c.get(Bairro.SETOR_PEDRO_LUDOVICO) - 7.1) < 0.01);
        conferir("Central -> Central = 0", c.get(Bairro.SETOR_CENTRAL) == 0.0);

        System.out.println("=== PONTO MAIS PRÓXIMO ===");
        conferir("Maria (Bueno) -> Faiçalville",
                pontos.pontoMaisProximo(maria).getBairro() == Bairro.SETOR_FAICALVILLE);
        conferir("João (Jd Curitiba) -> Jardim Guanabara",
                pontos.pontoMaisProximo(joao).getBairro() == Bairro.JARDIM_GUANABARA);
        conferir("Carlos (Central) -> Jardim Guanabara",
                pontos.pontoMaisProximo(coletor).getBairro() == Bairro.JARDIM_GUANABARA);
        conferir("Ana (Pq Amazônia) -> Faiçalville",
                pontos.pontoMaisProximo(gerente).getBairro() == Bairro.SETOR_FAICALVILLE);
        esperaErro("usuário null dá erro", () -> pontos.pontoMaisProximo(null));

        // ================================================================
        System.out.println("=== FILA DEPOIS DE REINICIAR ===");
        // cria 4 pedidos novos, "reinicia" o sistema (repository e service novos, lendo o JSON)
        // e confere se a ordem da fila continua a mesma
        Residuo f1 = residuos.salvar(joao, TipoResiduo.CABOS, 1, "Fila teste 1");
        Residuo f2 = residuos.salvar(joao, TipoResiduo.CABOS, 1, "Fila teste 2");
        Residuo f3 = residuos.salvar(joao, TipoResiduo.CABOS, 1, "Fila teste 3");
        Residuo f4 = residuos.salvar(joao, TipoResiduo.CABOS, 1, "Fila teste 4");
        Solicitacao[] emOrdem = {
                solicitacoes.solicitar(joao, f1.getId()), solicitacoes.solicitar(joao, f2.getId()),
                solicitacoes.solicitar(joao, f3.getId()), solicitacoes.solicitar(joao, f4.getId())};
        GerenciarSolicitacoesService reiniciado = new GerenciarSolicitacoesService(
                new GerenciarSolicitacoesRepository(), residuos, inst.getLogService());
        boolean ordemMantida = true;
        for (int i = 1; i < emOrdem.length; i++) {
            if (reiniciado.posicaoNaFila(emOrdem[i].getId()) < reiniciado.posicaoNaFila(emOrdem[i - 1].getId()))
                ordemMantida = false;
        }
        conferir("a fila mantém a ordem de chegada depois de reiniciar", ordemMantida);
        // limpa: cancela os 4 pedidos de teste
        for (Solicitacao s : emOrdem) solicitacoes.cancelar(gerente, s.getId());

        // ================================================================
        System.out.println("=== USUÁRIOS ===");
        GerenciarUsuariosService usuarios = inst.getUsuariosService();
        LogarUsuario login = new LogarUsuario(usuarios);
        // se uma rodada anterior parou no meio, apaga os usuários de teste antes
        apagarSeExistir(usuarios, gerente, "714.602.380-01");
        apagarSeExistir(usuarios, gerente, "865.204.171-73");
        apagarSeExistir(usuarios, gerente, "302.918.547-88");

        Usuario pedro = usuarios.cadastrar("714.602.380-01", "Pedro Teste", "pedro@teste.com",
                "Senha2026", "01/01/2001", Bairro.SETOR_BUENO);
        conferir("cadastro aberto cria com cargo USUARIO", pedro.getCargoUsuario() == CargoUsuario.USUARIO);
        conferir("CPF fica só com dígitos", pedro.getCpf().equals("71460238001"));
        conferir("senha não fica salva em texto puro", !pedro.getSenhaCriptografada().equals("Senha2026"));
        conferir("bairro fica salvo", pedro.getBairro() == Bairro.SETOR_BUENO);

        esperaErro("CPF repetido dá erro", () -> usuarios.cadastrar("714.602.380-01", "Outro",
                "outro@teste.com", "x", "01/01/2001", Bairro.SETOR_BUENO));
        esperaErro("e-mail repetido dá erro", () -> usuarios.cadastrar("458.217.036-62", "Outro",
                "pedro@teste.com", "x", "01/01/2001", Bairro.SETOR_BUENO));
        esperaErro("cadastro sem bairro dá erro", () -> usuarios.cadastrar("458.217.036-62", "Outro",
                "outro@teste.com", "x", "01/01/2001", null));
        esperaErro("CPF inválido dá erro", () -> usuarios.cadastrar("111.111.111-12", "Outro",
                "outro@teste.com", "x", "01/01/2001", Bairro.SETOR_BUENO));
        esperaErro("data em formato errado dá erro", () -> usuarios.cadastrar("458.217.036-62", "Outro",
                "outro@teste.com", "x", "2001-01-01", Bairro.SETOR_BUENO));
        esperaErro("e-mail inválido dá erro", () -> usuarios.cadastrar("458.217.036-62", "Outro",
                "sem-arroba", "x", "01/01/2001", Bairro.SETOR_BUENO));

        System.out.println("--- login ---");
        conferir("login com senha certa devolve o usuário",
                login.logarUsuario("pedro@teste.com", "Senha2026").getId().equals(pedro.getId()));
        esperaErro("login com senha errada dá erro", () -> login.logarUsuario("pedro@teste.com", "senha2026"));
        esperaErro("login com e-mail que não existe dá erro", () -> login.logarUsuario("ninguem@teste.com", "x"));
        esperaErro("login com senha vazia dá erro", () -> login.logarUsuario("pedro@teste.com", " "));

        System.out.println("--- editar ---");
        usuarios.editar(pedro, "Pedro Teste Editado", null, null, null);
        Usuario pedroEditado = usuarios.buscar(pedro.getCpf());
        conferir("editar só o nome muda o nome", pedroEditado.getNomeCompleto().equals("Pedro Teste Editado"));
        conferir("editar só o nome mantém o e-mail", pedroEditado.getEmail().equals("pedro@teste.com"));
        conferir("editar só o nome mantém o bairro", pedroEditado.getBairro() == Bairro.SETOR_BUENO);
        usuarios.editar(pedro, null, null, "NovaSenha1", Bairro.SETOR_CENTRAL);
        conferir("depois de trocar a senha, a nova funciona no login",
                login.logarUsuario("pedro@teste.com", "NovaSenha1") != null);
        esperaErro("depois de trocar a senha, a antiga não funciona",
                () -> login.logarUsuario("pedro@teste.com", "Senha2026"));
        conferir("editar o bairro muda o bairro", usuarios.buscar(pedro.getCpf()).getBairro() == Bairro.SETOR_CENTRAL);

        System.out.println("--- cargos ---");
        esperaAcessoNegado("usuário comum não cadastra funcionário", () -> usuarios.cadastroGerente(maria,
                "865.204.171-73", "Func Teste", "func@teste.com", "x", CargoUsuario.FUNCIONARIO, "01/01/1999", Bairro.SETOR_CENTRAL));
        Usuario funcionario = usuarios.cadastroGerente(gerente, "865.204.171-73", "Func Teste",
                "func@teste.com", "Func2026", CargoUsuario.FUNCIONARIO, "01/01/1999", Bairro.SETOR_CENTRAL);
        conferir("gerente cadastra funcionário", funcionario.getCargoUsuario() == CargoUsuario.FUNCIONARIO);

        esperaAcessoNegado("funcionário não promove", () -> usuarios.promoverUsuario(funcionario, pedro.getCpf(), CargoUsuario.GERENTE));
        usuarios.promoverUsuario(gerente, pedro.getCpf(), CargoUsuario.FUNCIONARIO);
        conferir("gerente promove pelo CPF", usuarios.buscar(pedro.getCpf()).getCargoUsuario() == CargoUsuario.FUNCIONARIO);
        esperaErro("promover sem cargo dá erro", () -> usuarios.promoverUsuario(gerente, pedro.getCpf(), null));

        Usuario gerente2 = usuarios.cadastroGerente(gerente, "302.918.547-88", "Gerente Teste",
                "gerente2@teste.com", "Ger2026", CargoUsuario.GERENTE, "01/01/1985", Bairro.SETOR_CENTRAL);
        esperaErro("gerente não muda o próprio cargo",
                () -> usuarios.promoverUsuario(gerente2, gerente2.getCpf(), CargoUsuario.USUARIO));
        esperaErro("gerente não remove a si mesmo", () -> usuarios.remover(gerente2, gerente2.getCpf()));
        esperaAcessoNegado("funcionário não remove usuário", () -> usuarios.remover(funcionario, pedro.getCpf()));

        // ================================================================
        System.out.println("=== LOG DE AUDITORIA ===");
        GerenciarLogService logService = inst.getLogService();
        RegistroLog topo = logService.listar(gerente).get(0);
        conferir("a última ação (cadastrar gerente) está no topo", topo.getAcaoLog() == AcaoLog.CADASTROU_USUARIO);
        conferir("o registro guarda quem fez", topo.getNomeUsuario().equals("Ana Gerente")
                && topo.getUsuarioId().equals(gerente.getId()));
        conferir("o registro guarda o alvo", topo.getAlvo().contains("30291854788"));
        conferir("a data é preenchida sozinha", topo.getDataRegistro() != null);

        int antes = logService.listar(gerente).size();
        PontoColeta pontoLog = pontos.cadastrar(gerente, "Ponto do Teste de Log", "Rua Log, 1", Bairro.SETOR_CENTRAL);
        List<RegistroLog> depois = logService.listar(gerente);
        conferir("cadastrar ponto aumenta o log em 1", depois.size() == antes + 1);
        conferir("cadastrar ponto fica no topo como CADASTROU_PONTO", depois.get(0).getAcaoLog() == AcaoLog.CADASTROU_PONTO);
        pontos.excluir(gerente, pontoLog.getId());
        conferir("excluir ponto vira EXCLUIU_PONTO no topo",
                logService.listar(gerente).get(0).getAcaoLog() == AcaoLog.EXCLUIU_PONTO);

        boolean maisNovoPrimeiro = true;
        List<RegistroLog> todosLog = logService.listar(gerente);
        for (int i = 1; i < todosLog.size(); i++)
            if (todosLog.get(i).getDataRegistro().isAfter(todosLog.get(i - 1).getDataRegistro())) maisNovoPrimeiro = false;
        conferir("o log sai do mais novo para o mais antigo", maisNovoPrimeiro);

        int antesDoUsuario = logService.listar(gerente).size();
        residuos.salvar(maria, TipoResiduo.CABOS, 1, "Resíduo que não vai pro log");
        conferir("ação de usuário comum não entra no log", logService.listar(gerente).size() == antesDoUsuario);

        esperaAcessoNegado("usuário comum não vê o log", () -> logService.listar(maria));
        esperaAcessoNegado("funcionário não vê o log", () -> logService.listar(coletor));
        esperaErro("registrar sem quem dá erro", () -> logService.registrar(null, AcaoLog.EDITOU_PONTO, "x"));
        esperaErro("registrar sem ação dá erro", () -> logService.registrar(gerente, null, "x"));

        // relê o arquivo: testa o adaptador de LocalDateTime e a ordem da pilha
        RegistroLog topoAntes = logService.listar(gerente).get(0);
        RegistroLog topoRelido = new GerenciarLogRepository().listar().get(0);
        conferir("depois de reler o banco, o topo é o mesmo", topoRelido.getId().equals(topoAntes.getId()));
        conferir("a data e hora volta igual do MySQL", topoRelido.getDataRegistro().equals(topoAntes.getDataRegistro()));

        // limpa os usuários de teste (o gerente remove)
        usuarios.remover(gerente, funcionario.getCpf());
        usuarios.remover(gerente, gerente2.getCpf());
        usuarios.remover(gerente, pedro.getCpf());
        esperaErro("usuário removido não é mais encontrado", () -> usuarios.buscar(pedro.getCpf()));
        conferir("remover entra no log como REMOVEU_USUARIO",
                logService.listar(gerente).get(0).getAcaoLog() == AcaoLog.REMOVEU_USUARIO);

        // ================================================================
        System.out.println("=== CIFRA AFIM (alfabeto de 62) ===");
        CifraAfim afim = new CifraAfim();
        String[] mensagens = {"Senha2026", "EcoCode", "abcXYZ019", "Reciclar Eletronicos!", "zzZZ99"};
        for (String m : mensagens) {
            String cifrada = afim.cifraAfim(m);
            conferir("cifrar e decifrar volta o original: " + m, afim.descriptografarMensagemAfim(cifrada).equals(m));
            conferir("a cifrada é diferente do original: " + m, !cifrada.equals(m));
        }
        conferir("espaço e pontuação passam sem mudar",
                afim.cifraAfim("a b!").charAt(1) == ' ' && afim.cifraAfim("a b!").charAt(3) == '!');
        conferir("os dígitos também são cifrados", !afim.cifraAfim("0123456789").equals("0123456789"));
        conferir("a mesma senha gera sempre o mesmo texto (por isso não é hash)",
                afim.cifraAfim("Senha2026").equals(afim.cifraAfim("Senha2026")));
        esperaErro("cifrar null dá erro", () -> afim.cifraAfim(null));

        System.out.println("--- aritmética ---");
        conferir("mdc(7, 62) = 1", Aritmetica.mdc(7, 62) == 1);
        conferir("mdc(31, 62) = 31", Aritmetica.mdc(31, 62) == 31);
        conferir("inverso de 7 mod 62 = 9", Aritmetica.inversoModular(7, 62) == 9);
        conferir("31 não tem inverso mod 62", !Aritmetica.possuiInverso(31, 62));
        esperaErro("inverso de 2 mod 62 dá erro", () -> Aritmetica.inversoModular(2, 62));
        int valoresDeA = 0;
        for (int a = 1; a < 62; a++) if (Aritmetica.mdc(a, 62) == 1) valoresDeA++;
        conferir("30 valores de a (phi(62) = 30)", valoresDeA == 30);
        conferir("30 x 62 = 1.860 chaves", valoresDeA * 62 == 1860);

        // ================================================================
        System.out.println("=== ÁRVORE AVL ===");
        ArvoreAVL<Integer, String> avl = new ArvoreAVL<>();
        conferir("árvore nova está vazia", avl.estaVazia() && avl.tamanho() == 0 && avl.altura() == 0);
        for (int i = 1; i <= 1000; i++) avl.inserir(i, "valor " + i);   // em ordem: o pior caso sem balanceamento
        conferir("1000 inserções em ordem: tamanho 1000", avl.tamanho() == 1000);
        conferir("1000 inserções em ordem: altura no máximo 14 (sem AVL seria 1000)", avl.altura() <= 14);
        conferir("buscar 500 acha", "valor 500".equals(avl.buscar(500)));
        conferir("buscar 1001 não acha", avl.buscar(1001) == null);
        conferir("emOrdem sai ordenado", estaOrdenado(avl.emOrdem()));

        avl.inserir(500, "novo 500");
        conferir("chave repetida só troca o valor", "novo 500".equals(avl.buscar(500)) && avl.tamanho() == 1000);

        avl.remover(1);      // folha ou um filho
        avl.remover(500);    // nó do meio, com dois filhos
        avl.remover(5000);   // não existe: não faz nada
        conferir("remover tira do tamanho só o que existia", avl.tamanho() == 998);
        conferir("removido não é mais achado", !avl.contem(1) && !avl.contem(500));
        conferir("vizinhos do removido continuam lá", avl.contem(499) && avl.contem(501));
        for (int i = 2; i <= 700; i++) avl.remover(i);
        conferir("depois de remover 700 a árvore continua balanceada", avl.altura() <= 11 && avl.tamanho() == 300);
        conferir("emOrdem continua ordenado depois das remoções", estaOrdenado(avl.emOrdem()));
        esperaErro("inserir chave null dá erro", () -> avl.inserir(null, "x"));
        esperaErro("remover chave null dá erro", () -> avl.remover(null));

        List<Residuo> listaResiduos = residuos.listar();
        boolean porCodigo = true;
        for (int i = 1; i < listaResiduos.size(); i++)
            if (listaResiduos.get(i).getCodigo().compareTo(listaResiduos.get(i - 1).getCodigo()) < 0) porCodigo = false;
        conferir("o repository de resíduos lista pela ordem do código (AVL)", porCodigo);

        // ================================================================
        System.out.println("=== PILHA E FILA ===");
        PilhaEncadeada<String> pilha = new PilhaEncadeada<>();
        pilha.empilhar("A"); pilha.empilhar("B"); pilha.empilhar("C");
        conferir("espiar mostra o último que entrou", pilha.espiar().equals("C"));
        conferir("listar vem do topo para baixo", pilha.listar().equals(List.of("C", "B", "A")));
        conferir("desempilhar tira o último", pilha.desempilhar().equals("C") && pilha.tamanho() == 2);
        pilha.desempilhar(); pilha.desempilhar();
        esperaErro("desempilhar pilha vazia dá erro", pilha::desempilhar);
        esperaErro("espiar pilha vazia dá erro", pilha::espiar);
        esperaErro("empilhar null dá erro", () -> pilha.empilhar(null));

        FilaEncadeada<String> fila = new FilaEncadeada<>();
        fila.enfileirar("A"); fila.enfileirar("B"); fila.enfileirar("C");
        conferir("fila: sai o primeiro que entrou", fila.desenfileirar().equals("A"));
        conferir("fila: espiar mostra o próximo", fila.espiar().equals("B") && fila.tamanho() == 2);
        fila.desenfileirar(); fila.desenfileirar();
        conferir("fila esvazia certinho", fila.estaVazia());
        fila.enfileirar("D");
        conferir("fila volta a funcionar depois de esvaziar", fila.espiar().equals("D"));
        fila.desenfileirar();
        esperaErro("desenfileirar fila vazia dá erro", fila::desenfileirar);

        // ================================================================
        System.out.println("=== DIJKSTRA NUM GRAFO PEQUENO ===");
        Grafo<String> pequeno = new Grafo<>();
        pequeno.adicionarAresta("A", "B", 1);
        pequeno.adicionarAresta("B", "C", 2);
        pequeno.adicionarAresta("A", "C", 5);
        pequeno.adicionarVertice("D");   // sem nenhuma aresta
        Map<String, Double> dp = new Dijkstra<>(pequeno).calcular("A");
        conferir("A -> C pega o caminho por B (3, e não 5)", dp.get("C") == 3.0);
        conferir("vértice sem caminho fica infinito", dp.get("D") == Double.POSITIVE_INFINITY);
        conferir("aresta vale nos dois sentidos", new Dijkstra<>(pequeno).calcular("C").get("A") == 3.0);
        esperaErro("origem fora do grafo dá erro", () -> new Dijkstra<>(pequeno).calcular("Z"));
        esperaErro("aresta negativa dá erro", () -> pequeno.adicionarAresta("A", "D", -1));
        esperaErro("aresta de um vértice nele mesmo dá erro", () -> pequeno.adicionarAresta("A", "A", 1));

        Map<Bairro, Double> todosBairros = new Dijkstra<>(GrafoBairros.criar()).calcular(Bairro.SETOR_CENTRAL);
        conferir("grafo de Goiânia tem 19 bairros", todosBairros.size() == 19);
        conferir("todo bairro é alcançável a partir do Central",
                todosBairros.values().stream().noneMatch(v -> v == Double.POSITIVE_INFINITY));

        System.out.println();
        System.out.println("Passaram: " + ok + " | Falharam: " + falhou);
        if (falhou > 0) throw new AssertionError("Há verificações com falha");
    }

    // ---------------- ajudantes ----------------

    static void apagarSeExistir(GerenciarUsuariosService usuarios, Usuario gerente, String cpf) {
        try {
            usuarios.remover(gerente, cpf);
        } catch (IllegalArgumentException e) {
            // não existia: nada a fazer
        }
    }

    static boolean estaOrdenado(List<String> valores) {
        int anterior = Integer.MIN_VALUE;
        for (String v : valores) {
            int numero = Integer.parseInt(v.replaceAll("\\D", ""));
            if (numero < anterior) return false;
            anterior = numero;
        }
        return true;
    }

    static void pegarAte(GerenciarSolicitacoesService solicitacoes, Usuario coletor, Solicitacao alvo) {
        for (int i = 0; i < 200; i++) {
            Solicitacao pega = solicitacoes.pegarProxima(coletor);
            if (pega.getId().equals(alvo.getId())) {
                return;
            }
        }
    }

    // cadastra o ponto só se ainda não existir um com esse nome
    static PontoColeta garantirPonto(GerenciarPontoColetaService pontos, Usuario gerente,
                                     String nome, String endereco, Bairro bairro) {
        for (PontoColeta p : pontos.listar())
            if (p.getNome().equals(nome)) return p;
        return pontos.cadastrar(gerente, nome, endereco, bairro);
    }

    static boolean naoContem(GerenciarResiduosService residuos, Residuo r) {
        for (Residuo x : residuos.listarDisponiveis())
            if (r.getId().equals(x.getId())) return false;
        return true;
    }

    static boolean contemTodos(GerenciarResiduosService residuos, Residuo[] lista) {
        for (Residuo r : lista)
            if (naoContem(residuos, r)) return false;
        return true;
    }

    static boolean contemTodosPontos(GerenciarPontoColetaService pontos, List<PontoColeta> lista) {
        for (PontoColeta p : lista) {
            boolean achou = false;
            for (PontoColeta x : pontos.listar())
                if (p.getId().equals(x.getId())) achou = true;
            if (!achou) return false;
        }
        return true;
    }

    static void conferir(String nome, boolean passou) {
        if (passou) ok++; else falhou++;
        System.out.println((passou ? "[OK]    " : "[FALHA] ") + nome);
    }

    static void esperaErro(String nome, Runnable acao) {
        try {
            acao.run();
            conferir(nome, false);
        } catch (IllegalArgumentException | IllegalStateException e) {
            conferir(nome, true);
        }
    }

    static void esperaAcessoNegado(String nome, Runnable acao) {
        try {
            acao.run();
            conferir(nome, false);
        } catch (AcessoNegadoException e) {
            conferir(nome, true);
        }
    }
}
