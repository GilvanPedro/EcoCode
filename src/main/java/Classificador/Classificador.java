package Classificador;

public class Classificador {

    public static int obterRestoPorTipo(String tipo) {

        tipo = tipo.toLowerCase().trim();

        if (tipo.equals("notebook") ||
                tipo.equals("computador") ||
                tipo.equals("pc") ||
                tipo.equals("desktop")) {

            return 0;
        }

        else if (tipo.equals("celular") ||
                tipo.equals("smartphone") ||
                tipo.equals("tablet")) {

            return 1;
        }

        else if (tipo.equals("cabo") ||
                tipo.equals("monitor") ||
                tipo.equals("teclado") ||
                tipo.equals("mouse") ||
                tipo.equals("fone") ||
                tipo.equals("fone de ouvido") ||
                tipo.equals("fone bluetooth") ||
                tipo.equals("headset") ||
                tipo.equals("carregador")||
                tipo.equals("periférico") ||
                tipo.equals("periferico"))  {

            return 2;
        }

        else if (tipo.equals("bateria") ||
                tipo.equals("pilha")) {

            return 3;
        }

        else {

            return 4;
        }
    }

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