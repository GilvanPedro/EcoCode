package backend_ecocode.Estrutura;

import java.util.ArrayList;
import java.util.List;

public class FilaEncadeada<T> {
    private No<T> inicio;
    private No<T> fim;
    private int tamanho;

    public void enfileirar(T valor) {
        No<T> novoNo = new No<>(valor);

        if (inicio == null) {
            // fila vazia: o novo é o primeiro e o último ao mesmo tempo
            inicio = novoNo;
        } else {
            // o último atual passa a apontar pro novo
            fim.setProximoNo(novoNo);
        }

        fim = novoNo;
        tamanho++;
    }

    public T desenfileirar() {
        if (estaVazia()) {
            throw new IllegalStateException("A fila está vazia");
        }

        T valor = inicio.getValor();
        inicio = inicio.getProximoNo();   // o segundo vira o primeiro
        tamanho--;

        if (inicio == null) {
            // a fila esvaziou: o fim não pode continuar apontando pro nó que saiu
            fim = null;
        }

        return valor;
    }

    public T espiar() {
        if (estaVazia()) {
            throw new IllegalStateException("A fila está vazia");
        }
        return inicio.getValor();
    }

    public boolean estaVazia() {
        return inicio == null;
    }

    public int tamanho() {
        return tamanho;
    }

    // devolve os valores na ordem, sem tirar ninguém da fila
    public List<T> listar() {
        List<T> valores = new ArrayList<>();
        No<T> atual = inicio;

        while (atual != null) {
            valores.add(atual.getValor());
            atual = atual.getProximoNo();
        }

        return valores;
    }
}