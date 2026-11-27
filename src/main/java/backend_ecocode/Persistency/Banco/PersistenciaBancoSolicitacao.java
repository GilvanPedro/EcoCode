package backend_ecocode.Persistency.Banco;

import backend_ecocode.Domain.Entities.Solicitacao;
import backend_ecocode.Domain.Enums.StatusSolicitacao;
import backend_ecocode.Persistency.Persistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersistenciaBancoSolicitacao implements Persistencia<Solicitacao> {

    private static final String SELECT_TODOS = "SELECT * FROM solicitacoes";

    private static final String UPSERT =
            "INSERT INTO solicitacoes (id, id_usuario, id_residuo, status, data_solicitacao) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE status = VALUES(status)";

    @Override
    public List<Solicitacao> carregar() {
        List<Solicitacao> solicitacoes = new ArrayList<>();
        try (var acesso = ConexaoBanco.abrir();
             PreparedStatement ps = acesso.conexao().prepareStatement(SELECT_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                solicitacoes.add(linhaParaSolicitacao(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar solicitações: " + e.getMessage(), e);
        }
        return solicitacoes;
    }

    @Override
    public void salvar(List<Solicitacao> solicitacoes) {
        try (var acesso = ConexaoBanco.abrir();
             PreparedStatement ps = acesso.conexao().prepareStatement(UPSERT)) {

            for (Solicitacao s : solicitacoes) {
                preencher(ps, s);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar solicitações: " + e.getMessage(), e);
        }
    }

    private Solicitacao linhaParaSolicitacao(ResultSet rs) throws SQLException {
        return new Solicitacao(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("id_usuario")),
                UUID.fromString(rs.getString("id_residuo")),
                StatusSolicitacao.valueOf(rs.getString("status")),
                rs.getTimestamp("data_solicitacao").toLocalDateTime()
        );
    }

    private void preencher(PreparedStatement ps, Solicitacao s) throws SQLException {
        ps.setString(1, s.getId().toString());
        ps.setString(2, s.getUsuarioId().toString());
        ps.setString(3, s.getResiduoId().toString());
        ps.setString(4, s.getStatus().name());
        ps.setTimestamp(5, Timestamp.valueOf(s.getDataSolicitacao()));
    }
}