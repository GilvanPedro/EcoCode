package Domain;

import java.util.List;

public interface GerenciarResiduosInterface {
    public void cadastrarResiduo(String codigoResiduo, TipoResiduo tipoResiduo, int quantidadeResiduo);
    public void editarResiduo(String codigoResiduo);
    public void excluirResiduo(String codigoResiduo);
    public void listarResiduo(List<Residuo> residuos);
}
