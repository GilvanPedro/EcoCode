

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

confirmar.addEventListener("click", () => {

    // pedir o cancelamento e só mudar a tela se der certo.
    
    if (pedidoParaCancelar) {
        const hoje = new Date();
        const data = `${String(hoje.getDate()).padStart(2, "0")}/${String(hoje.getMonth() + 1).padStart(2, "0")}`;

        pedidoParaCancelar.dataset.situacao = "cancelada";

        const etiqueta = pedidoParaCancelar.querySelector(".etiqueta");
        etiqueta.className = "etiqueta cancelada";
        etiqueta.textContent = "Cancelada";

        const meta = pedidoParaCancelar.querySelector(".pedido-meta");
        meta.className = "pedido-meta cancelado";
        meta.textContent = `Cancelado por você em ${data}`;

        pedidoParaCancelar.querySelector(".pedido-acoes").innerHTML =
            '<a href="pedido" class="btn-acao">Ver detalhes</a>';
    }

    pedidoParaCancelar = null;
    dialogo.close();

    atualizarContagens();
    aplicarFiltro();
});

atualizarContagens();