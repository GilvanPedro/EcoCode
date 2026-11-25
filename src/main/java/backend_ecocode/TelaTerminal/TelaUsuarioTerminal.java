package backend_ecocode.TelaTerminal;

import backend_ecocode.Domain.Entities.Sessao;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.Bairro;
import backend_ecocode.Exceptions.AcessoNegadoException;
import backend_ecocode.Service.GerenciarResiduosService;
import backend_ecocode.Service.GerenciarUsuariosService;
import backend_ecocode.Service.SessaoService;
import backend_ecocode.Util.LogarUsuario;

import java.util.Scanner;

// tela de entrada: entra direto pela sessão salva, ou deixa a pessoa entrar / criar conta
public class TelaUsuarioTerminal {

    // único Scanner do programa; é repassado para as outras telas
    private final Scanner sc = new Scanner(System.in);
    private final LogarUsuario logar;
    private final GerenciarUsuariosService usuariosService;
    private final GerenciarResiduosService residuosService;
    private final SessaoService sessaoService;

    public TelaUsuarioTerminal(LogarUsuario logar,
                               GerenciarUsuariosService usuariosService,
                               GerenciarResiduosService residuosService,
                               SessaoService sessaoService) {
        this.logar = logar;
        this.usuariosService = usuariosService;
        this.residuosService = residuosService;
        this.sessaoService = sessaoService;
    }

    public void telaPrincipalUsuario() {
        Usuario usuarioLogado = entrarPelaSessao();

        if (usuarioLogado == null) {
            usuarioLogado = telaInicial();
            perguntarSeMantemConectado(usuarioLogado);
        }

        System.out.println("\nBem-vindo(a), " + usuarioLogado.getNomeCompleto());

        TelaCadastroResiduoTerminal telaResiduo = new TelaCadastroResiduoTerminal(
                sc, usuarioLogado, residuosService, usuariosService
        );
        telaResiduo.telaResiduo();
    }

    // Se tem sessão válida salva, devolve o usuário dela. Se não, devolve null.
    private Usuario entrarPelaSessao() {
        Sessao sessao = sessaoService.buscarValida();
        if (sessao == null) return null;

        Usuario usuario = usuariosService.buscarPorId(sessao.getUsuarioId());
        if (usuario == null) sessaoService.encerrar(); // usuário foi apagado
        return usuario;
    }

    // Primeiro menu: entrar ou criar conta. Só sai daqui quando alguém fizer login.
    private Usuario telaInicial() {
        while (true) {
            System.out.println("\n--- ECOCODE ---");
            System.out.println("1 - Entrar");
            System.out.println("2 - Criar conta");
            System.out.print("Escolha: ");
            String opcao = sc.nextLine().trim();

            if (opcao.equals("1")) return pedirLogin();
            if (opcao.equals("2")) criarConta();
            else System.out.println("Opção inválida!");
        }
    }

    // Pede e-mail e senha até acertar
    private Usuario pedirLogin() {
        Usuario usuarioLogado = null;

        while (usuarioLogado == null) {
            System.out.println("\n--- FAÇA O LOGIN ---");
            System.out.print("Informe o seu email: ");
            String email = sc.nextLine();
            System.out.print("Informe a sua senha: ");
            String senha = sc.nextLine();

            try {
                usuarioLogado = logar.logarUsuario(email, senha);
            } catch (IllegalArgumentException | AcessoNegadoException e) {
                // mesma mensagem para e-mail ou senha errados, para não revelar quais e-mails existem
                System.out.println("Falha no Login, verifique o email e senha digitados");
            }
        }
        return usuarioLogado;
    }

    // Cadastro público: toda conta nova começa como USUARIO
    private void criarConta() {
        System.out.println("\n--- CRIAR CONTA ---");
        System.out.print("Nome completo: ");
        String nome = sc.nextLine();
        System.out.print("CPF: ");
        String cpf = sc.nextLine();
        System.out.print("E-mail: ");
        String email = sc.nextLine();
        System.out.print("Senha: ");
        String senha = sc.nextLine();
        System.out.print("Data de nascimento (DD/MM/AAAA): ");
        String dataNascimento = sc.nextLine();

        Bairro bairro = lerBairro();
        if (bairro == null) {
            System.out.println("\nBairro inválido. Cadastro cancelado.");
            return;
        }

        try {
            Usuario novo = usuariosService.cadastrar(cpf, nome, email, senha, dataNascimento, bairro);
            System.out.println("\nConta criada! Agora entre com o e-mail " + novo.getEmail());
        } catch (IllegalArgumentException e) {
            System.out.println("\nNão foi possível criar a conta: " + e.getMessage());
        }
    }

    // Mostra os bairros numerados e devolve o escolhido, ou null se a opção não existir
    private Bairro lerBairro() {
        Bairro[] bairros = Bairro.values();
        for (int i = 0; i < bairros.length; i++) {
            System.out.println((i + 1) + " - " + bairros[i].getDescricao());
        }
        System.out.print("Escolha o seu bairro: ");

        try {
            int opcao = Integer.parseInt(sc.nextLine().trim());
            if (opcao < 1 || opcao > bairros.length) return null;
            return bairros[opcao - 1];
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // O "Manter conectado por 30 dias" da tela de login do front, na versão terminal
    private void perguntarSeMantemConectado(Usuario usuario) {
        System.out.print("Manter conectado por 30 dias? (s/n): ");
        String resposta = sc.nextLine().trim();

        if (resposta.equalsIgnoreCase("s")) {
            sessaoService.criar(usuario.getId());
        }
    }
}
