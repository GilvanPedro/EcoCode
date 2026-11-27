const form = document.getElementById("form-ponto");
const corpo = document.getElementById("corpo-pontos");
const tituloForm = document.getElementById("titulo-form");

const campoNome = document.getElementById("nome");
const campoBairro = document.getElementById("bairro");
const campoEndereco = document.getElementById("endereco");
const campoHorario = document.getElementById("horario");
const campoAtivo = document.getElementById("ativo");

let linhaEditando = null;

function celula(texto, classe) {
    const td = document.createElement("td");
    td.textContent = texto;
    if (classe) td.className = classe;
    return td;
}

function celulaSituacao(ativo) {
    const td = document.createElement("td");
    const etiqueta = document.createElement("span");
    etiqueta.className = "etiqueta " + (ativo ? "ponto-ativo" : "ponto-inativo");
    etiqueta.textContent = ativo ? "Ativo" : "Inativo";
    td.appendChild(etiqueta);
    return td;
}

function celulaEditar() {
    const td = document.createElement("td");
    const botao = document.createElement("button");
    botao.type = "button";
    botao.className = "btn-acao";
    botao.textContent = "Editar";
    td.appendChild(botao);
    return td;
}

function montarLinha(tr, dados) {
    tr.dataset.endereco = dados.endereco;
    tr.replaceChildren(
        celula(dados.nome, "ponto-nome"),
        celula(dados.bairro),
        celula(dados.horario),
        celulaSituacao(dados.ativo),
        celulaEditar()
    );
}

function marcarErro(campo) {
    campo.closest(".campo").classList.add("invalido");
}

[campoNome, campoBairro, campoHorario].forEach((c) => {

    c.addEventListener("input", () => c.closest(".campo").classList.remove("invalido"));
    c.addEventListener("change", () => c.closest(".campo").classList.remove("invalido"));

});

corpo.addEventListener("click", (e) => {
    const botao = e.target.closest(".btn-acao");
    if (!botao) return;

    const linha = botao.closest("tr");
    const celulas = linha.children;
    const bairro = celulas[1].textContent;

    if (!Array.from(campoBairro.options).some((o) => o.value === bairro)) {

        const opcao = document.createElement("option");
        opcao.textContent = bairro;
        campoBairro.appendChild(opcao);

    }

    campoNome.value = celulas[0].textContent;
    campoBairro.value = bairro;
    campoEndereco.value = linha.dataset.endereco || "";
    campoHorario.value = celulas[2].textContent;
    campoAtivo.checked = celulas[3].querySelector(".etiqueta").classList.contains("ponto-ativo");

    linhaEditando = linha;
    tituloForm.textContent = "Editar ponto de coleta";
    campoNome.focus();
});


form.addEventListener("submit", (e) => {
    e.preventDefault();
    let ok = true;

    if (!campoNome.value.trim()) { marcarErro(campoNome); ok = false; }
    if (!campoBairro.value) { marcarErro(campoBairro); ok = false; }
    if (!campoHorario.value.trim()) { marcarErro(campoHorario); ok = false; }
    if (!ok) return;

    // salvar o ponto e registrar no log. O bairro precisa ser um dos 15 do grafo.
    
    const dados = {
        nome: campoNome.value.trim(),
        bairro: campoBairro.value,
        endereco: campoEndereco.value.trim(),
        horario: campoHorario.value.trim(),
        ativo: campoAtivo.checked,
    };

    if (linhaEditando) {
        montarLinha(linhaEditando, dados);
    } else {
        const tr = document.createElement("tr");
        montarLinha(tr, dados);
        corpo.appendChild(tr);
    }

    linhaEditando = null;
    tituloForm.textContent = "Novo ponto de coleta";
    form.reset();
});