package backend_ecocode.Repository;

import backend_ecocode.Estrutura.PilhaEncadeada;
import backend_ecocode.Domain.Entities.RegistroLog;
import backend_ecocode.Domain.Interfaces.RepositoryLogInterface;
import backend_ecocode.Persistency.Files.PersistenciaArquivo;
import backend_ecocode.Persistency.Persistencia;
import backend_ecocode.Util.Constantes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GerenciarLogRepository implements RepositoryLogInterface {

    private final Persistencia<RegistroLog> persistencia = new PersistenciaArquivo<>(Constantes.LOG_PATH, RegistroLog.class);
    private final PilhaEncadeada<RegistroLog> pilha = new PilhaEncadeada<>();

    public GerenciarLogRepository() {
        List<RegistroLog> salvos = persistencia.carregar();

        // empilha do mais antigo pro mais novo, assim o mais novo fica no topo
        salvos.sort(Comparator.comparing(RegistroLog::getDataRegistro));
        for (RegistroLog registro : salvos) {
            pilha.empilhar(registro);
        }
    }

    @Override
    public synchronized void registrar(RegistroLog registro) {
        pilha.empilhar(registro);
        gravar();
    }

    // o mais recente primeiro
    @Override
    public synchronized List<RegistroLog> listar() {
        return pilha.listar();
    }

    private void gravar() {
        // inverte pra salvar em ordem cronológica, que é mais natural de ler no JSON
        List<RegistroLog> cronologico = new ArrayList<>(pilha.listar());
        java.util.Collections.reverse(cronologico);
        persistencia.salvar(cronologico);
    }
}