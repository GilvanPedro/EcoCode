package backend_ecocode.Domain;

import java.util.List;
import java.util.UUID;

public interface RepositoryPontoColetaInterface {
    public void salvar(PontoColeta ponto);
    public void atualizar(PontoColeta ponto);
    public void remover(UUID id);
    public PontoColeta buscarPorId(UUID id);
    public List<PontoColeta> listar();
}
