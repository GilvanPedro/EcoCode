package Model.Entity;

import Domain.TipoResiduo;

public class Residuo {

    private String codigo;
    private TipoResiduo tipo_residuo;
    private int quantidade;

    public Residuo(String codigo, TipoResiduo tipo_residuo, int quantidade){
        this.codigo = codigo;
        this.tipo_residuo = tipo_residuo;
        this.quantidade = quantidade;
    }

    public String getCodigo(){
        return codigo;
    }

    public String getTipoResiduo(){
        return tipo_residuo.getDescricao();
    }

    public TipoResiduo getResiduo(){
        return tipo_residuo;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
