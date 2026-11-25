package backend_ecocode.Domain.Entities;

import backend_ecocode.Domain.Enums.StatusResiduo;
import backend_ecocode.Domain.Enums.TipoResiduo;
import backend_ecocode.Util.ValidacoesResiduos;

import java.util.UUID;

public class Residuo {

    private final UUID id;
    private final UUID usuarioId;
    private String codigo;
    private TipoResiduo tipo_residuo;
    private int quantidade;
    private String descricao;
    private StatusResiduo status;

    private static final ValidacoesResiduos validar = new ValidacoesResiduos();

    public Residuo(UUID usuarioId, String codigo, TipoResiduo tipo_residuo, int quantidade, String descricao){

        validar.validarResiduo(tipo_residuo, quantidade, codigo);

        this.usuarioId = usuarioId;
        this.id = UUID.randomUUID();
        this.codigo = codigo;
        this.tipo_residuo = tipo_residuo;
        this.quantidade = quantidade;
        this.descricao = (descricao == null) ? "" : descricao.trim();
        this.status = StatusResiduo.DISPONIVEL;
    }

    // construtor de carregamento do banco de dados
    public Residuo(UUID id, UUID usuarioId, String codigo, TipoResiduo tipo_residuo, int quantidade, String descricao, StatusResiduo status){

        validar.validarResiduo(tipo_residuo, quantidade, codigo);

        this.id = id;
        this.usuarioId = usuarioId;
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

    public UUID getUsuarioId() {
        return usuarioId;
    }
}
