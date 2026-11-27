package backend_ecocode.Persistency.Banco;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

public class ConexaoBanco {

    private static final Dotenv env = Dotenv.load();

    private static final String URL = env.get("DB_URL");
    private static final String USUARIO = env.get("DB_USUARIO");
    private static final String SENHA = env.get("DB_SENHA");

    private static final ThreadLocal<Connection> transacaoAtual = new ThreadLocal<>();

    public record Acesso(Connection conexao, boolean compartilhada) implements AutoCloseable {
        @Override
        public void close() throws SQLException {
            if (!compartilhada) conexao.close();
        }
    }

    public static Acesso abrir() throws SQLException {
        Connection atual = transacaoAtual.get();
        return atual == null ? new Acesso(conectar(), false) : new Acesso(atual, true);
    }

    public static <T> T emTransacao(Supplier<T> operacao) {
        try (Connection conexao = conectar()) {
            conexao.setAutoCommit(false);
            transacaoAtual.set(conexao);
            try {
                T resultado = operacao.get();
                conexao.commit();
                return resultado;
            } catch (RuntimeException | SQLException erro) {
                conexao.rollback();
                throw erro;
            } finally {
                transacaoAtual.remove();
            }
        } catch (SQLException erro) {
            throw new IllegalStateException("Falha na transação do banco", erro);
        }
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}