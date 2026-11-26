package backend_ecocode.Persistency.Banco;

import backend_ecocode.Domain.Entities.Residuo;
import backend_ecocode.Domain.Enums.StatusResiduo;
import backend_ecocode.Domain.Enums.TipoResiduo;
import backend_ecocode.Persistency.Persistencia;

import java.sql.*;
import java.util.*;

public class PersistenciaBancoResiduo implements Persistencia<Residuo> {

    private static final String SELECT_TODOS = "SELECT * FROM residuos";
    private static final String SELECT_IDS = "SELECT id FROM residuos";
    private static final String DELETE = "DELETE FROM residuos WHERE id = ?";

    private static final String UPSERT =
            "INSERT INTO residuos (id, id_usuario, codigo, tipo_residuo, quantidade, " +
                    "descricao, status) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantidade = VALUES(quantidade), " +
                    "descricao = VALUES(descricao), status = VALUES(status)";

    @Override
    public List<Residuo> carregar() {
        List<Residuo> residuos = new ArrayList<>();
        try (Connection c = ConexaoBanco.conectar();
             PreparedStatement ps = c.prepareStatement(SELECT_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                residuos.add(linhaParaResiduo(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar resíduos: " + e.getMessage(), e);
        }
        return residuos;
    }

    @Override
    public void salvar(List<Residuo> residuos) {
        try (Connection c = ConexaoBanco.conectar()) {
            removerExcluidos(c, residuos);
            gravarTodos(c, residuos);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar resíduos: " + e.getMessage(), e);
        }
    }

    // apaga do banco o que não está mais na lista
    private void removerExcluidos(Connection c, List<Residuo> residuos) throws SQLException {
        Set<String> idsNaLista = new HashSet<>();
        for (Residuo r : residuos) {
            idsNaLista.add(r.getId().toString());
        }

        try (PreparedStatement select = c.prepareStatement(SELECT_IDS);
             ResultSet rs = select.executeQuery();
             PreparedStatement delete = c.prepareStatement(DELETE)) {

            while (rs.next()) {
                String id = rs.getString("id");
                if (!idsNaLista.contains(id)) {
                    delete.setString(1, id);
                    delete.addBatch();
                }
            }
            delete.executeBatch();
        }
    }

    private void gravarTodos(Connection c, List<Residuo> residuos) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(UPSERT)) {
            for (Residuo r : residuos) {
                preencher(ps, r);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private Residuo linhaParaResiduo(ResultSet rs) throws SQLException {
        return new Residuo(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("id_usuario")),
                rs.getString("codigo"),
                TipoResiduo.valueOf(rs.getString("tipo_residuo")),
                rs.getInt("quantidade"),
                rs.getString("descricao"),
                StatusResiduo.valueOf(rs.getString("status"))
        );
    }

    private void preencher(PreparedStatement ps, Residuo r) throws SQLException {
        ps.setString(1, r.getId().toString());
        ps.setString(2, r.getUsuarioId().toString());
        ps.setString(3, r.getCodigo());
        ps.setString(4, r.getResiduo().name());
        ps.setInt(5, r.getQuantidade());
        ps.setString(6, r.getDescricao());
        ps.setString(7, r.getStatus().name());
    }
}