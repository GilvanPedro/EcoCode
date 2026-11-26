package backend_ecocode.Persistency.Banco;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final Dotenv env = Dotenv.load();

    private static final String URL = env.get("DB_URL");
    private static final String USUARIO = env.get("DB_USUARIO");
    private static final String SENHA = env.get("DB_SENHA");

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}