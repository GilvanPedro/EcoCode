export const campo = id => document.getElementById(id);
export const valor = id => campo(id).value;
export const pagina = location.pathname.split("/").pop().replace(".html", "");
export const situacoes = {
    DISPONIVEL: "Disponível", EM_SOLICITACAO: "Em solicitação", DESCARTADO: "Descartado",
    PENDENTE: "Pendente", EM_ROTA: "Em rota", CONCLUIDA: "Concluída", CANCELADA: "Cancelada"
};

export function elemento(tag, texto, classe) {
    const el = document.createElement(tag);
    if (texto != null) el.textContent = texto;
    if (classe) el.className = classe;
    return el;
}

export async function api(caminho, metodo = "GET", dados) {
    const resposta = await fetch("/api/" + caminho, {
        method: metodo, cache: "no-store",
        headers: {"Content-Type": "application/json", "X-EcoCode": "web"},
        body: dados === undefined ? undefined : JSON.stringify(dados)
    });
    if (resposta.status === 401 && caminho !== "login") location.replace("login.html");
    const texto = await resposta.text();
    const resultado = texto ? JSON.parse(texto) : null;
    if (!resposta.ok) throw new Error(resultado?.mensagem || (resposta.status === 401
        ? "E-mail ou senha inválidos." : "Não foi possível concluir a operação."));
    return resultado;
}

export function aviso(mensagem) {
    let alerta = campo("mensagem-api");
    if (!alerta) {
        alerta = elemento("p", "", "cartao");
        alerta.id = "mensagem-api";
        alerta.setAttribute("role", "alert");
        document.querySelector("main").prepend(alerta);
    }
    alerta.textContent = mensagem;
    alerta.scrollIntoView({block: "nearest"});
}

export function acao(el, evento, executar) {
    el.addEventListener(evento, async e => {
        e.preventDefault();
        if (el.dataset.ocupado) return;
        el.dataset.ocupado = "sim";
        try { await executar(e); }
        catch (erro) { aviso(erro.message); }
        finally { delete el.dataset.ocupado; }
    });
}

export async function preencherBairros(select, atual = "") {
    select.replaceChildren(new Option("Selecione seu bairro", ""));
    for (const [id, nome] of Object.entries(await api("bairros"))) select.add(new Option(nome, id));
    select.value = atual;
}

export function link(texto, href) {
    const a = elemento("a", texto, "btn-acao");
    a.href = href;
    return a;
}

export function botao(texto, executar) {
    const b = elemento("button", texto, "btn btn-secundario");
    b.type = "button";
    acao(b, "click", executar);
    return b;
}

export function linha(tbody, valores) {
    const tr = tbody.insertRow();
    for (const valor of valores) {
        const td = tr.insertCell();
        if (valor instanceof Node) td.append(valor);
        else td.textContent = valor ?? "";
    }
    return tr;
}
