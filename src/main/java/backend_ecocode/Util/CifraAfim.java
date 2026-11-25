package backend_ecocode.Util;

public class CifraAfim {
    private final Configuracao configuracao = new Configuracao();

    // um alfabeto só: maiúsculas (0-25), minúsculas (26-51) e dígitos (52-61)
    private static final String ALFABETO =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "0123456789";

    private static final int M = ALFABETO.length(); // 62

    private final int chave_a = configuracao.getChaveCifraAfimA();
    private final int chave_b = configuracao.getChaveCifraAfimB();

    public CifraAfim() {
        // sem mdc(a, 62) = 1 não existe inverso, e não dá pra descriptografar
        if (Aritmetica.mdc(chave_a, M) != 1)
            throw new IllegalStateException("A chave 'a' precisa ser prima com " + M);
    }

    // caractere -> valor (A = 0 ... z = 51, 0 = 52 ... 9 = 61); -1 se não estiver no alfabeto
    private int caractereEmValor(char caractere) {
        return ALFABETO.indexOf(caractere);
    }

    // valor -> caractere
    private char valorEmCaractere(int valor) {
        if (valor < 0 || valor >= M)
            throw new IllegalArgumentException("Valor fora do alfabeto: " + valor);
        return ALFABETO.charAt(valor);
    }

    // E(x) = (a·x + b) mod 62
    public String cifraAfim(String mensagem) {
        if (mensagem == null)
            throw new IllegalArgumentException("A mensagem é obrigatória");

        StringBuilder resultado = new StringBuilder();

        for (char caractere : mensagem.toCharArray()) {
            int valor = caractereEmValor(caractere);

            // espaço, pontuação, acento: passa sem mudar
            if (valor == -1) {
                resultado.append(caractere);
                continue;
            }

            int valorAfim = Math.floorMod(chave_a * valor + chave_b, M);
            resultado.append(valorEmCaractere(valorAfim));
        }
        return resultado.toString();
    }

    // D(y) = a⁻¹·(y − b) mod 62
    public String descriptografarMensagemAfim(String mensagem) {
        if (mensagem == null)
            throw new IllegalArgumentException("A mensagem é obrigatória");

        int inversoA = Aritmetica.inversoModular(chave_a, M);
        StringBuilder resultado = new StringBuilder();

        for (char caractere : mensagem.toCharArray()) {
            int valor = caractereEmValor(caractere);

            if (valor == -1) {
                resultado.append(caractere);
                continue;
            }

            int valorOriginal = Math.floorMod(inversoA * (valor - chave_b), M);
            resultado.append(valorEmCaractere(valorOriginal));
        }
        return resultado.toString();
    }
}