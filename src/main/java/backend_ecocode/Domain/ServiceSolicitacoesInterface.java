package backend_ecocode.Domain;

import java.util.List;
import java.util.UUID;

public interface ServiceSolicitacoesInterface {
    Solicitacao solicitar(Usuario solicitante, UUID residuoId);
    Solicitacao pegarProxima(Usuario coletor);
    Solicitacao concluir(Usuario coletor, UUID solicitacaoId);
    Solicitacao cancelar(Usuario quemCancela, UUID solicitacaoId);
    List<Solicitacao> listarDoUsuario(UUID usuarioId);
    int posicaoNaFila(UUID solicitacaoId);
}
