import {acao, api, elemento, link, situacoes} from "./api.js";


const lista = document.getElementById("lista-pedidos");
const filtros = document.querySelectorAll(".filtro");
const semResultados = document.getElementById("sem-resultados");

const dialogo = document.getElementById("dialogo-cancelar");
const dialogoPedido = document.getElementById("dialogo-pedido");
const confirmar = document.getElementById("confirmar-cancelar");
const voltar = document.getElementById("voltar-cancelar");

let situacaoAtiva = "todas";
let pedidoParaCancelar = null;

const pedidos = () => Array.from(lista.querySelectorAll("li"));

// ---------- filtro e contagens ----------
function atualizarContagens() {
    filtros.forEach((botao) => {
        const situacao = botao.dataset.situacao;
        const total = situacao === "todas"
            ? pedidos().length
            : pedidos().filter((p) => p.dataset.situacao === situacao).length;

        botao.textContent = `${botao.dataset.rotulo} (${total})`;
    });
}

function aplicarFiltro() {
    let visiveis = 0;

    pedidos().forEach((pedido) => {
        const mostrar = situacaoAtiva === "todas" || pedido.dataset.situacao === situacaoAtiva;
        pedido.hidden = !mostrar;
        if (mostrar) visiveis++;
    });

    semResultados.hidden = visiveis > 0;
}

filtros.forEach((botao) => {
    botao.addEventListener("click", () => {
        situacaoAtiva = botao.dataset.situacao;
        filtros.forEach((b) => b.classList.toggle("ativo", b === botao));
        aplicarFiltro();
    });
});

// ---------- cancelar (só pedidos Pendentes têm o botão) ----------
lista.addEventListener("click", (e) => {
    const botao = e.target.closest(".btn-acao.perigo");
    if (!botao) return;

    pedidoParaCancelar = botao.closest("li");
    dialogoPedido.textContent = `#${pedidoParaCancelar.dataset.pedido}`;
    dialogo.showModal();
});

voltar.addEventListener("click", () => {
    pedidoParaCancelar = null;
    dialogo.close();
});

acao(confirmar, "click", async () => {
    await api("pedidos/" + pedidoParaCancelar.dataset.pedido + "/cancelar", "POST");
    location.reload();
});

export async function iniciar() {
    for (const p of await api("pedidos")) {
        const item = elemento("li");
        item.dataset.pedido = p.id;
        item.dataset.situacao = p.status.toLowerCase().replace("_", "-");
        item.append(link("Pedido " + p.id.slice(0,8), "pedido.html?id=" + p.id),
            elemento("span", situacoes[p.status], "etiqueta " + item.dataset.situacao));
        if (p.status === "PENDENTE") item.append(elemento("button", "Cancelar", "btn-acao perigo"));
        lista.append(item);
    }
    atualizarContagens();
    aplicarFiltro();
}
