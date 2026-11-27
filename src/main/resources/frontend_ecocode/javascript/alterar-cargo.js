
const MEU_CPF = "111.222.333-96"; 

const pessoas = {
    "987.654.321-00": { nome: "Rafael Lima", email: "rafael.lima@email.com", bairro: "Setor Bueno", cargo: "Usuário" },
    "123.456.789-09": { nome: "Mariana Alves", email: "mariana.alves@email.com", bairro: "Setor Central", cargo: "Usuário" },
};

const formBusca = document.getElementById("form-busca");
const campoCpf = document.getElementById("cpf");
const resultado = document.getElementById("resultado");
const formCargo = document.getElementById("form-cargo");
const avisoSucesso = document.getElementById("aviso-sucesso");
const avisoErro = document.getElementById("aviso-erro");

let pessoaAtual = null;

// máscara do CPF 
campoCpf.addEventListener("input", () => {
    const d = campoCpf.value.replace(/\D/g, "").slice(0, 11);
    campoCpf.value = d
        .replace(/^(\d{3})(\d)/, "$1.$2")
        .replace(/^(\d{3})\.(\d{3})(\d)/, "$1.$2.$3")
        .replace(/\.(\d{3})(\d)/, ".$1-$2");
});

function limparAvisos() {
    avisoSucesso.classList.remove("visivel");
    avisoErro.classList.remove("visivel");
}

function mostrarErro(texto) {
    limparAvisos();
    avisoErro.textContent = texto;
    avisoErro.classList.add("visivel");
}

function mostrarSucesso(texto) {
    limparAvisos();
    avisoSucesso.textContent = texto;
    avisoSucesso.classList.add("visivel");
}

function iniciais(nome) {
    const partes = nome.trim().split(/\s+/);
    return (partes[0][0] + partes[partes.length - 1][0]).toUpperCase();
}

function mostrarPessoa(pessoa) {
    document.getElementById("avatar").textContent = iniciais(pessoa.nome);
    document.getElementById("pessoa-nome").textContent = pessoa.nome;
    document.getElementById("pessoa-info").textContent = `${pessoa.email} · ${pessoa.bairro}`;
    document.getElementById("cargo-atual").textContent = pessoa.cargo;

    formCargo.querySelectorAll("input[name=cargo]").forEach((radio) => {
        radio.checked = radio.value === pessoa.cargo;
    });

    resultado.hidden = false;
}

formBusca.addEventListener("submit", (e) => {
    e.preventDefault();
    limparAvisos();
    resultado.hidden = true;
    pessoaAtual = null;

    const cpf = campoCpf.value;

    if (cpf.replace(/\D/g, "").length !== 11) {
        mostrarErro("Informe um CPF com 11 números.");
        return;
    }

    if (cpf === MEU_CPF) {
        mostrarErro("Este CPF é o da sua conta. Você não pode alterar o próprio cargo.");
        return;
    }

    //  buscar a pessoa pelo CPF.
    const pessoa = pessoas[cpf];

    if (!pessoa) {
        mostrarErro("Nenhuma pessoa encontrada com esse CPF.");
        return;
    }

    pessoaAtual = pessoa;
    mostrarPessoa(pessoa);
});


formCargo.addEventListener("submit", (e) => {
    e.preventDefault();
    if (!pessoaAtual) return;

    const novo = formCargo.querySelector("input[name=cargo]:checked").value;

    if (novo === pessoaAtual.cargo) {
        mostrarErro("Escolha um cargo diferente do atual.");
        return;
    }

    // salvar o novo cargo e registrar a mudança no log de auditoria.
    const antigo = pessoaAtual.cargo;
    pessoaAtual.cargo = novo;
    document.getElementById("cargo-atual").textContent = novo;

    mostrarSucesso(`Cargo de ${pessoaAtual.nome} alterado de ${antigo} para ${novo}. A mudança foi registrada no log.`);
});

// cancelar 
document.getElementById("cancelar-busca").addEventListener("click", () => {
    resultado.hidden = true;
    pessoaAtual = null;
    campoCpf.value = "";
    limparAvisos();
});