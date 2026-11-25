package backend_ecocode.Domain.Interfaces;

import backend_ecocode.Domain.Entities.RegistroLog;

import java.util.List;

public interface RepositoryLogInterface {

    void registrar(RegistroLog registro);
    List<RegistroLog> listar(); // o mais recente primeiro
}