import {acao, api, campo, valor} from "./api.js";

export function iniciar() {
    acao(campo("form-login"), "submit", async () => {
        const usuario = await api("login", "POST", {email: valor("email"), senha: valor("senha"), manter: campo("manter").checked});
        location.assign(usuario.cargo === "USUARIO" ? "inicio.html" : "fila-coletas.html");
    });
}
