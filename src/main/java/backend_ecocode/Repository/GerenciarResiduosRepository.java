package backend_ecocode.Repository;

import backend_ecocode.Estrutura.ArvoreAVL;
import backend_ecocode.Domain.Interfaces.RepositoryResiduosInterface;
import backend_ecocode.Domain.Entities.Residuo;
import backend_ecocode.Persistency.Banco.PersistenciaBancoResiduo;
import backend_ecocode.Persistency.Persistencia;

import java.util.*;

public class GerenciarResiduosRepository implements RepositoryResiduosInterface {

    private final Persistencia<Residuo> persistencia = new PersistenciaBancoResiduo();

    // antes: private final Map<String, Residuo> residuos = new LinkedHashMap<>();
    private final ArvoreAVL<String, Residuo> residuos = new ArvoreAVL<>();

    public GerenciarResiduosRepository() {
        for (Residuo residuo : persistencia.carregar()) {
            residuos.inserir(residuo.getCodigo(), residuo);
        }
    }

    @Override
    public synchronized void salvar(Residuo residuo) {
        residuos.inserir(residuo.getCodigo(), residuo);
        gravar();
    }

    @Override
    public synchronized void editar(Residuo residuoEditado) {
        residuos.inserir(residuoEditado.getCodigo(), residuoEditado); // chave repetida só atualiza
        gravar();
    }

    @Override
    public synchronized void excluir(String codigoResiduo) {
        residuos.remover(codigoResiduo);
        gravar();
    }

    @Override
    public synchronized List<Residuo> listar() {
        return residuos.emOrdem();
    }

    @Override
    public Residuo buscar(String codigoResiduo) {
        return residuos.buscar(codigoResiduo);
    }

    // toda mudança no Map é copiada para o arquivo
    private void gravar() {
        persistencia.salvar(listar());
    }
}
