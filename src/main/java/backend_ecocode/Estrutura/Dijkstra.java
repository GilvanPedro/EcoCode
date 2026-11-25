package Estrutura;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// menor caminho a partir de uma origem, usando o Grafo
public class Dijkstra<T> {

    private final Grafo<T> grafo;
    private final Map<T, Double> distancias = new HashMap<>();
    private final Set<T> visitados = new HashSet<>();

    public Dijkstra(Grafo<T> grafo) {
        this.grafo = grafo;
    }

    public Map<T, Double> calcular(T origem) {
        if (!grafo.contem(origem))
            throw new IllegalArgumentException("Vértice de origem não existe no grafo");

        iniciar(origem);

        T atual = maisProximoNaoVisitado();
        while (atual != null) {
            visitados.add(atual);
            relaxarVizinhos(atual);
            atual = maisProximoNaoVisitado();
        }

        return distancias;
    }

    // todo mundo começa "infinito", menos a origem
    private void iniciar(T origem) {
        distancias.clear();
        visitados.clear();
        for (T vertice : grafo.getVertices()) {
            distancias.put(vertice, Double.POSITIVE_INFINITY);
        }
        distancias.put(origem, 0.0);
    }

    // o vértice ainda aberto com a menor distância (null quando acabar)
    private T maisProximoNaoVisitado() {
        T melhor = null;
        for (T vertice : grafo.getVertices()) {
            if (visitados.contains(vertice)) continue;
            if (distancias.get(vertice) == Double.POSITIVE_INFINITY) continue;
            if (melhor == null || distancias.get(vertice) < distancias.get(melhor)) {
                melhor = vertice;
            }
        }
        return melhor;
    }

    // passar pelo atual deixa algum vizinho mais perto?
    private void relaxarVizinhos(T atual) {
        for (Map.Entry<T, Double> aresta : grafo.getVizinhos(atual).entrySet()) {
            T vizinho = aresta.getKey();
            double passandoPeloAtual = distancias.get(atual) + aresta.getValue();
            if (passandoPeloAtual < distancias.get(vizinho)) {
                distancias.put(vizinho, passandoPeloAtual);
            }
        }
    }
}