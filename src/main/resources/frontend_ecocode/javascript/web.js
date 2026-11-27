import {acao, api, aviso, campo, link, pagina} from "./api.js";

async function iniciar() {
    const publico = ["login", "cadastro"].includes(pagina);
    const perfil = publico ? null : await api("perfil");
    if (perfil) {
        campo("usuario-nome").textContent = perfil.nome;
        campo("usuario-info").textContent = perfil.cargo;
        const menu = document.querySelector("nav.menu");
        if (!menu.querySelector('[href="inicio.html"]')) menu.prepend(link("Início", "inicio.html"));
        if (perfil.cargo !== "USUARIO" && !menu.querySelector('[href="fila-coletas.html"]'))
            menu.append(link("Fila de coletas", "fila-coletas.html"));
        const gerencia = ["pontos-coleta", "alterar-cargo", "log-auditoria", "todas-solicitacoes"];
        for (const nome of gerencia) {
            if (perfil.cargo !== "GERENTE") menu.querySelector(`[href="${nome}.html"]`)?.remove();
        }
        if ((gerencia.includes(pagina) && perfil.cargo !== "GERENTE") ||
            (["todas-solicitacoes", "fila-coletas"].includes(pagina) && perfil.cargo === "USUARIO")) {
            location.replace("inicio.html");
            return;
        }
        for (const sair of document.querySelectorAll('a[href="login.html"]')) {
            acao(sair, "click", async () => { await api("logout", "POST"); location.replace("login.html"); });
        }
    }
    const modulo = pagina === "inicio" ? "painel" : pagina === "pontos-coleta" ? "ponto-coleta" : pagina;
    const tela = await import("./" + modulo + ".js");
    await tela.iniciar(perfil);
}
iniciar().catch(erro => aviso(erro.message));
