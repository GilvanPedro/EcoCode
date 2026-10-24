package Util;

import java.util.ArrayList;
import java.util.List;

public class CifraCesar {

    private final Configuracao configuracao = new Configuracao();

    private static final String ALFABETO = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final int chave = configuracao.getChaveCifra();

    // letra -> valor (A = 0 ... Z = 25); devolve -1 se não for letra do alfabeto
    public int letraEmValor(char letra) {
        return ALFABETO.indexOf(Character.toUpperCase(letra));
    }

    // valor -> letra (0 = A ... 25 = Z)
    public char valorEmLetra(int valor) {
        if (valor < 0 || valor > 25)
            throw new IllegalArgumentException("Valor fora do alfabeto: " + valor);

        return ALFABETO.charAt(valor);
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

            // anda "chave" casas no alfabeto; o floorMod dá a volta depois do Z
            int novoValor = Math.floorMod(valor + chave, ALFABETO.length());

            resultadoCriptografado.append(valorEmLetra(novoValor));
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

            // anda "chave" casas para trás; o floorMod dá a volta antes do A
            int valorOriginal = Math.floorMod(valor - chave, ALFABETO.length());

            resultadoDescriptografado.append(valorEmLetra(valorOriginal));
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

                // anda "i" casas para trás; o floorMod dá a volta antes do A
                int valorOriginal = Math.floorMod(valor - i, ALFABETO.length());

                resultadoDescriptografado.append(valorEmLetra(valorOriginal));
            }

            resultadoTodasChaves.add(resultadoDescriptografado.toString());
        }

        return resultadoTodasChaves;
    }
}