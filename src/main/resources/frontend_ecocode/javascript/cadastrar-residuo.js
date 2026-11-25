

const form = document.getElementById("form-residuo");
const categoria = document.getElementById("categoria");
const quantidade = document.getElementById("quantidade");
const aviso = document.getElementById("aviso-sucesso");

function marcarErro(campo) {
    campo.closest(".campo").classList.add("invalido");
}

function limparErro(campo) {
    campo.closest(".campo").classList.remove("invalido");
}

[categoria, quantidade].forEach((c) => {
    c.addEventListener("input", () => limparErro(c));
    c.addEventListener("change", () => limparErro(c));
});

form.addEventListener("submit", (e) => {
    e.preventDefault();
    aviso.classList.remove("visivel");
    let ok = true;

    if (!categoria.value) { marcarErro(categoria); ok = false; }

    const qtd = Number(quantidade.value);
    if (!Number.isInteger(qtd) || qtd < 1) { marcarErro(quantidade); ok = false; }

    if (!ok) return;

    // (backend): enviar categoria, quantidade e descrição e receber o código gerado.
    // Código de exemplo: 10 + número da categoria (10 % 5 = 0, 11 % 5 = 1...), só para mostrar a mensagem.

    const codigo = String(10 + Number(categoria.value)).padStart(4, "0");
    const nomeCategoria = categoria.options[categoria.selectedIndex].text;

    aviso.innerHTML = `Resíduo cadastrado com sucesso. Código gerado: <strong>${codigo}</strong> (${nomeCategoria}).`;
    aviso.classList.add("visivel");

    form.reset();
    window.scrollTo({ top: 0, behavior: "smooth" });
});