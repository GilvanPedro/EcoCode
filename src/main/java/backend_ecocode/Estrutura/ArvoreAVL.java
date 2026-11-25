package backend_ecocode.Estrutura;

import java.util.ArrayList;
import java.util.List;

public class ArvoreAVL<K extends Comparable<K>, V> {

    private NoAVL<K, V> raiz;
    private int tamanho;

    // altura de um nó (vazio = 0)
    private int altura(NoAVL<K, V> no) {
        return (no == null) ? 0 : no.altura;
    }

    // recalcula a altura depois que os filhos mudam
    private void atualizarAltura(NoAVL<K, V> no) {
        no.altura = 1 + Math.max(altura(no.esquerda), altura(no.direita));
    }

    // positivo = pesado pra esquerda, negativo = pesado pra direita
    private int fatorBalanceamento(NoAVL<K, V> no) {
        return (no == null) ? 0 : altura(no.esquerda) - altura(no.direita);
    }

    private NoAVL<K, V> rotacaoDireita(NoAVL<K, V> y) {
        NoAVL<K, V> x = y.esquerda;
        NoAVL<K, V> b = x.direita;

        x.direita = y;
        y.esquerda = b;

        atualizarAltura(y); // y primeiro, porque agora ele tá embaixo
        atualizarAltura(x);
        return x;
    }

    private NoAVL<K, V> rotacaoEsquerda(NoAVL<K, V> x) {
        NoAVL<K, V> y = x.direita;
        NoAVL<K, V> b = y.esquerda;

        y.esquerda = x;
        x.direita = b;

        atualizarAltura(x);
        atualizarAltura(y);
        return y;
    }

    // confere o nó e gira se ele tiver tortado
    private NoAVL<K, V> balancear(NoAVL<K, V> no) {
        atualizarAltura(no);
        int fator = fatorBalanceamento(no);

        // pesado pra esquerda
        if (fator > 1) {
            if (fatorBalanceamento(no.esquerda) < 0) {
                no.esquerda = rotacaoEsquerda(no.esquerda); // caso esquerda-direita
            }
            return rotacaoDireita(no);                      // caso esquerda-esquerda
        }

        // pesado pra direita
        if (fator < -1) {
            if (fatorBalanceamento(no.direita) > 0) {
                no.direita = rotacaoDireita(no.direita);    // caso direita-esquerda
            }
            return rotacaoEsquerda(no);                     // caso direita-direita
        }

        return no; // tá equilibrado, não mexe
    }

    public void inserir(K chave, V valor) {
        if (chave == null)
            throw new IllegalArgumentException("A chave não pode ser nula");
        raiz = inserir(raiz, chave, valor);
    }

    private NoAVL<K, V> inserir(NoAVL<K, V> no, K chave, V valor) {
        if (no == null) {
            tamanho++;
            return new NoAVL<>(chave, valor);
        }

        int comparacao = chave.compareTo(no.chave);

        if (comparacao < 0) {
            no.esquerda = inserir(no.esquerda, chave, valor);
        } else if (comparacao > 0) {
            no.direita = inserir(no.direita, chave, valor);
        } else {
            no.valor = valor; // chave repetida: só atualiza o valor
            return no;
        }

        return balancear(no);
    }

    public V buscar(K chave) {
        NoAVL<K, V> atual = raiz;

        while (atual != null) {
            int comparacao = chave.compareTo(atual.chave);
            if (comparacao == 0) return atual.valor;
            atual = (comparacao < 0) ? atual.esquerda : atual.direita;
        }

        return null; // não achou
    }

    public void remover(K chave) {
        if (chave == null)
            throw new IllegalArgumentException("A chave não pode ser nula");
        raiz = remover(raiz, chave);
    }

    private NoAVL<K, V> remover(NoAVL<K, V> no, K chave) {
        if (no == null) return null; // não achou, não faz nada

        int comparacao = chave.compareTo(no.chave);

        if (comparacao < 0) {
            no.esquerda = remover(no.esquerda, chave);
        } else if (comparacao > 0) {
            no.direita = remover(no.direita, chave);
        } else {
            // achou o nó

            // casos 1 e 2: zero ou um filho
            if (no.esquerda == null || no.direita == null) {
                tamanho--;
                return (no.esquerda != null) ? no.esquerda : no.direita;
            }

            // caso 3: dois filhos, usa o sucessor
            NoAVL<K, V> sucessor = menor(no.direita);
            no.chave = sucessor.chave;
            no.valor = sucessor.valor;
            no.direita = remover(no.direita, sucessor.chave);
        }

        return balancear(no);
    }

    // o nó mais à esquerda é o de menor chave
    private NoAVL<K, V> menor(NoAVL<K, V> no) {
        while (no.esquerda != null) {
            no = no.esquerda;
        }
        return no;
    }

    public boolean contem(K chave) {
        return buscar(chave) != null;
    }

    // percurso em ordem: devolve os valores ordenados pela chave
    public List<V> emOrdem() {
        List<V> lista = new ArrayList<>();
        emOrdem(raiz, lista);
        return lista;
    }

    private void emOrdem(NoAVL<K, V> no, List<V> lista) {
        if (no == null) return;
        emOrdem(no.esquerda, lista);
        lista.add(no.valor);
        emOrdem(no.direita, lista);
    }

    public int altura() {
        return altura(raiz);
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean estaVazia() {
        return raiz == null;
    }
}