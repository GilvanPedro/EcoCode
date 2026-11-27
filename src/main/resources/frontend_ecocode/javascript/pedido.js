import {acao, api, campo, elemento, situacoes} from "./api.js";

export async function iniciar() {
    const id = new URLSearchParams(location.search).get("id");
    const pedido = (await api("pedidos")).find(p => p.id === id);
    if (!pedido) throw new Error("Pedido não encontrado.");
    const residuo = (await api("residuos")).find(r => r.id === pedido.residuoId);
    campo("titulo").textContent = "Pedido " + id.slice(0, 8);
    const dados = campo("detalhes");
    dados.append(elemento("h2", situacoes[pedido.status]), elemento("p", new Date(pedido.dataSolicitacao).toLocaleString("pt-BR")));
    campo("etiqueta-pedido").textContent = situacoes[pedido.status];
    campo("etiqueta-pedido").className = "etiqueta " + pedido.status.toLowerCase().replace("_","-");
    const etapas = pedido.status === "CANCELADA" ? ["PENDENTE","CANCELADA"] : ["PENDENTE","EM_ROTA","CONCLUIDA"];
    for (const etapa of etapas) campo("etapas").append(elemento("li", situacoes[etapa], etapa === pedido.status ? "atual" : ""));
    if (pedido.posicao) dados.append(elemento("p", "Posição na fila: " + pedido.posicao));
    if (residuo) campo("materiais").append(elemento("li", [residuo.codigo, residuo.categoriaResiduo, residuo.descricao, residuo.quantidade + " un."].join(" · ")));
    campo("botao-cancelar").hidden = pedido.status !== "PENDENTE";
    acao(campo("botao-cancelar"), "click", async () => {
        if (!confirm("Cancelar esta solicitação?")) return;
        await api("pedidos/" + id + "/cancelar", "POST");
        location.reload();
    });
}
