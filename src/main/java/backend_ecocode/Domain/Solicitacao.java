package backend_ecocode.Domain;

import java.time.LocalDate;
import java.util.UUID;

public class Solicitacao {
    private final UUID id;
    private UUID usuarioId;
    private UUID residuoId;
    private StatusSolicitacao status;
    private LocalDate dataSolicitacao;

    public Solicitacao(UUID usuarioId, UUID residuoId) {
        this.id = UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.residuoId = residuoId;
        this.status = StatusSolicitacao.PENDENTE;
        this.dataSolicitacao = LocalDate.now();
    }

    // construtor de carregamento do banco de dados
    public Solicitacao(UUID id, UUID usuarioId, UUID residuoId, StatusSolicitacao status, LocalDate dataSolicitacao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.residuoId = residuoId;
        this.status = status;
        this.dataSolicitacao = dataSolicitacao;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public UUID getResiduoId() {
        return residuoId;
    }

    public StatusSolicitacao getStatus() {
        return status;
    }

    public void iniciarRota(){
        if(status != StatusSolicitacao.PENDENTE){
            throw new IllegalArgumentException("Só é possível iniciar uma rota se a solicitação estiver pendente");
        }
        this.status = StatusSolicitacao.EM_ROTA;
    }

    public void finalizarRota(){
        if(status != StatusSolicitacao.EM_ROTA){
            throw new IllegalArgumentException("Só é possível concluir uma rota se a solicitação estiver em andamento");
        }
        this.status = StatusSolicitacao.CONCLUIDA;
    }

    public void cancelarRota(){
        if(status == StatusSolicitacao.EM_ROTA){
            throw new IllegalArgumentException("Não é possível cancelar uma solicitação em andamento");
        }
        if(status == StatusSolicitacao.CONCLUIDA){
            throw new IllegalArgumentException("Não é possível cancelar uma solicitação concluida");
        }
        if(status == StatusSolicitacao.CANCELADA){
            throw new IllegalArgumentException("Não é possível cancelar uma solicitação já cancelada");
        }

        this.status = StatusSolicitacao.CANCELADA;
    }

    public LocalDate getDataSolicitacao() {
        return dataSolicitacao;
    }
}
