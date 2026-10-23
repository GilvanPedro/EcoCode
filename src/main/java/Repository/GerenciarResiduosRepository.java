package Repository;

import Domain.RepositoryResiduosInterface;
import Domain.Residuo;

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
    public void editar(String codigoResiduo) {

    }

    @Override
    public void excluir(String codigoResiduo) {

    }

    @Override
    public void listar(List<Residuo> residuos) {

    }

    @Override
    public Residuo buscar(String codigoResiduo) {
        return residuos.get(codigoResiduo);
    }
}
