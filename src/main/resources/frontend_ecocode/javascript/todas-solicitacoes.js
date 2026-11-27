import {api, botao, campo, linha, situacoes} from "./api.js";

export async function iniciar(perfil) {
    let filtro = "todas";
    const botoes = document.querySelectorAll("button[data-situacao]");
    const corpo = campo("corpo-solicitacoes");
    function filtrar() {
        for (const tr of corpo.rows) tr.hidden = (filtro !== "todas" && tr.dataset.status !== filtro)
            || !tr.textContent.toLowerCase().includes(campo("busca").value.trim().toLowerCase());
        campo("sem-resultados").hidden = [...corpo.rows].some(tr => !tr.hidden);
    }
    for (const b of botoes) b.onclick = () => {
        filtro = b.dataset.situacao;
        botoes.forEach(botao => botao.classList.toggle("ativo", b === botao));
        filtrar();
    };
    campo("busca").addEventListener("input", filtrar);
    for (const p of await api("pedidos?equipe=true")) {
        const cancelar = botao("Cancelar", async () => {
            if (!confirm("Cancelar esta solicitação?")) return;
            await api("pedidos/" + p.id + "/cancelar", "POST");
            location.reload();
        });
        const tr = linha(corpo, [p.id, p.solicitante, p.material, new Date(p.dataSolicitacao).toLocaleString("pt-BR"), situacoes[p.status],
            perfil.cargo === "GERENTE" && ["PENDENTE", "EM_ROTA"].includes(p.status) ? cancelar : ""]);
        tr.dataset.status = p.status.toLowerCase().replace("_", "-");
    }
    filtrar();
}
