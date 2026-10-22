package Classificador;

public class Classificador {

        public static int calcularResto(String codigo) {

            int numero = Integer.parseInt(codigo);

            return numero % 5;
        }

        public static String classificar(String codigo) {

            int resto = calcularResto(codigo);

            switch (resto) {

                case 0:
                    return "Computadores e notebooks";

                case 1:
                    return "Celulares e tablets";

                case 2:
                    return "Cabos e periféricos";

                case 3:
                    return "Baterias e pilhas";

                case 4:
                    return "Outros eletrônicos";

                default:
                    return "Categoria desconhecida";
            }
        }
}