import {acao, api, aviso, campo, valor} from "./api.js";

export function iniciar() {
    const form = campo("form-residuo");
    acao(form, "submit", async () => {
        const r = await api("residuos", "POST", {tipo: valor("categoria"), quantidade: Number(valor("quantidade")), descricao: valor("descricao")});
        form.reset();
        aviso("Resíduo cadastrado. Código " + r.codigo);
    });
}
