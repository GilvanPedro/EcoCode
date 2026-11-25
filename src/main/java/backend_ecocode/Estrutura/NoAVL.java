package backend_ecocode.Estrutura;

// um nó da árvore AVL: chave, valor, filhos e a altura dele
class NoAVL<K, V> {
    K chave;
    V valor;
    NoAVL<K, V> esquerda;
    NoAVL<K, V> direita;
    int altura;

    NoAVL(K chave, V valor) {
        this.chave = chave;
        this.valor = valor;
        this.altura = 1; // nó novo sempre nasce como folha
    }

    public K getChave() {
        return chave;
    }

    public void setChave(K chave) {
        this.chave = chave;
    }

    public V getValor() {
        return valor;
    }

    public NoAVL<K, V> getEsquerda() {
        return esquerda;
    }

    public void setEsquerda(NoAVL<K, V> esquerda) {
        this.esquerda = esquerda;
    }

    public NoAVL<K, V> getDireita() {
        return direita;
    }

    public void setDireita(NoAVL<K, V> direita) {
        this.direita = direita;
    }

    public int getAltura() {
        return altura;
    }
}