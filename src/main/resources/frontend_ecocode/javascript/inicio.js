
const botoesTema = document.querySelectorAll("button[data-tema]");

function aplicarTema(tema) {
    document.documentElement.dataset.tema = tema;
    botoesTema.forEach((b) => b.classList.toggle("ativo", b.dataset.tema === tema));

    try {
        localStorage.setItem("ecocode_tema", tema);
    } catch (e) {

        // se o navegador bloquear o armazenamento, o tema só vale nesta tela

    }
}

botoesTema.forEach((b) => b.addEventListener("click", () => aplicarTema(b.dataset.tema)));

let temaSalvo = "claro";

try {
    temaSalvo = localStorage.getItem("ecocode_tema") || "claro";
} catch (e) {
    temaSalvo = "claro";
}

aplicarTema(temaSalvo);