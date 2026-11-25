package backend_ecocode.Service;

import backend_ecocode.Estrutura.FilaEncadeada;
import backend_ecocode.Domain.Entities.Residuo;
import backend_ecocode.Domain.Entities.Solicitacao;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.AcaoLog;
import backend_ecocode.Domain.Enums.CargoUsuario;
import backend_ecocode.Domain.Enums.StatusResiduo;
import backend_ecocode.Domain.Enums.StatusSolicitacao;
import backend_ecocode.Domain.Interfaces.ServiceSolicitacoesInterface;
import backend_ecocode.Exceptions.AcessoNegadoException;
import backend_ecocode.Repository.GerenciarSolicitacoesRepository;

import java.util.*;

public class GerenciarSolicitacoesService implements ServiceSolicitacoesInterface {

    private final GerenciarSolicitacoesRepository repository;
    private final FilaEncadeada<Solicitacao> filaPendentes = new FilaEncadeada<>();
    private final Map<UUID, Solicitacao> emRota = new HashMap<>();
    private final GerenciarResiduosService residuoService;
    private final GerenciarLogService log;

    public GerenciarSolicitacoesService(GerenciarSolicitacoesRepository repository, GerenciarResiduosService residuoService, GerenciarLogService log) {
        this.repository = repository;
        this.residuoService = residuoService;
        this.log = log;

        // remonta a fila e as em rota a partir do que está salvo
        List<Solicitacao> todas = repository.listar();
        todas.sort(Comparator.comparing(Solicitacao::getDataSolicitacao));

        for (Solicitacao solicitacao : todas) {
            if (solicitacao.getStatus() == StatusSolicitacao.PENDENTE) {
                filaPendentes.enfileirar(solicitacao);
            } else if (solicitacao.getStatus() == StatusSolicitacao.EM_ROTA) {
                emRota.put(solicitacao.getId(), solicitacao);
            }
        }
    }

    @Override
    public Solicitacao solicitar(Usuario solicitante, UUID residuoId) {
        if(solicitante == null){
            throw new IllegalArgumentException("Quem está solicitando não pode ser nulo");
        }

        if(residuoId == null){
            throw new IllegalArgumentException("Você precisa adicioanar um resíduo para ser retirado");
        }

        Residuo residuo = residuoService.buscarPorId(residuoId);

        if (!solicitante.getId().equals(residuo.getUsuarioId())){
            throw new AcessoNegadoException("Você só pode pedir descarte dos seus resíduos");

        }

        if(residuo.getStatus() != StatusResiduo.DISPONIVEL){
            throw new IllegalArgumentException("Esse resíduo já está em uma solicitação ou já foi descartado");
        }

        Solicitacao solicitacao = new Solicitacao(solicitante.getId(), residuoId);

        residuoService.alterarStatus(residuoId, StatusResiduo.EM_SOLICITACAO);
        repository.salvar(solicitacao);
        filaPendentes.enfileirar(solicitacao);

        return solicitacao;
    }

    @Override
    public Solicitacao pegarProxima(Usuario coletor) {
        if (coletor == null) {
            throw new IllegalArgumentException("É preciso informar quem está pegando a solicitação");
        }

        if (coletor.getCargoUsuario() == CargoUsuario.USUARIO) {
            throw new AcessoNegadoException("Usuário não pode pegar uma solicitação");
        }

        Solicitacao proxima = null;

        // tira da fila até achar uma pendente; as canceladas são descartadas no caminho
        while (!filaPendentes.estaVazia()) {
            Solicitacao atual = filaPendentes.desenfileirar();

            if (atual.getStatus() == StatusSolicitacao.PENDENTE) {
                proxima = atual;
                break;
            }
        }

        if (proxima == null) {
            throw new IllegalStateException("Não há solicitações pendentes");
        }

        proxima.iniciarRota();
        emRota.put(proxima.getId(), proxima);
        repository.atualizar(proxima);
        log.registrar(coletor, AcaoLog.PEGOU_SOLICITACAO, proxima.getId().toString());

        return proxima;
    }

    @Override
    public Solicitacao concluir(Usuario coletor, UUID solicitacaoId) {
        if (coletor == null) {
            throw new IllegalArgumentException("É preciso informar quem está concluindo a solicitação");
        }

        if (coletor.getCargoUsuario() == CargoUsuario.USUARIO) {
            throw new AcessoNegadoException("Usuário não pode concluir uma solicitação");
        }

        Solicitacao solicitacao = emRota.get(solicitacaoId);

        if(solicitacao == null){
            throw new IllegalArgumentException("Solicitação não está em rota");
        }

        solicitacao.finalizarRota();
        emRota.remove(solicitacaoId);
        repository.atualizar(solicitacao);
        residuoService.alterarStatus(solicitacao.getResiduoId(), StatusResiduo.DESCARTADO);
        log.registrar(coletor, AcaoLog.CONCLUIU_SOLICITACAO, solicitacao.getId().toString());

        return solicitacao;
    }

    @Override
    public Solicitacao cancelar(Usuario quemCancela, UUID solicitacaoId) {
        if(quemCancela == null){
            throw new IllegalArgumentException("É preciso informar quem cancela uma solicitação");
        }

        Solicitacao solicitacao = repository.buscarPorId(solicitacaoId);

        if(solicitacao == null){
            throw new IllegalArgumentException("Solicitação não existe");
        }

        if (quemCancela.getCargoUsuario() == CargoUsuario.GERENTE) {
            solicitacao.cancelarPorGerente();
            emRota.remove(solicitacaoId);   // se não estiver lá, não faz nada
        } else {
            if (!quemCancela.getId().equals(solicitacao.getUsuarioId())) {
                throw new AcessoNegadoException("Usuário não pode cancelar solicitação de outra pessoa");
            }
            solicitacao.cancelarRota();
        }

        repository.atualizar(solicitacao);
        residuoService.alterarStatus(solicitacao.getResiduoId(), StatusResiduo.DISPONIVEL);

        if(quemCancela.getCargoUsuario() == CargoUsuario.GERENTE) {
            log.registrar(quemCancela, AcaoLog.CANCELOU_SOLICITACAO, solicitacao.getId().toString());
        }

        return solicitacao;
    }

    @Override
    public List<Solicitacao> listarDoUsuario(UUID usuarioId) {
        List<Solicitacao> doUsuario = new ArrayList<>();

        for (Solicitacao solicitacao : repository.listar()) {
            if (solicitacao.getUsuarioId().equals(usuarioId)) {
                doUsuario.add(solicitacao);
            }
        }

        return doUsuario;
    }

    @Override
    public int posicaoNaFila(UUID solicitacaoId) {
        int posicao = 0;

        for (Solicitacao solicitacao : filaPendentes.listar()) {
            // as canceladas continuam na fila, mas não contam
            if (solicitacao.getStatus() != StatusSolicitacao.PENDENTE) {
                continue;
            }

            posicao++;

            if (solicitacao.getId().equals(solicitacaoId)) {
                return posicao;
            }
        }

        throw new IllegalArgumentException("Essa solicitação não está aguardando na fila");
    }

}
