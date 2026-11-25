// Cadastro: por enquanto só valida o formato dos campos e vai para o login.
// Depois o backend (feito à parte) cria a conta e avisa se o CPF ou o e-mail já existem.

const form = document.getElementById("form-cadastro");
const campos = {
    nome: document.getElementById("nome"),
    cpf: document.getElementById("cpf"),
    telefone: document.getElementById("telefone"),
    email: document.getElementById("email"),
    bairro: document.getElementById("bairro"),
    senha: document.getElementById("senha"),
    confirmar: document.getElementById("confirmar"),
};

const apenasDigitos = (v) => v.replace(/\D/g, "");

// ---------- máscaras ----------
campos.cpf.addEventListener("input", () => {
    const d = apenasDigitos(campos.cpf.value).slice(0, 11);
    campos.cpf.value = d
        .replace(/^(\d{3})(\d)/, "$1.$2")
        .replace(/^(\d{3})\.(\d{3})(\d)/, "$1.$2.$3")
        .replace(/\.(\d{3})(\d)/, ".$1-$2");
});

campos.telefone.addEventListener("input", () => {
    const d = apenasDigitos(campos.telefone.value).slice(0, 11);
    campos.telefone.value = d
        .replace(/^(\d{2})(\d)/, "($1) $2")
        .replace(/(\d{5})(\d{1,4})$/, "$1-$2");
});

// ---------- validações ----------
function cpfValido(cpf) {
    const d = apenasDigitos(cpf);
    if (d.length !== 11 || /^(\d)\1+$/.test(d)) return false;
    for (const t of [9, 10]) {
        let soma = 0;
        for (let i = 0; i < t; i++) soma += Number(d[i]) * (t + 1 - i);
        const dv = ((soma * 10) % 11) % 10;
        if (dv !== Number(d[t])) return false;
    }
    return true;
}

// Para mostrar um erro que veio do backend, use: marcarErro(campos.cpf, "CPF já cadastrado em outra conta.")
function marcarErro(campo, mensagem) {
    const bloco = campo.closest(".campo");
    if (mensagem) bloco.querySelector(".msg-erro").textContent = mensagem;
    bloco.classList.add("invalido");
}

function limparErro(campo) {
    campo.closest(".campo").classList.remove("invalido");
}

Object.values(campos).forEach((c) => c.addEventListener("input", () => limparErro(c)));

// ---------- envio ----------
form.addEventListener("submit", (e) => {
    e.preventDefault();
    let ok = true;

    if (campos.nome.value.trim().split(/\s+/).length < 2) { marcarErro(campos.nome); ok = false; }
    if (!cpfValido(campos.cpf.value)) { marcarErro(campos.cpf, "CPF inválido."); ok = false; }
    if (apenasDigitos(campos.telefone.value).length < 10) { marcarErro(campos.telefone); ok = false; }
    if (!campos.email.value || !campos.email.checkValidity()) { marcarErro(campos.email, "Informe um e-mail válido."); ok = false; }
    if (!campos.bairro.value) { marcarErro(campos.bairro); ok = false; }
    if (campos.senha.value.length < 8) { marcarErro(campos.senha); ok = false; }
    if (campos.confirmar.value !== campos.senha.value) { marcarErro(campos.confirmar); ok = false; }
    if (!ok) return;

    // TODO (backend): enviar os dados para criar a conta (cargo começa como Usuário).
    const aviso = document.getElementById("sucesso");
    aviso.textContent = "Conta criada! Redirecionando para o login...";
    aviso.classList.add("visivel");
    setTimeout(() => (window.location.href = "login.html"), 1200);
});