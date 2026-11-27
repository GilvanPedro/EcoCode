import {api, campo, elemento} from "./api.js";

export async function iniciar(perfil) {
    const [pontos, proximo] = await Promise.all([api("pontos"), api("pontos/proximo")]);
    campo("proximo").textContent = proximo ? proximo.nome + " · " + proximo.endereco : "Nenhum ponto cadastrado.";
    campo("partida").textContent = "Seu bairro: " + perfil.bairro.replaceAll("_", " ");
    for (const p of pontos) campo("pontos").append(elemento("li", p.nome + " · " + p.endereco));
}
