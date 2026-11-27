const linhaEmRota = document.getElementById("linha-em-rota");
const semEmRota = document.getElementById("sem-em-rota");
const concluir = document.getElementById("concluir-coleta");
const cancelarRota = document.getElementById("cancelar-rota");

const dialogo = document.getElementById("dialogo-cancelar-rota");
const voltar = document.getElementById("voltar-cancelar");
const confirmar = document.getElementById("confirmar-cancelar");

function esvaziarEmRota() {
    linhaEmRota.hidden = true;
    semEmRota.hidden = false;
}

concluir.addEventListener("click", () => {

    // marcar o pedido como Concluída e registrar no log.
    esvaziarEmRota();

});

cancelarRota.addEventListener("click", () => dialogo.showModal());

voltar.addEventListener("click", () => dialogo.close());

confirmar.addEventListener("click", () => {

    // cancelar o pedido (só gerente), avisar o coletor e registrar no log.

    esvaziarEmRota();
    dialogo.close();
});