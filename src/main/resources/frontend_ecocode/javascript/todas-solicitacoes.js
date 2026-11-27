

const corpo = document.getElementById("corpo-solicitacoes");
const filtros = document.querySelectorAll(".filtro");
const busca = document.getElementById("busca");
const semResultados = document.getElementById("sem-resultados");

const painel = document.getElementById("painel-confirmacao");
const painelTitulo = document.getElementById("painel-titulo");
const painelTexto = document.getElementById("painel-texto");
const painelVoltar = document.getElementById("painel-voltar");
const painelConfirmar = document.getElementById("painel-confirmar");

let situacaoAtiva = "todas";
let linhaAlvo = null;

const linhas = () => Array.from(corpo.querySelectorAll("tr"));

// filtro e busca 
function aplicarFiltro() {
    const termo = busca.value.trim().toLowerCase();
    let visiveis = 0;

    linhas().forEach((linha) => {
        const situacaoOk = situacaoAtiva === "todas" || linha.dataset.situacao === situacaoAtiva;
        const buscaOk = !termo || linha.textContent.toLowerCase().includes(termo);
        const mostrar = situacaoOk && buscaOk;

        linha.hidden = !mostrar;
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

busca.addEventListener("input", aplicarFiltro);

corpo.addEventListener("click", (e) => {
    const botao = e.target.closest(".btn-acao.perigo");
    if (!botao) return;

    linhaAlvo = botao.closest("tr");
    const pedido = linhaAlvo.dataset.pedido;
    const emRota = linhaAlvo.dataset.situacao === "em-rota";

    painelTitulo.textContent = `Confirmar cancelamento do pedido #${pedido}?`;
    painelTexto.textContent = emRota
        ? "Ele está Em rota. O coletor será avisado e a ação vai para o log."
        : "Ele está Pendente. A ação vai para o log.";

    painel.hidden = false;
    painel.scrollIntoView({ behavior: "smooth", block: "nearest" });
});

painelVoltar.addEventListener("click", () => {
    linhaAlvo = null;
    painel.hidden = true;
});

painelConfirmar.addEventListener("click", () => {

    // cancelar o pedido, avisar o coletor se estiver Em rota e registrar no log.

    if (linhaAlvo) {
        linhaAlvo.dataset.situacao = "cancelada";

        const etiqueta = linhaAlvo.querySelector(".etiqueta");
        etiqueta.className = "etiqueta cancelada";
        etiqueta.textContent = "Cancelada";

        const celulaAcao = linhaAlvo.lastElementChild;
        celulaAcao.innerHTML = '<span class="sem-acao">Sem ação</span>';
    }

    linhaAlvo = null;
    painel.hidden = true;
    aplicarFiltro();
});