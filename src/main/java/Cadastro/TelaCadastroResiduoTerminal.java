package Cadastro;

import Domain.TipoResiduo;
import Domain.Residuo;
import Classificador.Classificador;
import Util.GerarCodigoResiduo;

import java.util.ArrayList;
import java.util.Scanner;

public class TelaCadastroResiduoTerminal {

    private ArrayList<Residuo> residuos = new ArrayList<>();

    private Scanner sc;

    public TelaCadastroResiduoTerminal(Scanner sc) {
        this.sc = sc;

        cadastrarRegistrosIniciais();
    }

    private void cadastrarRegistrosIniciais() {

        GerarCodigoResiduo gerarCodigoResiduo = new GerarCodigoResiduo();

        residuos.add(new Residuo(gerarCodigoResiduo.gerarCodigo(TipoResiduo.COMPUTADORES), TipoResiduo.COMPUTADORES, 5));
        residuos.add(new Residuo(gerarCodigoResiduo.gerarCodigo(TipoResiduo.BATERIAS), TipoResiduo.BATERIAS, 2));
        residuos.add(new Residuo(gerarCodigoResiduo.gerarCodigo(TipoResiduo.OUTROS), TipoResiduo.OUTROS, 6));
        residuos.add(new Residuo(gerarCodigoResiduo.gerarCodigo(TipoResiduo.CELULARES), TipoResiduo.CELULARES, 5));
        residuos.add(new Residuo(gerarCodigoResiduo.gerarCodigo(TipoResiduo.CABOS), TipoResiduo.CABOS, 1));

    }

    public void telaCadastrarResiduoTerminal() {

        GerarCodigoResiduo gerarCodigoResiduo = new GerarCodigoResiduo();

        System.out.println("\n--- CADASTRO DE RESÍDUO ---");

        // 1. Exibe as opções listando o resto e a descrição do seu enum
        System.out.println("Selecione o tipo de resíduo eletrônico:");
        for (Domain.TipoResiduo tipo : Domain.TipoResiduo.values()) {
            System.out.println("[" + tipo.getResto() + "] - " + tipo.getDescricao());
        }

        System.out.print("Digite o número da opção: ");
        byte opcaoDigitada = sc.nextByte();
        sc.nextLine(); // Limpa o buffer do teclado

        // 2. Procura qual enum possui o 'resto' igual ao número digitado
        Domain.TipoResiduo tipoSelecionado = Domain.TipoResiduo.OUTROS; // Padrão caso não encontre
        for (Domain.TipoResiduo tipo : Domain.TipoResiduo.values()) {
            if (tipo.getResto() == opcaoDigitada) {
                tipoSelecionado = tipo;
                break;
            }
        }

        System.out.print("Qual a quantidade? ");
        int quantidade = sc.nextInt();
        sc.nextLine(); // Limpa o buffer do teclado

        String codigo = gerarCodigoResiduo.gerarCodigo(tipoSelecionado);

        Residuo residuo = new Residuo(
                codigo,
                tipoSelecionado,
                quantidade
        );

        residuos.add(residuo);

        System.out.println("\nResíduo cadastrado!");
        System.out.println("Código: " + codigo);
        System.out.println("Resíduo: " + tipoSelecionado.getDescricao());
        System.out.println("Quantidade: " + quantidade);
        System.out.println("Categoria: " + residuo.getCategoriaResiduo());
    }

    public void listar_residuos() {

        System.out.println("\n--- LISTA DE RESÍDUOS ---");

        if (residuos.isEmpty()) {
            System.out.println("Nenhum resíduo cadastrado.");
            return;
        }

        for (Residuo residuo : residuos) {

            System.out.println("-------------------------");
            System.out.println("Código: " + residuo.getCodigo());
            System.out.println("Resíduo: " + residuo.getCategoriaResiduo());
            System.out.println("Quantidade: " + residuo.getQuantidade());
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

            int resto = Classificador.calcularResto(residuo.getCodigo());

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
        return residuos.size();
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