package backend_ecocode.Domain.Enums;

public enum AcaoLog {

    // usuários
    CADASTROU_USUARIO("Cadastrou um usuário"),
    EDITOU_USUARIO("Editou os dados de um usuário"),
    PROMOVEU_USUARIO("Alterou o cargo de um usuário"),
    REMOVEU_USUARIO("Removeu um usuário"),

    // pontos de coleta
    CADASTROU_PONTO("Cadastrou um ponto de coleta"),
    EDITOU_PONTO("Editou um ponto de coleta"),
    EXCLUIU_PONTO("Excluiu um ponto de coleta"),

    // solicitações
    PEGOU_SOLICITACAO("Pegou uma solicitação da fila"),
    CONCLUIU_SOLICITACAO("Concluiu uma coleta"),
    CANCELOU_SOLICITACAO("Cancelou uma solicitação");

    private final String descricao;

    AcaoLog(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}