package backend_ecocode.Repository;

import backend_ecocode.Domain.Interfaces.RepositorySolicitacoesInterface;
import backend_ecocode.Domain.Entities.Solicitacao;
import backend_ecocode.Persistency.Banco.PersistenciaBancoSolicitacao;
import backend_ecocode.Persistency.Persistencia;

import java.util.*;

public class GerenciarSolicitacoesRepository implements RepositorySolicitacoesInterface {
    private final Persistencia<Solicitacao> persistencia = new PersistenciaBancoSolicitacao();
    private final Map<UUID, Solicitacao> solicitacoes = new HashMap<>();

    public GerenciarSolicitacoesRepository() {
        for (Solicitacao solicitacao : persistencia.carregar()) {
            solicitacoes.put(solicitacao.getId(), solicitacao);
        }
    }

    @Override
    public void salvar(Solicitacao solicitacao) {
        solicitacoes.put(solicitacao.getId(), solicitacao);
        gravar();
    }

    @Override
    public void atualizar(Solicitacao solicitacaoAtualizada) {
        solicitacoes.put(solicitacaoAtualizada.getId(), solicitacaoAtualizada);
        gravar();
    }

    @Override
    public Solicitacao buscarPorId(UUID id) {
        return solicitacoes.get(id);
    }

    @Override
    public List<Solicitacao> listar() {
        return new ArrayList<>(solicitacoes.values());
    }

    private void gravar(){
        persistencia.salvar(listar());
    }
}
