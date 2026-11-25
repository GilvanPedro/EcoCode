

const aviso = document.getElementById("aviso-sucesso");
const etiqueta = document.getElementById("etiqueta-pedido");
const botaoCancelar = document.getElementById("botao-cancelar");
const notaCancelar = document.getElementById("nota-cancelar");
const cartaoFila = document.getElementById("cartao-fila");

const dialogo = document.getElementById("dialogo-cancelar");
const confirmar = document.getElementById("confirmar-cancelar");
const voltar = document.getElementById("voltar-cancelar");

if (window.location.hash === "#enviado") {
    aviso.classList.add("visivel");
}

botaoCancelar.addEventListener("click", () => dialogo.showModal());
voltar.addEventListener("click", () => dialogo.close());

confirmar.addEventListener("click", () => {

    // pedir o cancelamento e só mudar a tela se der certo.

    etiqueta.className = "etiqueta cancelada";
    etiqueta.textContent = "Cancelada";

    botaoCancelar.hidden = true;
    notaCancelar.hidden = true;
    cartaoFila.hidden = true;

    aviso.textContent = "Pedido cancelado. Os resíduos voltaram a ficar Disponíveis.";
    aviso.classList.add("visivel");

    dialogo.close();
});