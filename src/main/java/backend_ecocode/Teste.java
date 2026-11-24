package backend_ecocode;

import backend_ecocode.Domain.*;
import backend_ecocode.Service.GerenciarResiduosService;
import backend_ecocode.Service.GerenciarSolicitacoesService;

public class Teste{

    public static void main(String[] args) {
        Instanciador inst = new Instanciador();
        GerenciarResiduosService residuos = inst.getResiduosService();
        GerenciarSolicitacoesService solicitacoes = inst.getSolicitacoesService();

        Usuario usuario = new Usuario("Usuário Teste", "529.982.247-25",
                "usuario@teste.com", "senha123", CargoUsuario.USUARIO, "10/05/2000");

        Usuario funcionario = new Usuario("Funcionário Teste", "111.444.777-35",
                "funcionario@teste.com", "senha123", CargoUsuario.FUNCIONARIO, "15/08/1995");

        // 1. pedir: o resíduo sai da lista de disponíveis
        Residuo r1 = residuos.salvar(TipoResiduo.values()[0], 2, "teste 1");
        Solicitacao s1 = solicitacoes.solicitar(usuario, r1.getId());
        conferir("1. status vira EM_SOLICITACAO",
                residuos.buscarPorId(r1.getId()).getStatus() == StatusResiduo.EM_SOLICITACAO);
        conferir("1. some dos disponíveis", !residuos.listarDisponiveis().contains(r1));

        // 2. pedir o mesmo de novo: tem que dar erro
        try {
            solicitacoes.solicitar(usuario, r1.getId());
            conferir("2. pedir de novo dá erro", false);
        } catch (IllegalArgumentException e) {
            conferir("2. pedir de novo dá erro", true);
        }

        // 3. cancelar: volta pra DISPONIVEL
        solicitacoes.cancelar(usuario, s1.getId());
        conferir("3. cancelar volta pra DISPONIVEL",
                residuos.buscarPorId(r1.getId()).getStatus() == StatusResiduo.DISPONIVEL);
        conferir("3. volta pros disponíveis", residuos.listarDisponiveis().contains(r1));

        // 4. concluir: vira DESCARTADO
        Residuo r2 = residuos.salvar(TipoResiduo.values()[0], 1, "teste 2");
        solicitacoes.solicitar(usuario, r2.getId());
        Solicitacao emRota = solicitacoes.pegarProxima(funcionario);
        solicitacoes.concluir(funcionario, emRota.getId());
        conferir("4. concluir vira DESCARTADO",
                residuos.buscarPorId(emRota.getResiduoId()).getStatus() == StatusResiduo.DESCARTADO);
    }

    private static void conferir(String nome, boolean passou) {
        System.out.println((passou ? "[OK]    " : "[FALHOU] ") + nome);
    }
}