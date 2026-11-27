package backend_ecocode.Persistency.Banco;

import backend_ecocode.Domain.Entities.PontoColeta;
import backend_ecocode.Domain.Enums.Bairro;
import backend_ecocode.Persistency.Persistencia;

import java.sql.*;
import java.util.*;

public class PersistenciaBancoPontoColeta implements Persistencia<PontoColeta> {

    private static final String SELECT_TODOS = "SELECT * FROM ponto_coleta";
    private static final String SELECT_IDS = "SELECT id FROM ponto_coleta";
    private static final String DELETE = "DELETE FROM ponto_coleta WHERE id = ?";

    private static final String UPSERT =
            "INSERT INTO ponto_coleta (id, nome, endereco, bairro) VALUES (?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE nome = VALUES(nome), " +
                    "endereco = VALUES(endereco), bairro = VALUES(bairro)";

    @Override
    public List<PontoColeta> carregar() {
        List<PontoColeta> pontos = new ArrayList<>();
        try (var acesso = ConexaoBanco.abrir();
             PreparedStatement ps = acesso.conexao().prepareStatement(SELECT_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pontos.add(linhaParaPonto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar pontos de coleta: " + e.getMessage(), e);
        }
        return pontos;
    }

    @Override
    public void salvar(List<PontoColeta> pontos) {
        try (var acesso = ConexaoBanco.abrir()) {
            removerExcluidos(acesso.conexao(), pontos);
            gravarTodos(acesso.conexao(), pontos);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pontos de coleta: " + e.getMessage(), e);
        }
    }

    // apaga do banco o que não está mais na lista
    private void removerExcluidos(Connection c, List<PontoColeta> pontos) throws SQLException {
        Set<String> idsNaLista = new HashSet<>();
        for (PontoColeta p : pontos) {
            idsNaLista.add(p.getId().toString());
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

    private void gravarTodos(Connection c, List<PontoColeta> pontos) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(UPSERT)) {
            for (PontoColeta p : pontos) {
                preencher(ps, p);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private PontoColeta linhaParaPonto(ResultSet rs) throws SQLException {
        return new PontoColeta(
                UUID.fromString(rs.getString("id")),
                rs.getString("nome"),
                rs.getString("endereco"),
                Bairro.valueOf(rs.getString("bairro"))
        );
    }

    private void preencher(PreparedStatement ps, PontoColeta p) throws SQLException {
        ps.setString(1, p.getId().toString());
        ps.setString(2, p.getNome());
        ps.setString(3, p.getEndereco());
        ps.setString(4, p.getBairro().name());
    }
}