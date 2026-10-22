package Domain;

public interface GerenciarResiduosInterface {
    public void cadastrarResiduo(String codigoResiduo, String tipoResiduo, int quantidadeResiduo);
    public void editarResiduo();
    public void excluirResiduo();
    public void listarResiduo();
}
