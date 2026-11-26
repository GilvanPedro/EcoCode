package backend_ecocode.Testes;

import backend_ecocode.Domain.Entities.Residuo;
import backend_ecocode.Domain.Entities.Solicitacao;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.TipoResiduo;
import backend_ecocode.Instanciador;
import backend_ecocode.Service.GerenciarResiduosService;
import backend_ecocode.Service.GerenciarSolicitacoesService;
import backend_ecocode.Service.GerenciarUsuariosService;

public class TesteSolicitacoes {

    public static void main(String[] args) throws InterruptedException {
        Instanciador inst = new Instanciador();
        GerenciarUsuariosService usuarios = inst.getUsuariosService();
        GerenciarResiduosService residuos = inst.getResiduosService();
        GerenciarSolicitacoesService solicitacoes = inst.getSolicitacoesService();

        Usuario ana = usuarios.buscar("39053344705");    // gerente
        Usuario carlos = usuarios.buscar("11144477735"); // funcionário
        Usuario maria = usuarios.buscar("52998224725");
        Usuario joao = usuarios.buscar("12345678909");
        Usuario bia = usuarios.buscar("71460238001");

        System.out.println("=== RESÍDUOS NOVOS PRA PEDIR ===");
        Residuo rMaria = residuos.salvar(maria, TipoResiduo.COMPUTADORES, 1, "Teste fila 1");
        Residuo rJoao = residuos.salvar(joao, TipoResiduo.CELULARES, 1, "Teste fila 2");
        Residuo rBia = residuos.salvar(bia, TipoResiduo.CABOS, 3, "Teste fila 3");
        Residuo rBia2 = residuos.salvar(bia, TipoResiduo.BATERIAS, 2, "Teste cancelar");
        System.out.println("[OK] 4 resíduos cadastrados");

        System.out.println("\n=== PEDINDO (um segundo entre cada, pra ordem ficar clara) ===");
        Solicitacao sMaria = pedir(solicitacoes, maria, rMaria);
        Thread.sleep(1000);
        Solicitacao sJoao = pedir(solicitacoes, joao, rJoao);
        Thread.sleep(1000);
        Solicitacao sBia = pedir(solicitacoes, bia, rBia);
        Solicitacao sBia2 = pedir(solicitacoes, bia, rBia2);

        System.out.println("\n=== POSIÇÃO NA FILA ===");
        mostrarPosicao(solicitacoes, "Maria", sMaria);
        mostrarPosicao(solicitacoes, "João", sJoao);
        mostrarPosicao(solicitacoes, "Bia", sBia);

        System.out.println("\n=== CANCELANDO ===");
        tentarOk("Bia cancela o próprio pedido pendente", () -> solicitacoes.cancelar(bia, sBia2.getId()));
        System.out.println("     resíduo voltou pra: " + residuos.buscarPorId(rBia2.getId()).getStatus());

        System.out.println("\n=== REINICIANDO O PROGRAMA (lendo tudo do banco) ===");
        Instanciador inst2 = new Instanciador();
        GerenciarSolicitacoesService solicitacoes2 = inst2.getSolicitacoesService();
        mostrarPosicao(solicitacoes2, "Maria", sMaria);
        mostrarPosicao(solicitacoes2, "João", sJoao);
        mostrarPosicao(solicitacoes2, "Bia", sBia);

        System.out.println("\n=== COLETA (depois de reiniciar) ===");
        Solicitacao pega = solicitacoes2.pegarProxima(carlos);
        System.out.println("[OK] Carlos pegou: " + pega.getId() + " | " + pega.getStatus());
        System.out.println("     é a mais antiga das que ainda estavam na fila (pode ser de uma rodada anterior)");
        tentarOk("Carlos conclui a coleta", () -> solicitacoes2.concluir(carlos, pega.getId()));

        System.out.println("\n=== REGRAS ===");
        tentarBarrar("Maria pegando solicitação (é usuário comum)", () -> solicitacoes2.pegarProxima(maria));
        tentarBarrar("João cancelando pedido da Bia", () -> solicitacoes2.cancelar(joao, sBia.getId()));
        tentarBarrar("pedir o mesmo resíduo duas vezes", () -> solicitacoes2.solicitar(joao, rJoao.getId()));
        tentarBarrar("Maria pedindo resíduo do João", () -> solicitacoes2.solicitar(maria, rJoao.getId()));
        tentarBarrar("cancelar pedido já cancelado", () -> solicitacoes2.cancelar(bia, sBia2.getId()));

        System.out.println("\n=== GERENTE ===");
        tentarOk("Ana (gerente) cancela o pedido da Bia", () -> solicitacoes2.cancelar(ana, sBia.getId()));
    }

    static Solicitacao pedir(GerenciarSolicitacoesService s, Usuario quem, Residuo r) {
        Solicitacao sol = s.solicitar(quem, r.getId());
        System.out.println("[OK] " + quem.getNomeCompleto() + " pediu | " + sol.getDataSolicitacao());
        return sol;
    }

    static void mostrarPosicao(GerenciarSolicitacoesService s, String nome, Solicitacao sol) {
        try {
            System.out.println(nome + ": posição " + s.posicaoNaFila(sol.getId()));
        } catch (RuntimeException e) {
            System.out.println(nome + ": " + e.getMessage());
        }
    }

    static void tentarOk(String nome, Runnable acao) {
        try {
            acao.run();
            System.out.println("[OK] " + nome);
        } catch (RuntimeException e) {
            System.out.println("[FALHA] " + nome + ": " + e.getMessage());
        }
    }

    static void tentarBarrar(String nome, Runnable acao) {
        try {
            acao.run();
            System.out.println("[FALHA] " + nome + ": deixou passar");
        } catch (RuntimeException e) {
            System.out.println("[OK] " + nome + " barrado: " + e.getMessage());
        }
    }
}