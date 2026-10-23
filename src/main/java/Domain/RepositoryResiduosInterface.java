package Domain;

import java.util.List;

public interface RepositoryResiduosInterface {
    public void salvar(Residuo residuo);
    public void editar(String codigoResiduo);
    public void excluir(String codigoResiduo);
    public void listar(List<Residuo> residuos);
    public Residuo buscar(String codigoResiduo);
}
