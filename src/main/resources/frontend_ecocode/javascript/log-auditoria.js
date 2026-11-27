import {api, campo, linha} from "./api.js";

export async function iniciar() {
    const logs = await api("log");
    const filtro = campo("filtro-acao");
    for (const acao of new Set(logs.map(r => r.acaoLog))) filtro.add(new Option(acao.replaceAll("_", " "), acao));
    function atualizar() {
        const termo = campo("busca").value.toLocaleLowerCase();
        const corpo = campo("corpo-log");
        corpo.replaceChildren();
        for (const r of logs) {
            if (filtro.value && filtro.value !== r.acaoLog) continue;
            if (!(r.nomeUsuario + " " + r.alvo).toLocaleLowerCase().includes(termo)) continue;
            linha(corpo, [new Date(r.dataRegistro).toLocaleString("pt-BR"), r.nomeUsuario, r.acaoLog.replaceAll("_", " "), r.alvo]);
        }
        campo("contagem").textContent = corpo.rows.length + " de " + logs.length + " registros";
        campo("sem-resultados").hidden = corpo.rows.length > 0;
    }
    filtro.addEventListener("change", atualizar);
    campo("busca").addEventListener("input", atualizar);
    atualizar();
}
