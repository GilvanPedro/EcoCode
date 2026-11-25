

const form = document.getElementById("form-perfil");
const aviso = document.getElementById("aviso-sucesso");

const nome = document.getElementById("nome");
const email = document.getElementById("email");
const senha = document.getElementById("senha");
const confirmar = document.getElementById("confirmar");

function marcarErro(campo) {
    campo.closest(".campo").classList.add("invalido");
}

function limparErros() {
    form.querySelectorAll(".campo.invalido").forEach((c) => c.classList.remove("invalido"));
}

[nome, email, senha, confirmar].forEach((c) =>
    c.addEventListener("input", () => c.closest(".campo").classList.remove("invalido"))
);

// "Descartar mudanças" volta os campos para o que estava no HTML.

form.addEventListener("reset", () => {
    limparErros();
    aviso.classList.remove("visivel");
});

form.addEventListener("submit", (e) => {
    e.preventDefault();
    limparErros();
    aviso.classList.remove("visivel");

    let ok = true;

    if (nome.value.trim().split(/\s+/).length < 2) { marcarErro(nome); ok = false; }
    if (!email.value || !email.checkValidity()) { marcarErro(email); ok = false; }

    // A senha é opcional: só confere se a pessoa digitou alguma coisa.

    if (senha.value && senha.value.length < 8) { marcarErro(senha); ok = false; }
    if (senha.value !== confirmar.value) { marcarErro(confirmar); ok = false; }

    if (!ok) return;

    // enviar só o que mudou (nome, e-mail, bairro e, se preenchida, a nova senha).
    
    senha.value = "";
    confirmar.value = "";

    aviso.classList.add("visivel");
    window.scrollTo({ top: 0, behavior: "smooth" });
});