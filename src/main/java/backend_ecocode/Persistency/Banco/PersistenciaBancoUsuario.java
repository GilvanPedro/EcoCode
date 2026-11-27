package backend_ecocode.Persistency.Banco;

import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.Bairro;
import backend_ecocode.Domain.Enums.CargoUsuario;
import backend_ecocode.Persistency.Persistencia;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PersistenciaBancoUsuario implements Persistencia<Usuario> {

    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String SELECT_TODOS = "SELECT * FROM usuarios";

    private static final String UPSERT =
            "INSERT INTO usuarios (id, nome_completo, cpf, email, senha_criptografada, " +
                    "cargo, data_nascimento, bairro) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE nome_completo = VALUES(nome_completo), " +
                    "email = VALUES(email), senha_criptografada = VALUES(senha_criptografada), " +
                    "cargo = VALUES(cargo), bairro = VALUES(bairro)";

    @Override
    public List<Usuario> carregar() {
        List<Usuario> usuarios = new ArrayList<>();
        try (var acesso = ConexaoBanco.abrir();
             PreparedStatement ps = acesso.conexao().prepareStatement(SELECT_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(linhaParaUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar usuários: " + e.getMessage(), e);
        }
        return usuarios;
    }

    @Override
    public void salvar(List<Usuario> usuarios) {
        try (var acesso = ConexaoBanco.abrir();
             PreparedStatement ps = acesso.conexao().prepareStatement(UPSERT)) {

            for (Usuario u : usuarios) {
                preencher(ps, u);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuários: " + e.getMessage(), e);
        }
    }

    private Usuario linhaParaUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                UUID.fromString(rs.getString("id")),
                rs.getString("nome_completo"),
                rs.getString("cpf"),
                rs.getString("email"),
                rs.getString("senha_criptografada"),
                CargoUsuario.valueOf(rs.getString("cargo")),
                rs.getDate("data_nascimento").toLocalDate().format(FORMATO_BR),
                Bairro.valueOf(rs.getString("bairro"))
        );
    }

    private void preencher(PreparedStatement ps, Usuario u) throws SQLException {
        ps.setString(1, u.getId().toString());
        ps.setString(2, u.getNomeCompleto());
        ps.setString(3, u.getCpf());
        ps.setString(4, u.getEmail());
        ps.setString(5, u.getSenhaCriptografada());
        ps.setString(6, u.getCargoUsuario().name());
        LocalDate nascimento = LocalDate.parse(u.getDataNascimento(), FORMATO_BR);
        ps.setDate(7, Date.valueOf(nascimento));
        ps.setString(8, u.getBairro().name());
    }
}