package backend_ecocode.Domain;

import backend_ecocode.Util.ValidacoesResiduos;

import java.util.UUID;

public class Residuo {

    private final UUID id;
    private String codigo;
    private TipoResiduo tipo_residuo;
    private int quantidade;
    private String descricao;
    private StatusResiduo status;

    private static final ValidacoesResiduos validar = new ValidacoesResiduos();

    public Residuo(String codigo, TipoResiduo tipo_residuo, int quantidade, String descricao){

        validar.validarResiduo(tipo_residuo, quantidade, codigo);

        this.id = UUID.randomUUID();
        this.codigo = codigo;
        this.tipo_residuo = tipo_residuo;
        this.quantidade = quantidade;
        this.descricao = (descricao == null) ? "" : descricao.trim();
        this.status = StatusResiduo.DISPONIVEL;
    }

    // construtor de carregamento do banco de dados
    public Residuo(UUID id, String codigo, TipoResiduo tipo_residuo, int quantidade, String descricao, StatusResiduo status){

        validar.validarResiduo(tipo_residuo, quantidade, codigo);

        this.id = id;
        this.codigo = codigo;
        this.tipo_residuo = tipo_residuo;
        this.quantidade = quantidade;
        this.descricao = (descricao == null) ? "" : descricao.trim();
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public StatusResiduo getStatus() {
        if(status == null) return StatusResiduo.DISPONIVEL;
        return status;
    }

    public String getCodigo(){
        return codigo;
    }

    public String getCategoriaResiduo(){
        return tipo_residuo.getDescricao();
    }

    public TipoResiduo getResiduo(){
        return tipo_residuo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setStatus(StatusResiduo status) {
        this.status = status;
    }
}
