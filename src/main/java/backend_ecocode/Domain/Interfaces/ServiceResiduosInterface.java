package backend_ecocode.Domain.Interfaces;

import backend_ecocode.Domain.Entities.Residuo;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.TipoResiduo;

import java.util.List;
import java.util.UUID;

public interface ServiceResiduosInterface {
    public Residuo salvar(Usuario dono, TipoResiduo tipoResiduo, int quantidadeResiduo, String descricao);
    public Residuo editar(Usuario quem, String codigoResiduo, int quantidadeResiduo, String descricao);
    public void excluir(Usuario quem, String codigoResiduo);
    public List<Residuo> listar();
    public List<Residuo> listarPorTipo(TipoResiduo tipoResiduo);
    public List<Residuo> listarPorCodigo();
    public Residuo buscar(String codigoResiduo);
    public Residuo buscarPorId(UUID id);
    public List<Residuo> listarDisponiveis();
}
