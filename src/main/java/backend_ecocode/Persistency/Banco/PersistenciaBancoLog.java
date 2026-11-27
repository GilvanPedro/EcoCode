package backend_ecocode.Persistency.Banco;

import backend_ecocode.Domain.Entities.RegistroLog;
import backend_ecocode.Domain.Enums.AcaoLog;
import backend_ecocode.Persistency.Persistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersistenciaBancoLog implements Persistencia<RegistroLog> {

    private static final String SELECT_TODOS =
            "SELECT l.*, u.nome_completo FROM logs_auditoria l " +
                    "JOIN usuarios u ON u.id = l.id_usuario";

    private static final String INSERT =
            "INSERT IGNORE INTO logs_auditoria (id, id_usuario, acao_log, alvo, data_registro) " +
                    "VALUES (?, ?, ?, ?, ?)";

    @Override
    public List<RegistroLog> carregar() {
        List<RegistroLog> registros = new ArrayList<>();
        try (var acesso = ConexaoBanco.abrir();
             PreparedStatement ps = acesso.conexao().prepareStatement(SELECT_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                registros.add(linhaParaRegistro(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar o log: " + e.getMessage(), e);
        }
        return registros;
    }

    @Override
    public void salvar(List<RegistroLog> registros) {
        try (var acesso = ConexaoBanco.abrir();
             PreparedStatement ps = acesso.conexao().prepareStatement(INSERT)) {

            for (RegistroLog r : registros) {
                preencher(ps, r);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar o log: " + e.getMessage(), e);
        }
    }

    private RegistroLog linhaParaRegistro(ResultSet rs) throws SQLException {
        return new RegistroLog(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("id_usuario")),
                rs.getString("nome_completo"),
                rs.getTimestamp("data_registro").toLocalDateTime(),
                AcaoLog.valueOf(rs.getString("acao_log")),
                rs.getString("alvo")
        );
    }

    private void preencher(PreparedStatement ps, RegistroLog r) throws SQLException {
        ps.setString(1, r.getId().toString());
        ps.setString(2, r.getUsuarioId().toString());
        ps.setString(3, r.getAcaoLog().name());
        ps.setString(4, r.getAlvo());
        ps.setTimestamp(5, Timestamp.valueOf(r.getDataRegistro()));
    }
}