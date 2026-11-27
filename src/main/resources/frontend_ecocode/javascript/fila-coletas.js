import {acao, api, botao, campo, elemento} from "./api.js";

export async function iniciar(perfil) {
    const pedidos = await api("fila");
    const pendentes = pedidos.filter(p => p.status === "PENDENTE").sort((a,b) => a.posicao - b.posicao);
    campo("primeiro").textContent = pendentes.length ? pendentes[0].material : "Nenhum pedido pendente";
    const mostrar = (pedido, lista) => {
        const item = elemento("li", "", "bloco-linha");
        item.append(elemento("p", [pedido.solicitante, pedido.bairro, pedido.material].join(" · "), "linha-titulo"));
        lista.append(item);
        return item;
    };
    for (const p of pendentes) mostrar(p, campo("pendentes"));
    for (const p of pedidos.filter(p => p.status === "EM_ROTA")) {
        const item = mostrar(p, campo("rotas"));
        item.append(botao("Concluir coleta", async () => {
            await api("pedidos/" + p.id + "/concluir", "POST");
            location.reload();
        }));
        if (perfil.cargo === "GERENTE") item.append(botao("Cancelar", async () => {
            if (!confirm("Cancelar esta coleta?")) return;
            await api("pedidos/" + p.id + "/cancelar", "POST");
            location.reload();
        }));
    }
    campo("proxima").disabled = !pendentes.length;
    acao(campo("proxima"), "click", async () => { await api("pedidos/proximo", "POST"); location.reload(); });
}
