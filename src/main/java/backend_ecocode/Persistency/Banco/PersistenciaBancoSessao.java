package backend_ecocode.Persistency.Banco;

import backend_ecocode.Domain.Entities.Sessao;

import java.sql.*;
import java.util.UUID;

public class PersistenciaBancoSessao {

    private static final String INSERT =
            "INSERT INTO sessoes (token, id_usuario, expiracao) VALUES (?, ?, ?)";
    private static final String SELECT =
            "SELECT * FROM sessoes WHERE token = ?";
    private static final String DELETE =
            "DELETE FROM sessoes WHERE token = ?";

    public void salvar(Sessao s) {
        try (Connection c = ConexaoBanco.conectar();
             PreparedStatement ps = c.prepareStatement(INSERT)) {
            ps.setString(1, s.getToken());
            ps.setString(2, s.getUsuarioId().toString());
            ps.setTimestamp(3, Timestamp.valueOf(s.getExpiraEm()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar sessão: " + e.getMessage(), e);
        }
    }

    public Sessao buscar(String token) {
        try (Connection c = ConexaoBanco.conectar();
             PreparedStatement ps = c.prepareStatement(SELECT)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Sessao(
                        rs.getString("token"),
                        UUID.fromString(rs.getString("id_usuario")),
                        rs.getTimestamp("expiracao").toLocalDateTime()
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar sessão: " + e.getMessage(), e);
        }
    }

    public void apagar(String token) {
        try (Connection c = ConexaoBanco.conectar();
             PreparedStatement ps = c.prepareStatement(DELETE)) {
            ps.setString(1, token);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao apagar sessão: " + e.getMessage(), e);
        }
    }
}