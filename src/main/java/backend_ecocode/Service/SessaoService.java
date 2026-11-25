package backend_ecocode.Service;

import backend_ecocode.Domain.Entities.Sessao;
import backend_ecocode.Persistency.Files.PersistenciaArquivo;
import backend_ecocode.Persistency.Persistencia;
import backend_ecocode.Util.Constantes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessaoService {
    private final Persistencia<Sessao> persistencia = new PersistenciaArquivo<>(Constantes.SESSAO_PATH, Sessao.class);

    public Sessao criar(UUID usuarioId){
        Sessao sessao = new Sessao(usuarioId);
        List<Sessao> lista = new ArrayList<>();
        lista.add(sessao);
        persistencia.salvar(lista);

        return sessao;
    }

    public Sessao buscarValida(){
        List<Sessao> salvas = persistencia.carregar();
        if(salvas.isEmpty()) return null;

        Sessao sessao = salvas.get(0);
        if(sessao.estaVencida()){
            encerrar();
            return null;
        }
        return sessao;
    }

    public void encerrar(){
        persistencia.salvar(new ArrayList<>());
    }
}
