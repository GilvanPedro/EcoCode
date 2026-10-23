package Repository;

import Domain.RepositoryResiduosInterface;
import Domain.Residuo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GerenciarResiduosRepository implements RepositoryResiduosInterface {

    private Map<String, Residuo> residuos = new TreeMap<>();

    @Override
    public void salvar(Residuo residuo) {
        residuos.put(residuo.getCodigo(), residuo);
    }

    @Override
    public void editar(Residuo residuoEditado) {
        residuos.put(residuoEditado.getCodigo(), residuoEditado);
    }

    @Override
    public void excluir(String codigoResiduo) {
        residuos.remove(codigoResiduo);
    }

    @Override
    public List<Residuo> listar() {
        return new ArrayList<>(residuos.values());
    }

    @Override
    public Residuo buscar(String codigoResiduo) {
        return residuos.get(codigoResiduo);
    }
}
