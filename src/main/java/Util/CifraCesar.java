package Util;

import java.util.ArrayList;
import java.util.List;

public class CifraCesar {

    private final Configuracao configuracao = new Configuracao();

    private static final String ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALFABETO_MINUSCULO = ALFABETO.toLowerCase();
    // private static final String NUMEROS = "0123456789";

    private final int chave = configuracao.getChaveCifra();

    // letra -> valor (A = 0 ... Z = 25); devolve -1 se não for letra do alfabeto
    private String alfabetoDe(char letra) {
        return Character.isLowerCase(letra) ? ALFABETO_MINUSCULO : ALFABETO;
    }

    public int letraEmValor(char letra) {
        return alfabetoDe(letra).indexOf(letra);
    }

    public char valorEmLetra(int valor, String alfabeto) {
        if (valor < 0 || valor >= alfabeto.length())
            throw new IllegalArgumentException("Valor fora do alfabeto: " + valor);

        return alfabeto.charAt(valor);
    }

    public String cifraCesar(String mensagem) {
        if (mensagem == null)
            throw new IllegalArgumentException("A mensagem é obrigatória");

        StringBuilder resultadoCriptografado = new StringBuilder();

        for (char caractere : mensagem.toCharArray()) {

            int valor = letraEmValor(caractere);

            // não é letra (espaço, número, pontuação, acento): passa sem mudar
            if (valor == -1) {
                resultadoCriptografado.append(caractere);
                continue;
            }

            String alfabeto = alfabetoDe(caractere);
            int deslocamento = Character.isLowerCase(caractere) ? -chave : chave;
            int novoValor = Math.floorMod(valor + deslocamento, alfabeto.length());

            resultadoCriptografado.append(valorEmLetra(novoValor, alfabeto));

        }

        return resultadoCriptografado.toString();
    }

    public String descriptografarCifra(String mensagem) {
        if (mensagem == null)
            throw new IllegalArgumentException("A mensagem é obrigatória");

        StringBuilder resultadoDescriptografado = new StringBuilder();

        for (char caractere : mensagem.toCharArray()) {

            int valor = letraEmValor(caractere);

            // não é letra (espaço, número, pontuação, acento): passa sem mudar
            if (valor == -1) {
                resultadoDescriptografado.append(caractere);
                continue;
            }

            String alfabeto = alfabetoDe(caractere);
            // inverte os valores para fazer o caminho oposto
            int deslocamento = Character.isLowerCase(caractere) ? chave : -chave;
            int novoValor = Math.floorMod(valor + deslocamento, alfabeto.length());

            resultadoDescriptografado.append(valorEmLetra(novoValor, alfabeto));
        }
        return resultadoDescriptografado.toString();
    }

    public List<String> descriptografarTodasChaves(String mensagem) {

        if (mensagem == null)
            throw new IllegalArgumentException("A mensagem é obrigatória");

        List<String> resultadoTodasChaves = new ArrayList<>();

        for (int i = 0; i < 26; i++) {

            StringBuilder resultadoDescriptografado = new StringBuilder();

            for (char caractere : mensagem.toCharArray()) {

                int valor = letraEmValor(caractere);

                // não é letra (espaço, número, pontuação, acento): passa sem mudar
                if (valor == -1) {
                    resultadoDescriptografado.append(caractere);
                    continue;
                }

                String alfabeto = alfabetoDe(caractere);
                // inverte os valores para fazer o caminho oposto e utilizando o i
                int deslocamento = Character.isLowerCase(caractere) ? i : -i;
                int novoValor = Math.floorMod(valor + deslocamento, alfabeto.length());

                resultadoDescriptografado.append(valorEmLetra(novoValor, alfabeto));
            }

            resultadoTodasChaves.add(resultadoDescriptografado.toString());
        }

        return resultadoTodasChaves;
    }
}