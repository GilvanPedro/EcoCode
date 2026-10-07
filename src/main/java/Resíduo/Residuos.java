package Resíduo;

public class Residuos {

    private String codigo;
    private String tipo_residuo;
    private int quantidade;

    public Residuos(String codigo, String tipo_residuo, int quantidade){
        this.codigo = codigo;
        this.tipo_residuo = tipo_residuo;
        this.quantidade = quantidade;
    }

    public String getCodigo(){
        return codigo;
    }

    public String getTipo_residuo(){
        return tipo_residuo;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
