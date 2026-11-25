package backend_ecocode.Domain.Interfaces;

import backend_ecocode.Domain.Entities.Solicitacao;

import java.util.List;
import java.util.UUID;

public interface RepositorySolicitacoesInterface {
    public void salvar(Solicitacao solicitacao);
    public void atualizar(Solicitacao solicitacao);
    public Solicitacao buscarPorId(UUID id);
    public List<Solicitacao> listar();
}
