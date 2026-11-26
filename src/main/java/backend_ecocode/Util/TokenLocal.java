package backend_ecocode.Util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TokenLocal {

    private final Path caminho = Path.of(Constantes.PATH + "/token.txt");

    public void guardar(String token) {
        try {
            Files.createDirectories(caminho.getParent());
            Files.writeString(caminho, token);
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível guardar o token", e);
        }
    }

    public String ler() {
        try {
            return Files.exists(caminho) ? Files.readString(caminho).trim() : null;
        } catch (IOException e) {
            return null;
        }
    }

    public void apagar() {
        try {
            Files.deleteIfExists(caminho);
        } catch (IOException ignored) { }
    }
}