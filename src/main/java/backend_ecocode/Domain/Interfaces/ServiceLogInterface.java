package backend_ecocode.Domain.Interfaces;

import backend_ecocode.Domain.Entities.RegistroLog;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.AcaoLog;

import java.util.List;

public interface ServiceLogInterface {

    void registrar(Usuario quem, AcaoLog acao, String alvo);
    List<RegistroLog> listar(Usuario quemConsulta);
}