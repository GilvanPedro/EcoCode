
const form = document.getElementById("form-solicitacao");
const grupo = document.getElementById("grupo-residuos");
const opcoes = form.querySelectorAll(".residuo-opcao input");
const ponto = document.getElementById("ponto");
const resumoResiduos = document.getElementById("resumo-residuos");
const resumoPonto = document.getElementById("resumo-ponto");

function atualizarResumo() {
    const marcados = Array.from(opcoes).filter((o) => o.checked);
    const unidades = marcados.reduce((soma, o) => soma + Number(o.dataset.qtd), 0);

    resumoResiduos.textContent = marcados.length === 0
        ? "Nenhum"
        : `${marcados.length} ${marcados.length === 1 ? "item" : "itens"}, ${unidades} un.`;

    resumoPonto.textContent = ponto.options[ponto.selectedIndex].dataset.resumo;

    opcoes.forEach((o) => o.closest(".residuo-opcao").classList.toggle("selecionado", o.checked));

    if (marcados.length > 0) grupo.classList.remove("invalido");
}

opcoes.forEach((o) => o.addEventListener("change", atualizarResumo));
ponto.addEventListener("change", atualizarResumo);

form.addEventListener("submit", (e) => {
    e.preventDefault();

    if (!Array.from(opcoes).some((o) => o.checked)) {
        grupo.classList.add("invalido");
        return;
    }

    // enviar os resíduos marcados, o ponto e a observação para criar o pedido.
    window.location.href = "pedido#enviado";
});

atualizarResumo();