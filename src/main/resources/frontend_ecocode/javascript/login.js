// Login: por enquanto só confere se os campos foram preenchidos e vai para a tela principal.
// O backend (feito à parte) vai conferir e-mail e senha depois.

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

    // TODO (backend): enviar e-mail e senha. Se der erro, mostrar "E-mail ou senha incorretos." na caixa de erro.
    
    window.location.href = "inicio.html";
});