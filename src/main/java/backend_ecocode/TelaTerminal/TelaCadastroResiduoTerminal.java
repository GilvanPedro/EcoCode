package backend_ecocode.TelaTerminal;

import backend_ecocode.Domain.Enums.CargoUsuario;
import backend_ecocode.Domain.Entities.Residuo;
import backend_ecocode.Domain.Enums.TipoResiduo;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Exceptions.AcessoNegadoException;
import backend_ecocode.Service.GerenciarResiduosService;
import backend_ecocode.Service.GerenciarUsuariosService;
import backend_ecocode.Service.SessaoService;

import java.util.List;
import java.util.Scanner;

public class TelaCadastroResiduoTerminal {

    private final Scanner sc;
    private final GerenciarResiduosService service;
    private final Usuario usuarioLogado;
    private final TelaGerenciarUsuariosTerminal telaUsuarios;
    private final SessaoService sessaoService;

    // recebe o Scanner da tela de login e o usuário que entrou
    public TelaCadastroResiduoTerminal(Scanner sc, Usuario usuarioLogado, GerenciarResiduosService residuosService,
                                       GerenciarUsuariosService usuariosService, SessaoService sessaoService) {
        this.sc = sc;
        this.usuarioLogado = usuarioLogado;
        this.service = residuosService;
        this.telaUsuarios = new TelaGerenciarUsuariosTerminal(sc, usuarioLogado, usuariosService);
        this.sessaoService = sessaoService;
    }

    public void telaResiduo(){

        // o cargo não muda durante a sessão, então basta conferir uma vez
        boolean ehGerente = usuarioLogado.getCargoUsuario() == CargoUsuario.GERENTE;

        int opcao;

        do {

            System.out.println("\n================================");
            System.out.println("RECICLA +");
            System.out.println("(Menos lixo e um mundo melhor)");
            System.out.println("================================");
            System.out.println("Usuário: " + usuarioLogado.getNomeCompleto()
                    + " (" + usuarioLogado.getCargoUsuario() + ")");

            System.out.println("\n1 - Cadastrar um resíduo");
            System.out.println("2 - Verificar lista de resíduos");
            System.out.println("3 - Verificar quantidade de resíduos");
            System.out.println("4 - Listar resíduos por categoria");
            System.out.println("5 - Excluir um resíduo");
            System.out.println("6 - Editar um resíduo");

            if (ehGerente) {
                System.out.println("8 - Cadastrar um usuário");
                System.out.println("9 - Listar usuários");
            }

            System.out.println("7 - Sair (continua conectado)");
            System.out.println("10 - Sair da conta");

            System.out.print("\nEscolha uma opção: ");
            opcao = lerInteiro();

            switch (opcao) {

                case 1:
                    telaCadastrarResiduo();
                    break;

                case 2:
                    listar_residuos();
                    break;

                case 3:
                    System.out.println("\n--- QUANTIDADE ---");
                    System.out.println("Quantidade de cadastros: " + service.quantidadeResiduosCadastrados());
                    System.out.println("Quantidade total de resíduos: " + service.quantidadeTotalResiduos());
                    break;

                case 4:
                    listarCategoriaResiduo();
                    break;

                case 5:
                    excluirResiduo();
                    break;

                case 6:
                    editarResiduo();
                    break;

                case 7:
                    System.out.println("\nEncerrando o programa...");
                    break;

                case 8:
                    if (ehGerente) telaUsuarios.cadastrarUsuario();
                    else System.out.println("\nOpção inválida!");
                    break;

                case 9:
                    if (ehGerente) telaUsuarios.listarUsuarios();
                    else System.out.println("\nOpção inválida!");
                    break;

                case 10:
                    sairDaConta();
                    break;

                default:
                    System.out.println("\nOpção inválida!");
            }
        } while (opcao != 7 && opcao != 10);
    }

    // apaga a sessão salva: na próxima vez o programa pede login de novo
    private void sairDaConta() {
        sessaoService.encerrar();
        System.out.println("\nVocê saiu da conta. Até a próxima!");
    }

    // lê uma linha e converte para número; devolve -1 se não for número
    private int lerInteiro() {
        String linha = sc.nextLine().trim();
        try {
            return Integer.parseInt(linha);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void telaCadastrarResiduo() {

        System.out.println("\n--- CADASTRO DE RESÍDUO ---");

        TipoResiduo tipoSelecionado = lerTipoResiduo();

        if (tipoSelecionado == null) {
            System.out.println("\nOpção inválida. Cadastro cancelado.");
            return;
        }

        System.out.print("Qual a quantidade? ");
        int quantidade = lerInteiro();

        System.out.print("Descrição (opcional, aperte Enter para pular): ");
        String descricao = sc.nextLine();

        try {
            Residuo residuo = service.salvar(usuarioLogado, tipoSelecionado, quantidade, descricao);

            System.out.println("\nResíduo cadastrado!");
            System.out.println("Código: " + residuo.getCodigo());
            System.out.println("Categoria: " + residuo.getCategoriaResiduo());
            System.out.println("Quantidade: " + residuo.getQuantidade());

            if (!residuo.getDescricao().isEmpty()) {
                System.out.println("Descrição: " + residuo.getDescricao());
            }

        } catch (IllegalArgumentException e) {
            System.out.println("\nNão foi possível cadastrar: " + e.getMessage());
        }
    }

    public void editarResiduo() {

        System.out.println("\n--- EDITAR RESÍDUO ---");
        System.out.print("Digite o código do resíduo: ");
        String codigo = sc.nextLine();

        try {
            Residuo residuo = service.buscar(codigo);

            System.out.println("\nCódigo: " + residuo.getCodigo());
            System.out.println("Categoria: " + residuo.getCategoriaResiduo());
            System.out.println("Quantidade: " + residuo.getQuantidade());
            if (!residuo.getDescricao().isEmpty()) {
                System.out.println("Descrição: " + residuo.getDescricao());
            }

            System.out.print("\nEsse é o resíduo que deseja editar? (s/n): ");
            String resposta = sc.nextLine();

            if (!resposta.equalsIgnoreCase("s")) {
                System.out.println("Edição cancelada.");
                return;
            }

            // Enter vazio mantém o valor atual
            System.out.print("Nova quantidade (Enter para manter " + residuo.getQuantidade() + "): ");
            String quantidadeDigitada = sc.nextLine().trim();

            int quantidade = residuo.getQuantidade();
            if (!quantidadeDigitada.isEmpty()) {
                quantidade = Integer.parseInt(quantidadeDigitada);
            }

            System.out.print("Nova descrição (Enter para manter a atual): ");
            String descricaoDigitada = sc.nextLine();

            String descricao = residuo.getDescricao();
            if (!descricaoDigitada.isBlank()) {
                descricao = descricaoDigitada;
            }

            Residuo editado = service.editar(usuarioLogado, codigo, quantidade, descricao);

            System.out.println("\nResíduo editado!");
            System.out.println("Código: " + editado.getCodigo());
            System.out.println("Categoria: " + editado.getCategoriaResiduo());
            System.out.println("Quantidade: " + editado.getQuantidade());
            if (!editado.getDescricao().isEmpty()) {
                System.out.println("Descrição: " + editado.getDescricao());
            }

        } catch (NumberFormatException e) {
            System.out.println("\nNão foi possível editar o resíduo: a quantidade deve ser um número.");
        } catch (IllegalArgumentException | AcessoNegadoException e) {
            System.out.println("\nNão foi possível editar o resíduo: " + e.getMessage());
        }
    }

    public void listar_residuos() {

        System.out.println("\n--- LISTA DE RESÍDUOS ---");
        System.out.println("1 - Ordem de cadastro");
        System.out.println("2 - Ordem de código");
        System.out.print("Escolha: ");
        String ordem = sc.nextLine().trim();

        // o gerente vê todos; os outros só veem os próprios resíduos
        List<Residuo> lista;
        if (usuarioLogado.getCargoUsuario() == CargoUsuario.GERENTE) {
            lista = ordem.equals("2") ? service.listarPorCodigo() : service.listar();
        } else {
            lista = service.listarDoUsuario(usuarioLogado.getId());
            if (ordem.equals("2")) {
                lista.sort(java.util.Comparator.comparingInt(r -> Integer.parseInt(r.getCodigo())));
            }
        }

        if (lista.isEmpty()) {
            System.out.println("Nenhum resíduo cadastrado.");
            return;
        }

        for (Residuo residuo : lista) {
            System.out.println("-------------------------");
            System.out.println("Código: " + residuo.getCodigo());
            System.out.println("Resíduo: " + residuo.getCategoriaResiduo());
            System.out.println("Quantidade: " + residuo.getQuantidade());
            System.out.println("Descrição: " + residuo.getDescricao());
        }

        System.out.println("-------------------------");
    }

    // mostra o menu de tipos e devolve o escolhido, ou null se a opção não existir
    private TipoResiduo lerTipoResiduo() {

        System.out.println("Selecione o tipo de resíduo eletrônico:");
        for (TipoResiduo tipo : TipoResiduo.values()) {
            System.out.println("[" + tipo.getResto() + "] - " + tipo.getDescricao());
        }

        System.out.print("Digite o número da opção: ");
        int opcaoDigitada = lerInteiro();

        for (TipoResiduo tipo : TipoResiduo.values()) {
            if (tipo.getResto() == opcaoDigitada) {
                return tipo;
            }
        }
        return null;
    }

    public void listarCategoriaResiduo() {

        System.out.println("\n--- LISTAR POR CATEGORIA ---");

        TipoResiduo tipoSelecionado = lerTipoResiduo();

        if (tipoSelecionado == null) {
            System.out.println("\nOpção inválida.");
            return;
        }

        List<Residuo> lista = service.listarPorTipo(tipoSelecionado);

        System.out.println("\nCategoria: " + tipoSelecionado.getDescricao());

        if (lista.isEmpty()) {
            System.out.println("Nenhum resíduo cadastrado nessa categoria.");
            return;
        }

        int total = 0;

        for (Residuo residuo : lista) {
            System.out.println("-------------------------");
            System.out.println("Código: " + residuo.getCodigo());
            System.out.println("Quantidade: " + residuo.getQuantidade());

            if (!residuo.getDescricao().isEmpty()) {
                System.out.println("Descrição: " + residuo.getDescricao());
            }

            total += residuo.getQuantidade();
        }

        System.out.println("-------------------------");
        System.out.println(lista.size() + " cadastro(s), " + total + " unidade(s) no total.");
    }

    public void excluirResiduo() {

        System.out.println("\n--- EXCLUIR RESÍDUO ---");
        System.out.print("Digite o código do resíduo: ");
        String codigo = sc.nextLine();

        try {
            Residuo residuo = service.buscar(codigo);

            System.out.println("\nCódigo: " + residuo.getCodigo());
            System.out.println("Categoria: " + residuo.getCategoriaResiduo());
            System.out.println("Quantidade: " + residuo.getQuantidade());

            System.out.print("\nTem certeza que deseja excluir? (s/n): ");
            String resposta = sc.nextLine();

            if (!resposta.equalsIgnoreCase("s")) {
                System.out.println("Exclusão cancelada.");
                return;
            }

            service.excluir(usuarioLogado, codigo);
            System.out.println("Resíduo excluído!");

        } catch (IllegalArgumentException | AcessoNegadoException e) {
            System.out.println("\nNão foi possível excluir: " + e.getMessage());
        }
    }
}