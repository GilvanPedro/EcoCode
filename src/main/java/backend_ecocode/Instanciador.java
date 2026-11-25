package backend_ecocode;

import backend_ecocode.Estrutura.Grafo;
import backend_ecocode.Domain.Enums.Bairro;
import backend_ecocode.Repository.*;
import backend_ecocode.Service.*;
import backend_ecocode.TelaTerminal.TelaUsuarioTerminal;
import backend_ecocode.Util.GrafoBairros;
import backend_ecocode.Util.LogarUsuario;

public class Instanciador {

    // 1. repositories (não dependem de ninguém)
    private final GerenciarUsuariosRepository usuariosRepository = new GerenciarUsuariosRepository();
    private final GerenciarResiduosRepository residuosRepository = new GerenciarResiduosRepository();
    private final GerenciarSolicitacoesRepository solicitacoesRepository = new GerenciarSolicitacoesRepository();
    private final GerenciarPontoColetaRepository pontoColetaRepository = new GerenciarPontoColetaRepository();
    private final GerenciarLogRepository logRepository = new GerenciarLogRepository();

    // 2. services, recebendo os repositories
    private final GerenciarLogService logService = new GerenciarLogService(logRepository);
    private final GerenciarUsuariosService usuariosService = new GerenciarUsuariosService(usuariosRepository, logService);
    private final GerenciarResiduosService residuosService = new GerenciarResiduosService(residuosRepository);
    private final GerenciarSolicitacoesService solicitacoesService = new GerenciarSolicitacoesService(solicitacoesRepository, residuosService, logService);
    private final Grafo<Bairro> grafoBairros = GrafoBairros.criar();
    private final GerenciarPontoColetaService pontoColetaService = new GerenciarPontoColetaService(pontoColetaRepository, grafoBairros, logService);
    private final SessaoService sessaoService = new SessaoService();

    // 3. quem usa os services (cada linha só usa o que está acima dela)
    private final LogarUsuario logarUsuario = new LogarUsuario(usuariosService);
    private final TelaUsuarioTerminal telaUsuario =
            new TelaUsuarioTerminal(logarUsuario, usuariosService, residuosService, sessaoService);

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

    public GerenciarPontoColetaService getPontoColetaService() {
        return pontoColetaService;
    }

    public GerenciarLogService getLogService() {
        return logService;
    }

    public SessaoService getSessaoService() {
        return sessaoService;
    }
}