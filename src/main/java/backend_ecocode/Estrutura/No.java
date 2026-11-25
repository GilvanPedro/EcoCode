package backend_ecocode.Estrutura;

public class No<T> {
    private T valor;
    private No<T> proximoNo;

    public No(T valor) {
        this.valor = valor;
        this.proximoNo = null;
    }

    public T getValor() { return valor; }
    public No<T> getProximoNo() { return proximoNo; }
    public void setProximoNo(No<T> proximoNo) { this.proximoNo = proximoNo; }
}