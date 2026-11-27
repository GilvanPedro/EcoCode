import {api, campo, elemento, link, situacoes} from "./api.js";

export async function iniciar(perfil) {
    const [residuos, pedidos, ponto] = await Promise.all([api("residuos"), api("pedidos"), api("pontos/proximo")]);
    campo("saudacao").textContent = "Olá, " + perfil.nome;
    campo("data-hoje").textContent = new Date().toLocaleDateString("pt-BR");
    const contagens = [residuos.length, pedidos.filter(p => ["PENDENTE", "EM_ROTA"].includes(p.status)).length,
        pedidos.filter(p => p.status === "CONCLUIDA").length, ponto?.nome || "Nenhum cadastrado"];
    document.querySelectorAll(".indicadores .numero").forEach((n, i) => n.textContent = contagens[i]);
    const lista = document.querySelector(".recentes ul");
    for (const p of pedidos.sort((a,b) => b.dataSolicitacao.localeCompare(a.dataSolicitacao)).slice(0, 3)) {
        const item = elemento("li");
        item.append(link("Pedido " + p.id.slice(0,8), "pedido.html?id=" + p.id), elemento("span", situacoes[p.status]));
        lista.append(item);
    }
    if (!lista.children.length) lista.append(elemento("li", "Nenhuma solicitação registrada."));
}
