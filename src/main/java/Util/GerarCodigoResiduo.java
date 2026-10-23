package Util;

import Domain.TipoResiduo;

public class GerarCodigoResiduo {

    private static int codigoNumerico = 1;

    public String gerarCodigo(TipoResiduo tipoResiduo) {
        ObterRestoPorTipo obterRestoPorTipo = new ObterRestoPorTipo();

        int restoDesejado = (int) obterRestoPorTipo.obterResto(tipoResiduo);

        while (codigoNumerico % 5 != restoDesejado) {
            codigoNumerico++;
        }

        return String.format("%04d", codigoNumerico++);
    }
}
