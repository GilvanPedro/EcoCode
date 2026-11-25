package backend_ecocode.Service;

import backend_ecocode.Domain.Entities.RegistroLog;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.AcaoLog;
import backend_ecocode.Domain.Enums.CargoUsuario;
import backend_ecocode.Domain.Interfaces.ServiceLogInterface;
import backend_ecocode.Exceptions.AcessoNegadoException;
import backend_ecocode.Repository.GerenciarLogRepository;

import java.util.List;

public class GerenciarLogService implements ServiceLogInterface {

    private final GerenciarLogRepository repository;

    public GerenciarLogService(GerenciarLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public void registrar(Usuario quem, AcaoLog acao, String alvo) {
        if (quem == null)
            throw new IllegalArgumentException("É preciso informar quem fez a ação");
        if (acao == null)
            throw new IllegalArgumentException("É preciso informar a ação");

        RegistroLog registro = new RegistroLog(quem.getId(), quem.getNomeCompleto(), acao, alvo);
        repository.registrar(registro);
    }

    @Override
    public List<RegistroLog> listar(Usuario quemConsulta) {
        if (quemConsulta == null || quemConsulta.getCargoUsuario() != CargoUsuario.GERENTE)
            throw new AcessoNegadoException("Só o gerente pode ver o log de auditoria");

        return repository.listar();
    }
}