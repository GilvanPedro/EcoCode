import {acao, api, elemento, linha, situacoes} from "./api.js";


const corpo = document.getElementById("corpo-tabela");
const resumo = document.getElementById("resumo");
const semResultados = document.getElementById("sem-resultados");
const filtros = document.querySelectorAll(".filtro");
const ordenar = document.getElementById("ordenar");

const dialogo = document.getElementById("dialogo-exclusao");
const dialogoCodigo = document.getElementById("dialogo-codigo");
const confirmar = document.getElementById("confirmar-exclusao");
const cancelar = document.getElementById("cancelar-exclusao");

let categoriaAtiva = "todos";
let linhaParaExcluir = null;

const linhas = () => Array.from(corpo.querySelectorAll("tr"));
const plural = (n, singular, plural) => (n === 1 ? singular : plural);

// ---------- resumo do topo ----------
function atualizarResumo() {
    const todas = linhas();
    const contar = (situacao) => todas.filter((l) => l.dataset.situacao === situacao).length;

    const total = todas.length;
    const disponiveis = contar("disponivel");
    const emSolicitacao = contar("solicitacao");
    const descartados = contar("descartado");

    resumo.textContent =
        `${total} ${plural(total, "resíduo cadastrado", "resíduos cadastrados")}: ` +
        `${disponiveis} ${plural(disponiveis, "disponível", "disponíveis")}, ` +
        `${emSolicitacao} em solicitação e ` +
        `${descartados} ${plural(descartados, "já descartado", "já descartados")}.`;
}

// ---------- filtro por categoria ----------
function aplicarFiltro() {
    let visiveis = 0;

    linhas().forEach((linha) => {
        const mostrar = categoriaAtiva === "todos" || linha.dataset.categoria === categoriaAtiva;
        linha.hidden = !mostrar;
        if (mostrar) visiveis++;
    });

    semResultados.hidden = visiveis > 0;
}

filtros.forEach((botao) => {
    botao.addEventListener("click", () => {
        categoriaAtiva = botao.dataset.categoria;
        filtros.forEach((b) => b.classList.toggle("ativo", b === botao));
        aplicarFiltro();
    });
});

// ---------- ordenação ----------
const ordemSituacao = { disponivel: 0, solicitacao: 1, descartado: 2 };

const ordens = {
    cadastro: (a, b) => Number(a.dataset.codigo) - Number(b.dataset.codigo),
    categoria: (a, b) => a.dataset.categoria.localeCompare(b.dataset.categoria, "pt-BR") || ordens.cadastro(a, b),
    situacao: (a, b) => ordemSituacao[a.dataset.situacao] - ordemSituacao[b.dataset.situacao] || ordens.cadastro(a, b),
    quantidade: (a, b) => Number(b.dataset.qtd) - Number(a.dataset.qtd) || ordens.cadastro(a, b),
};

ordenar.addEventListener("change", () => {
    linhas().sort(ordens[ordenar.value]).forEach((linha) => corpo.appendChild(linha));
});

// ---------- excluir (com confirmação) ----------
corpo.addEventListener("click", (e) => {
    const botao = e.target.closest(".btn-acao.perigo");
    if (!botao) return;

    linhaParaExcluir = botao.closest("tr");
    dialogoCodigo.textContent = linhaParaExcluir.dataset.codigo;
    dialogo.showModal();
});

cancelar.addEventListener("click", () => {
    linhaParaExcluir = null;
    dialogo.close();
});

acao(confirmar, "click", async () => {
    await api("residuos/" + linhaParaExcluir.dataset.codigo, "DELETE");
    if (linhaParaExcluir) linhaParaExcluir.remove();
    linhaParaExcluir = null;
    dialogo.close();

    atualizarResumo();
    aplicarFiltro();
});

export async function iniciar() {
    for (const r of await api("residuos")) {
        const excluir = elemento("button", "Excluir", "btn-acao perigo");
        excluir.disabled = r.status !== "DISPONIVEL";
        const tr = linha(corpo, [r.codigo, r.categoriaResiduo, r.descricao, r.quantidade, situacoes[r.status], excluir]);
        Object.assign(tr.dataset, {
            codigo: r.codigo, qtd: r.quantidade, categoria: r.residuo.toLowerCase(),
            situacao: {DISPONIVEL: "disponivel", EM_SOLICITACAO: "solicitacao", DESCARTADO: "descartado"}[r.status]
        });
    }
    atualizarResumo();
    aplicarFiltro();
}
