package Util;

public class ValidadorCpf {

    /**
     * Valida um CPF informado como String.
     * Aceita formatos com pontuação (000.000.000-00) ou apenas números (00000000000).
     */
    public static boolean ehValido(String cpf) {
        if (cpf == null) {
            return false;
        }

        // Remove qualquer caractere que não seja número (pontos, traços, espaços)
        String cpfNumeros = cpf.replaceAll("\\D", "");

        // Um CPF deve conter exatamente 11 dígitos
        if (cpfNumeros.length() != 11) {
            return false;
        }

        // Rejeita CPFs com todos os dígitos iguais (ex: 111.111.111-11),
        // pois passariam no teste matemático mas são inválidos.
        if (cpfNumeros.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Validação do Primeiro Dígito Verificador
            int soma = 0;
            int peso = 10;
            for (int i = 0; i < 9; i++) {
                int num = Character.getNumericValue(cpfNumeros.charAt(i));
                soma += (num * peso);
                peso--;
            }

            int resto = 11 - (soma % 11);
            int digito1 = (resto == 10 || resto == 11) ? 0 : resto;

            // Validação do Segundo Dígito Verificador
            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                int num = Character.getNumericValue(cpfNumeros.charAt(i));
                soma += (num * peso);
                peso--;
            }

            resto = 11 - (soma % 11);
            int digito2 = (resto == 10 || resto == 11) ? 0 : resto;

            // Verifica se os dígitos calculados batem com os dígitos do CPF informado
            int dv1Informado = Character.getNumericValue(cpfNumeros.charAt(9));
            int dv2Informado = Character.getNumericValue(cpfNumeros.charAt(10));

            return (digito1 == dv1Informado && digito2 == dv2Informado);

        } catch (Exception e) {
            return false;
        }
    }
}

