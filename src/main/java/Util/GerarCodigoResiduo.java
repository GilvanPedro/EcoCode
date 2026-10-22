package Util;

import Classificador.Classificador;
import Domain.TipoResiduo;

public class GerarCodigoResiduo {

    private static int codigo_numerico = 1;

    public String gerarCodigo(TipoResiduo tipoResiduo) {
        ObterRestoPorTipo obterRestoPorTipo = new ObterRestoPorTipo();

        int restoDesejado = (int) obterRestoPorTipo.obterResto(tipoResiduo);

        while (codigo_numerico % 5 != restoDesejado) {
            codigo_numerico++;
        }

        return String.format("%06d", codigo_numerico++);
    }
}
