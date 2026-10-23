package Cadastro;

import Util.CalcularRestoResiduo;
import Domain.TipoResiduo;
import Domain.Residuo;
import Service.GerenciarResiduosService;
import Util.GerarCodigoResiduo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TelaCadastroResiduoTerminal {

    private ArrayList<Residuo> residuos = new ArrayList<>();

    private Scanner sc;
    private final GerenciarResiduosService service = new GerenciarResiduosService();

    public TelaCadastroResiduoTerminal(Scanner sc) {
        this.sc = sc;

        cadastrarRegistrosIniciais();
    }

    private void cadastrarRegistrosIniciais() {

        TipoResiduo[] tipos = {
                TipoResiduo.COMPUTADORES, TipoResiduo.BATERIAS, TipoResiduo.OUTROS,
                TipoResiduo.CELULARES,    TipoResiduo.CABOS,    TipoResiduo.COMPUTADORES,
                TipoResiduo.BATERIAS,     TipoResiduo.OUTROS,   TipoResiduo.CELULARES,
                TipoResiduo.CABOS
        };

        int[] quantidades = {5, 2, 6, 5, 1, 3, 8, 4, 2, 7};

        for (int i = 0; i < tipos.length; i++) {
            service.salvar(tipos[i], quantidades[i], "descriçao padrao inicial de teste");
        }
    }

    public void telaCadastrarResiduoTerminal() {

        System.out.println("\n--- CADASTRO DE RESÍDUO ---");

        // 1. Exibe as opções listando o resto e a descrição do enum
        System.out.println("Selecione o tipo de resíduo eletrônico:");
        for (TipoResiduo tipo : TipoResiduo.values()) {
            System.out.println("[" + tipo.getResto() + "] - " + tipo.getDescricao());
        }

        System.out.print("Digite o número da opção: ");
        byte opcaoDigitada = sc.nextByte();
        sc.nextLine(); // limpa o buffer

        // 2. Procura o tipo com o resto digitado
        TipoResiduo tipoSelecionado = null;
        for (TipoResiduo tipo : TipoResiduo.values()) {
            if (tipo.getResto() == opcaoDigitada) {
                tipoSelecionado = tipo;
                break;
            }
        }

        if (tipoSelecionado == null) {
            System.out.println("\nOpção inválida. Cadastro cancelado.");
            return;
        }

        System.out.print("Qual a quantidade? ");
        int quantidade = sc.nextInt();
        sc.nextLine(); // limpa o buffer

        System.out.print("Descrição (opcional, aperte Enter para pular): ");
        String descricao = sc.nextLine();

        try {
            Residuo residuo = service.salvar(tipoSelecionado, quantidade, descricao);

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

    public void listar_residuos() {

        System.out.println("\n--- LISTA DE RESÍDUOS ---");

        List<Residuo> lista = service.listar();

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

    public void classificar_residuos() {

        System.out.println("\n--- CLASSIFICAÇÃO DOS RESÍDUOS ---");

        if (residuos.isEmpty()) {
            System.out.println("Nenhum resíduo cadastrado.");
            return;
        }

        for (Residuo residuo : residuos) {

            int resto = CalcularRestoResiduo.calcularResto(residuo.getCodigo());

            System.out.println("-------------------------");
            System.out.println("Código: " + residuo.getCodigo());
            System.out.println("Resíduo: " + residuo.getResiduo());
            System.out.println("Quantidade: " + residuo.getQuantidade());
            System.out.println("Resto: " + resto);
            System.out.println("Categoria: " + residuo.getCategoriaResiduo());
        }

        System.out.println("-------------------------");
    }

    public int quantidade_cadastros() {
        return service.listar().size();
    }

    public int quantidadeTotalResiduos() {

        int total = 0;

        for (Residuo residuo : residuos) {
            total += residuo.getQuantidade();
        }

        return total;
    }

    public ArrayList<Residuo> getResiduos() {
        return residuos;
    }
}