package Util;

import Domain.TipoResiduo;

public class GerarCodigoResiduo {

    // contador compartilhado: só anda pra frente, então nunca repete código na mesma execução
    private static int codigoNumerico = 1;

    // gera o próximo código cujo (código mod 5) é igual ao resto da categoria do tipo
    public String gerarCodigo(TipoResiduo tipoResiduo) {

        int restoDesejado = tipoResiduo.getResto();

        while (codigoNumerico % 5 != restoDesejado) {
            codigoNumerico++;
        }

        return String.format("%04d", codigoNumerico++);
    }
}