import {acao, api, campo, elemento} from "./api.js";

export async function iniciar() {
    const [residuos, ponto] = await Promise.all([api("residuos"), api("pontos/proximo")]);
    const grupo = campo("itens");
    for (const r of residuos.filter(r => r.status === "DISPONIVEL")) {
        const label = elemento("label", "", "residuo-opcao");
        const input = elemento("input");
        input.type = "checkbox";
        input.value = r.id;
        label.append(input, elemento("span", [r.codigo, r.descricao || r.categoriaResiduo, r.quantidade + " un."].join(" · ")));
        grupo.append(label);
    }
    if (!grupo.children.length) grupo.textContent = "Você ainda não tem resíduos disponíveis.";
    campo("proximo").textContent = ponto ? ponto.nome + " · " + ponto.endereco : "Nenhum ponto cadastrado.";
    campo("resumo-ponto").textContent = ponto?.nome || "Nenhum cadastrado";
    grupo.addEventListener("change", () => {
        campo("resumo-residuos").textContent = String(grupo.querySelectorAll("input:checked").length);
    });
    acao(campo("form-solicitacao"), "submit", async () => {
        const selecionados = [...grupo.querySelectorAll("input:checked")];
        if (!selecionados.length) throw new Error("Selecione pelo menos um resíduo disponível.");
        for (const input of selecionados) {
            await api("pedidos", "POST", {residuoId: input.value});
            input.closest("label").replaceWith(elemento("p", "Solicitação registrada."));
        }
        location.assign("solicitacoes.html");
    });
}
