package Util;

import Domain.TipoResiduo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

public class GerarCodigoResiduo {

    private static final Type TIPO_MAPA = new TypeToken<Map<TipoResiduo, Integer>>() {}.getType();

    // estes dois campos precisam vir ANTES do "ultimosCodigos", porque o carregarArquivo() usa os dois
    private final Path caminho = Path.of(Constantes.CODIGO_PATH);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // último código gerado de cada tipo, lido do arquivo
    private final Map<TipoResiduo, Integer> ultimosCodigos = carregarArquivo();

    // próximo código = último do mesmo tipo + 5, para o resto (mod 5) continuar igual
    public String gerarCodigo(TipoResiduo tipoResiduo) {

        if (tipoResiduo == null)
            throw new IllegalArgumentException("O tipo do resíduo é obrigatório");

        Integer ultimo = ultimosCodigos.get(tipoResiduo);
        int proximo;

        if (ultimo == null) {
            // primeiro código do tipo: o próprio resto (computadores começa em 5, para não usar 0)
            proximo = tipoResiduo.getResto() == 0 ? 5 : tipoResiduo.getResto();
        } else {
            proximo = ultimo + 5;
        }

        ultimosCodigos.put(tipoResiduo, proximo);
        salvarArquivo();

        return String.format("%04d", proximo);
    }

    private Map<TipoResiduo, Integer> carregarArquivo() {
        Map<TipoResiduo, Integer> mapa = new EnumMap<>(TipoResiduo.class);

        if (!Files.exists(caminho)) {
            return mapa;
        }

        try (Reader leitura = Files.newBufferedReader(caminho, StandardCharsets.UTF_8)) {
            Map<TipoResiduo, Integer> lido = gson.fromJson(leitura, TIPO_MAPA);
            if (lido != null) {
                mapa.putAll(lido);
            }
            return mapa;
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível ler o arquivo " + caminho, e);
        }
    }

    private void salvarArquivo() {
        try {
            Path pasta = caminho.getParent();
            if (pasta != null) {
                Files.createDirectories(pasta);
            }

            try (Writer escrita = Files.newBufferedWriter(caminho, StandardCharsets.UTF_8)) {
                gson.toJson(ultimosCodigos, TIPO_MAPA, escrita);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Não foi possível salvar o arquivo " + caminho, e);
        }
    }
}