package backend_ecocode.Domain.Entities;

import backend_ecocode.Domain.Enums.AcaoLog;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegistroLog {
    private final UUID id;
    private final UUID usuarioId;
    private final String nomeUsuario;
    private final LocalDateTime dataRegistro;
    private final AcaoLog acaoLog;
    private final String alvo;

    public RegistroLog(UUID usuarioId, String nomeUsuario, AcaoLog acaoLog, String alvo) {
        this.id = UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.dataRegistro = LocalDateTime.now();
        this.acaoLog = acaoLog;
        this.alvo = alvo;
    }

    public RegistroLog(UUID id, UUID usuarioId, String nomeUsuario, LocalDateTime dataRegistro, AcaoLog acaoLog, String alvo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nomeUsuario = nomeUsuario;
        this.dataRegistro = dataRegistro;
        this.acaoLog = acaoLog;
        this.alvo = alvo;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public AcaoLog getAcaoLog() {
        return acaoLog;
    }

    public String getAlvo() {
        return alvo;
    }
}
