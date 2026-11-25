package Estrutura;

import java.util.ArrayList;
import java.util.List;

// pilha (LIFO): o último que entra é o primeiro que sai
public class PilhaEncadeada<T> {

    private No<T> topo;
    private int tamanho;

    // coloca no topo
    public void empilhar(T valor) {
        if (valor == null)
            throw new IllegalArgumentException("Não é possível empilhar um valor nulo");

        No<T> novo = new No<>(valor);
        novo.setProximoNo(topo); // o novo aponta pro antigo topo
        topo = novo;
        tamanho++;
    }

    // tira do topo e devolve
    public T desempilhar() {
        if (estaVazia())
            throw new IllegalStateException("A pilha está vazia");

        T valor = topo.getValor();
        topo = topo.getProximoNo();
        tamanho--;
        return valor;
    }

    // olha o topo sem tirar
    public T espiar() {
        if (estaVazia())
            throw new IllegalStateException("A pilha está vazia");
        return topo.getValor();
    }

    public boolean estaVazia() {
        return topo == null;
    }

    public int tamanho() {
        return tamanho;
    }

    // do topo pra baixo: o mais recente primeiro
    public List<T> listar() {
        List<T> lista = new ArrayList<>();
        No<T> atual = topo;
        while (atual != null) {
            lista.add(atual.getValor());
            atual = atual.getProximoNo();
        }
        return lista;
    }
}