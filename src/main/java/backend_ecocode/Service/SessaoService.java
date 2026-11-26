package backend_ecocode.Service;

import backend_ecocode.Domain.Entities.Sessao;
import backend_ecocode.Persistency.Banco.PersistenciaBancoSessao;
import backend_ecocode.Persistency.Files.PersistenciaArquivo;
import backend_ecocode.Persistency.Persistencia;
import backend_ecocode.Util.Constantes;
import backend_ecocode.Util.TokenLocal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessaoService {
    private final PersistenciaBancoSessao banco = new PersistenciaBancoSessao();
    private final TokenLocal tokenLocal = new TokenLocal();

    public Sessao criar(UUID usuarioId) {
        encerrar(); // se já tinha uma sessão neste computador, apaga
        Sessao sessao = new Sessao(usuarioId);
        banco.salvar(sessao);
        tokenLocal.guardar(sessao.getToken());
        return sessao;
    }

    public Sessao buscarValida() {
        String token = tokenLocal.ler();
        if (token == null) return null;

        Sessao sessao = banco.buscar(token);
        if (sessao == null || sessao.estaVencida()) {
            encerrar();
            return null;
        }
        return sessao;
    }

    public void encerrar() {
        String token = tokenLocal.ler();
        if (token != null) banco.apagar(token);
        tokenLocal.apagar();
    }
}