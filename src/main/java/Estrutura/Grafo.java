package Estrutura;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// grafo não direcionado e com peso, guardado como lista de adjacência
public class Grafo<T> {

    // pra cada vértice, os vizinhos e a distância até cada um
    private final Map<T, Map<T, Double>> adjacencias = new HashMap<>();

    public void adicionarVertice(T vertice) {
        if (vertice == null) {
            throw new IllegalArgumentException("O vértice não pode ser nulo");
        }
        adjacencias.putIfAbsent(vertice, new HashMap<>());
    }

    public void adicionarAresta(T origem, T destino, double distancia) {
        if (distancia < 0) {
            throw new IllegalArgumentException("A distância não pode ser negativa");
        }
        if (origem.equals(destino)) {
            throw new IllegalArgumentException("Um vértice não pode ligar nele mesmo");
        }

        adicionarVertice(origem);
        adicionarVertice(destino);

        // mão dupla: a aresta vale nos dois sentidos
        adjacencias.get(origem).put(destino, distancia);
        adjacencias.get(destino).put(origem, distancia);
    }

    public Map<T, Double> getVizinhos(T vertice) {
        if (!adjacencias.containsKey(vertice)) {
            throw new IllegalArgumentException("Vértice não existe no grafo");
        }
        return adjacencias.get(vertice);
    }

    public Set<T> getVertices() {
        return adjacencias.keySet();
    }

    public boolean contem(T vertice) {
        return adjacencias.containsKey(vertice);
    }
}