const corpo = document.getElementById("corpo-log");
const filtroAcao = document.getElementById("filtro-acao");
const busca = document.getElementById("busca");
const semResultados = document.getElementById("sem-resultados");
const contagem = document.getElementById("contagem");

const linhas = Array.from(corpo.querySelectorAll("tr"));

function filtrar() {
    const acao = filtroAcao.value;
    const termo = busca.value.trim().toLowerCase();
    let visiveis = 0;

    linhas.forEach((linha) => {
        const acaoOk = acao === "todas" || linha.dataset.acao === acao;
        const buscaOk = !termo || linha.textContent.toLowerCase().includes(termo);
        const mostrar = acaoOk && buscaOk;

        linha.hidden = !mostrar;
        if (mostrar) visiveis++;
    });

    semResultados.hidden = visiveis > 0;

    // o total (142) e a busca por CPF vêm do servidor.
    contagem.textContent = `Mostrando ${visiveis} de 142 registros. Funcionários e gerentes podem consultar.`;
}

filtroAcao.addEventListener("change", filtrar);
busca.addEventListener("input", filtrar);