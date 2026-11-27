const form = document.getElementById("form-login");
const erro = document.getElementById("erro-login");

form.addEventListener("input", () => erro.classList.remove("visivel"));

form.addEventListener("submit", (e) => {
    e.preventDefault();

    const email = document.getElementById("email");
    const senha = document.getElementById("senha");

    if (!email.value.trim() || !email.checkValidity() || !senha.value) {
        erro.textContent = "Preencha o e-mail e a senha.";
        erro.classList.add("visivel");
        return;
    }

    // Qual botão foi clicado: "usuario" ou "funcionario" (funcionário ou gerente).
    const perfil = e.submitter ? e.submitter.dataset.perfil : "usuario";

    //  enviar e-mail e senha. Se der erro, mostrar "E-mail ou senha incorretos." na caixa de erro.
    window.location.href = perfil === "funcionario" ? "fila-coletas.html" : "inicio.html";
});