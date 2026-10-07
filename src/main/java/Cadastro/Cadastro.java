package Cadastro;

import Resíduo.Residuos;
import Classificador.Classificador;
import java.util.ArrayList;
import java.util.Scanner;

public class Cadastro {

    private static int codigo_numerico = 1;

    private ArrayList<Residuos> residuos = new ArrayList<>();

    private Scanner sc;

    public Cadastro(Scanner sc) {
        this.sc = sc;

        cadastrarRegistrosIniciais();
    }

    public String gerarCodigo(String tipo) {

        int restoDesejado = Classificador.obterRestoPorTipo(tipo);

        while (codigo_numerico % 5 != restoDesejado) {
            codigo_numerico++;
        }

        return String.format("%06d", codigo_numerico++);
    }

    private void cadastrarRegistrosIniciais() {

        residuos.add(new Residuos(gerarCodigo("Notebook"), "Notebook", 2));
        residuos.add(new Residuos(gerarCodigo("Computador"), "Computador", 1));
        residuos.add(new Residuos(gerarCodigo("Celular"), "Celular", 5));
        residuos.add(new Residuos(gerarCodigo("Tablet"), "Tablet", 3));
        residuos.add(new Residuos(gerarCodigo("Bateria"), "Bateria", 8));
        residuos.add(new Residuos(gerarCodigo("Pilha"), "Pilha", 10));
        residuos.add(new Residuos(gerarCodigo("Cabo"), "Cabo", 4));
        residuos.add(new Residuos(gerarCodigo("Monitor"), "Monitor", 2));
        residuos.add(new Residuos(gerarCodigo("Teclado"), "Teclado", 3));
        residuos.add(new Residuos(gerarCodigo("Mouse"), "Mouse", 2));
        residuos.add(new Residuos(gerarCodigo("Impressora"), "Impressora", 3));
        residuos.add(new Residuos(gerarCodigo("Carregador"),"Carregador", 8));
    }

    public void cadastrar_produto() {

        System.out.println("\n--- CADASTRO DE RESÍDUO ---");

        System.out.print("Qual resíduo eletrônico quer cadastrar? ");
        String tipo_residuo = sc.nextLine();

        System.out.print("Qual a quantidade? ");
        int quantidade = sc.nextInt();
        sc.nextLine();

        String codigo = gerarCodigo(tipo_residuo);

        Residuos residuo = new Residuos(
                codigo,
                tipo_residuo,
                quantidade
        );

        residuos.add(residuo);

        System.out.println("\nResíduo cadastrado!");
        System.out.println("Código: " + codigo);
        System.out.println("Resíduo: " + tipo_residuo);
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

        for (Residuos residuo : residuos) {

            System.out.println("-------------------------");
            System.out.println("Código: " + residuo.getCodigo());
            System.out.println("Resíduo: " + residuo.getTipo_residuo());
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

        for (Residuos residuo : residuos) {

            int resto = Classificador.calcularResto(residuo.getCodigo());

            String categoria = Classificador.classificar(residuo.getCodigo());

            System.out.println("-------------------------");
            System.out.println("Código: " + residuo.getCodigo());
            System.out.println("Resíduo: " + residuo.getTipo_residuo());
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

        for (Residuos residuo : residuos) {
            total += residuo.getQuantidade();
        }

        return total;
    }

    public ArrayList<Residuos> getResiduos() {
        return residuos;
    }
}