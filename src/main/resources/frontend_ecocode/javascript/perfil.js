import {acao, api, campo, preencherBairros, valor} from "./api.js";

export async function iniciar(perfil) {
    await preencherBairros(campo("bairro"), perfil.bairro);
    for (const id of ["nome", "email", "cpf", "cargo"]) campo(id).defaultValue = perfil[id];
    campo("bairro").selectedOptions[0].defaultSelected = true;
    acao(campo("form-perfil"), "submit", async () => {
        if (valor("senha") !== valor("confirmar")) throw new Error("Confira a confirmação da senha.");
        await api("perfil", "PUT", {nome: valor("nome"), email: valor("email"), senha: valor("senha"), bairro: valor("bairro")});
        location.reload();
    });
}
