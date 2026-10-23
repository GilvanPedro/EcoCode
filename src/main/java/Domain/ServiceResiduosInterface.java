package Domain;

import java.util.List;

public interface ServiceResiduosInterface {
    public void salvar(TipoResiduo tipoResiduo, int quantidadeResiduo);
    public void editar(String codigoResiduo);
    public void excluir(String codigoResiduo);
    public List<Residuo> listar();
    public Residuo buscar(String codigoResiduo);
}
