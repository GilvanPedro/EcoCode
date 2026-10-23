package Domain;

import java.util.List;

public interface ServiceResiduosInterface {
    public void salvar(TipoResiduo tipoResiduo, int quantidadeResiduo);
    public void editar(String codigoResiduo);
    public void excluir(String codigoResiduo);
    public void listar(List<Residuo> residuos);
    public Residuo buscar(String codigoResiduo);
}
