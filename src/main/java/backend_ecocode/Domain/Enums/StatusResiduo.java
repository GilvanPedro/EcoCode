package backend_ecocode.Domain.Enums;

public enum StatusResiduo {
    DISPONIVEL("Disponível"),
    EM_SOLICITACAO("Em solicitação"),
    DESCARTADO("Descartado");

    private final String descricao;

    StatusResiduo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}