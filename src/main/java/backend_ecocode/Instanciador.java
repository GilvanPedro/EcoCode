package backend_ecocode;

import backend_ecocode.Repository.GerenciarPontoColetaRepository;   // novo
import backend_ecocode.Repository.GerenciarResiduosRepository;
import backend_ecocode.Repository.GerenciarSolicitacoesRepository;
import backend_ecocode.Repository.GerenciarUsuariosRepository;
import backend_ecocode.Service.GerenciarPontoColetaService;         // novo
import backend_ecocode.Service.GerenciarResiduosService;
import backend_ecocode.Service.GerenciarSolicitacoesService;
import backend_ecocode.Service.GerenciarUsuariosService;
import backend_ecocode.TelaTerminal.TelaUsuarioTerminal;
import backend_ecocode.Util.LogarUsuario;

public class Instanciador {

    // 1. repositories (não dependem de ninguém)
    private final GerenciarUsuariosRepository usuariosRepository = new GerenciarUsuariosRepository();
    private final GerenciarResiduosRepository residuosRepository = new GerenciarResiduosRepository();
    private final GerenciarSolicitacoesRepository solicitacoesRepository = new GerenciarSolicitacoesRepository();
    private final GerenciarPontoColetaRepository pontoColetaRepository = new GerenciarPontoColetaRepository(); // novo

    // 2. services, recebendo os repositories
    private final GerenciarUsuariosService usuariosService = new GerenciarUsuariosService(usuariosRepository);
    private final GerenciarResiduosService residuosService = new GerenciarResiduosService(residuosRepository);
    private final GerenciarSolicitacoesService solicitacoesService = new GerenciarSolicitacoesService(solicitacoesRepository, residuosService);
    private final GerenciarPontoColetaService pontoColetaService = new GerenciarPontoColetaService(pontoColetaRepository); // novo

    // 3. quem usa os services
    private final LogarUsuario logarUsuario = new LogarUsuario(usuariosService);
    private final TelaUsuarioTerminal telaUsuario =
            new TelaUsuarioTerminal(logarUsuario, usuariosService, residuosService);

    public TelaUsuarioTerminal getTelaUsuario() {
        return telaUsuario;
    }

    public GerenciarUsuariosService getUsuariosService() {
        return usuariosService;
    }

    public GerenciarSolicitacoesService getSolicitacoesService() {
        return solicitacoesService;
    }

    public GerenciarResiduosService getResiduosService() {
        return residuosService;
    }

    public GerenciarPontoColetaService getPontoColetaService() { // novo
        return pontoColetaService;
    }
}