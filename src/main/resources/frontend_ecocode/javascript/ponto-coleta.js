import {acao, api, botao, campo, linha, preencherBairros, valor} from "./api.js";

export async function iniciar() {
    await preencherBairros(campo("bairro"));
    let editando = null;
    for (const p of await api("pontos")) {
        linha(campo("corpo-pontos"), [p.nome, p.bairro.replaceAll("_", " "), p.endereco, botao("Editar", () => {
            editando = p.id;
            campo("titulo-form").textContent = "Editar ponto de coleta";
            campo("nome").value = p.nome;
            campo("endereco").value = p.endereco;
            campo("bairro").value = p.bairro;
            campo("nome").focus();
        })]);
    }
    acao(campo("form-ponto"), "submit", async () => {
        await api(editando ? "pontos/" + editando : "pontos", editando ? "PUT" : "POST",
            {nome: valor("nome"), endereco: valor("endereco"), bairro: valor("bairro")});
        location.reload();
    });
}
