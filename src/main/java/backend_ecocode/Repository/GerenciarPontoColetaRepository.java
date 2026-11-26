package backend_ecocode.Repository;

import backend_ecocode.Domain.Entities.PontoColeta;
import backend_ecocode.Domain.Interfaces.RepositoryPontoColetaInterface;
import backend_ecocode.Persistency.Banco.PersistenciaBancoPontoColeta;
import backend_ecocode.Persistency.Files.PersistenciaArquivo;
import backend_ecocode.Persistency.Persistencia;
import backend_ecocode.Util.Constantes;

import java.util.*;

public class GerenciarPontoColetaRepository implements RepositoryPontoColetaInterface {

    private final Persistencia<PontoColeta> persistencia = new PersistenciaBancoPontoColeta();

    private final Map<UUID, PontoColeta> pontos = new HashMap<>();

    public GerenciarPontoColetaRepository() {
        for(PontoColeta ponto : persistencia.carregar()) {
            pontos.put(ponto.getId(), ponto);
        }
    }

    @Override
    public void salvar(PontoColeta ponto) {
        pontos.put(ponto.getId(), ponto);
        gravar();
    }

    @Override
    public void atualizar(PontoColeta ponto) {
        if (!pontos.containsKey(ponto.getId())){
            throw new IllegalArgumentException("Ponto de coleta não encontrado");
        }

        pontos.put(ponto.getId(), ponto);
        gravar();
    }

    @Override
    public void remover(UUID id) {
        if (!pontos.containsKey(id)){
            throw new IllegalArgumentException("Ponto de coleta não encontrado");
        }

        pontos.remove(id);
        gravar();
    }

    @Override
    public PontoColeta buscarPorId(UUID id) {
        PontoColeta ponto = pontos.get(id);
        if (ponto == null){
            throw new IllegalArgumentException("Ponto de coleta não encontrado");
        }

        return pontos.get(id);
    }

    @Override
    public List<PontoColeta> listar() {
        return new ArrayList<>(pontos.values());
    }

    private void gravar() {
        persistencia.salvar(new ArrayList<>(pontos.values()));
    }
}
