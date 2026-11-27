import {acao, api, campo, preencherBairros, valor} from "./api.js";

export async function iniciar() {
    await preencherBairros(campo("bairro"));
    acao(campo("form-cadastro"), "submit", async () => {
        if (valor("senha") !== valor("confirmar")) throw new Error("A confirmação deve coincidir com a senha.");
        const nascimento = valor("nascimento").split("-").reverse().join("/");
        await api("contas", "POST", {nome: valor("nome"), cpf: valor("cpf"), email: valor("email"),
            bairro: valor("bairro"), senha: valor("senha"), nascimento});
        location.assign("login.html");
    });
}
