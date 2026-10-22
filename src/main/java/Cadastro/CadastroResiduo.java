package Cadastro;

import Domain.TipoResiduo;
import Model.Entity.Residuo;
import Classificador.Classificador;
import Util.GerarCodigoResiduo;

import java.util.ArrayList;
import java.util.Scanner;

public class CadastroResiduo {

    private ArrayList<Residuo> residuos = new ArrayList<>();

    private Scanner sc;

    public CadastroResiduo(Scanner sc) {
        this.sc = sc;

        cadastrarRegistrosIniciais();
    }

    private void cadastrarRegistrosIniciais() {

        GerarCodigoResiduo gerarCodigoResiduo = new GerarCodigoResiduo();

        residuos.add(new Residuo(gerarCodigoResiduo.gerarCodigo(TipoResiduo.COMPUTADORES), TipoResiduo.COMPUTADORES, 3));


    }

    public void cadastrar_produto() {

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

        System.out.println(
                "Categoria: " + Classificador.classificar(codigo)
        );
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
            System.out.println("Resíduo: " + residuo.getTipoResiduo());
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

            String categoria = Classificador.classificar(residuo.getCodigo());

            System.out.println("-------------------------");
            System.out.println("Código: " + residuo.getCodigo());
            System.out.println("Resíduo: " + residuo.getTipoResiduo());
            System.out.println("Quantidade: " + residuo.getQuantidade());
            System.out.println("Resto: " + resto);
            System.out.println("Categoria: " + categoria);
        }

        System.out.println("-------------------------");
    }

    public int quantidade_cadastros() {
        return residuos.size();
    }

    public int quantidade_total_residuos() {

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