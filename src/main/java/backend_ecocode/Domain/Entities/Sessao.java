package backend_ecocode.Domain.Entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Sessao {
    private static final int DIAS_VALIDADE = 30;

    private String token;
    private UUID usuarioId;
    private LocalDateTime expiraEm;

    public Sessao(UUID usuarioId) {
        this.token = UUID.randomUUID().toString();
        this.usuarioId = usuarioId;
        this.expiraEm = LocalDateTime.now().plusDays(DIAS_VALIDADE);
    }

    public boolean estaVencida() {
        return LocalDateTime.now().isAfter(expiraEm);
    }

    public String getToken() { return token; }
    public UUID getUsuarioId() { return usuarioId; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
}