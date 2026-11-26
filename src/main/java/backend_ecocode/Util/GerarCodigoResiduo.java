package backend_ecocode.Util;

import backend_ecocode.Domain.Entities.Residuo;
import backend_ecocode.Domain.Enums.TipoResiduo;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GerarCodigoResiduo {

    // último código de cada tipo, calculado a partir dos resíduos do banco
    private final Map<TipoResiduo, Integer> ultimosCodigos = new EnumMap<>(TipoResiduo.class);

    public GerarCodigoResiduo(List<Residuo> residuosExistentes) {
        for (Residuo r : residuosExistentes) {
            int numero = Integer.parseInt(r.getCodigo());
            ultimosCodigos.merge(r.getResiduo(), numero, Math::max);
        }
    }

    // próximo código = menor número depois do último DO MESMO TIPO cujo (código mod 5) é o resto do tipo
    public String gerarCodigo(TipoResiduo tipoResiduo) {
        if (tipoResiduo == null)
            throw new IllegalArgumentException("O tipo do resíduo é obrigatório");

        int ultimo = ultimosCodigos.getOrDefault(tipoResiduo, 0);
        int candidato = ultimo + 1;
        int distancia = Math.floorMod(tipoResiduo.getResto() - candidato, 5);
        int proximo = candidato + distancia;

        ultimosCodigos.put(tipoResiduo, proximo);
        return String.format("%06d", proximo);
    }
}