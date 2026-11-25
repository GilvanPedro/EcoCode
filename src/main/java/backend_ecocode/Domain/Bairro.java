package backend_ecocode.Domain;

public enum Bairro {
    SETOR_CENTRAL("Setor Central"),
    SETOR_OESTE("Setor Oeste"),
    SETOR_CAMPINAS("Setor Campinas"),
    SETOR_LESTE_UNIVERSITARIO("Setor Leste Universitário"),
    SETOR_BUENO("Setor Bueno"),
    SETOR_MARISTA("Setor Marista"),
    JARDIM_GOIAS("Jardim Goiás"),
    SETOR_PEDRO_LUDOVICO("Setor Pedro Ludovico"),
    JARDIM_AMERICA("Jardim América"),
    PARQUE_AMAZONIA("Parque Amazônia"),
    SETOR_JAO("Setor Jaó"),
    JARDIM_GUANABARA("Jardim Guanabara"),
    JARDIM_NOVO_MUNDO("Jardim Novo Mundo"),
    CIDADE_JARDIM("Cidade Jardim"),
    JARDIM_CURITIBA("Jardim Curitiba"),
    SETOR_FAICALVILLE("Setor Faiçalville"),
    RESIDENCIAL_CAMPOS_DOURADOS("Residencial Campos Dourados"),
    JARDIM_SAO_JOSE("Jardim São José"),
    PARQUE_ELDORADO_OESTE("Parque Eldorado Oeste");

    private final String descricao;

    Bairro(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}