import {acao, api, aviso, campo, valor} from "./api.js";

export function iniciar() {
    let pessoa;
    acao(campo("form-busca"), "submit", async () => {
        campo("resultado").hidden = true;
        pessoa = await api("usuarios?cpf=" + encodeURIComponent(valor("cpf")));
        campo("avatar").textContent = pessoa.nome.charAt(0);
        campo("pessoa-nome").textContent = pessoa.nome;
        campo("pessoa-info").textContent = pessoa.email;
        campo("cargo-atual").textContent = pessoa.cargo;
        campo("form-cargo").elements.cargo.value = pessoa.cargo;
        campo("resultado").hidden = false;
    });
    campo("cpf").addEventListener("input", () => campo("resultado").hidden = true);
    campo("cancelar-busca").onclick = () => campo("resultado").hidden = true;
    acao(campo("form-cargo"), "submit", async () => {
        const u = await api("cargo", "PUT", {cpf: pessoa.cpf, cargo: campo("form-cargo").elements.cargo.value});
        campo("cargo-atual").textContent = u.cargo;
        aviso(u.nome + " agora tem o cargo " + u.cargo + ".");
    });
}
