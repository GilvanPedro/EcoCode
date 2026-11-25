package backend_ecocode;

import Estrutura.Dijkstra;
import Estrutura.Grafo;
import backend_ecocode.Domain.*;
import backend_ecocode.Exceptions.AcessoNegadoException;
import backend_ecocode.Service.*;
import backend_ecocode.Util.GrafoBairros;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Teste {

    static int ok = 0, falhou = 0;

    public static void main(String[] args) {
        Instanciador inst = new Instanciador();
        GerenciarResiduosService residuos = inst.getResiduosService();
        GerenciarSolicitacoesService solicitacoes = inst.getSolicitacoesService();
        GerenciarPontoColetaService pontos = inst.getPontoColetaService();

        // usuários de teste (não vão pro JSON de usuários)
        Usuario maria = new Usuario("Maria Souza", "529.982.247-25",
                "maria@teste.com", "senha123", CargoUsuario.USUARIO, "10/05/2000",
                Bairro.SETOR_BUENO);
        Usuario joao = new Usuario("João Lima", "123.456.789-09",
                "joao@teste.com", "senha123", CargoUsuario.USUARIO, "02/02/1998",
                Bairro.JARDIM_CURITIBA);
        Usuario coletor = new Usuario("Carlos Coletor", "111.444.777-35",
                "carlos@teste.com", "senha123", CargoUsuario.FUNCIONARIO, "15/08/1995",
                Bairro.SETOR_CENTRAL);
        Usuario gerente = new Usuario("Ana Gerente", "390.533.447-05",
                "ana@teste.com", "senha123", CargoUsuario.GERENTE, "20/03/1990",
                Bairro.PARQUE_AMAZONIA);

        // ================================================================
        System.out.println("=== RESÍDUOS ===");
        Residuo notebook = residuos.salvar(TipoResiduo.COMPUTADORES, 1, "Notebook Dell Inspiron que não liga mais");
        Residuo celular  = residuos.salvar(TipoResiduo.CELULARES, 2, "Dois celulares Samsung com tela quebrada");
        Residuo cabos    = residuos.salvar(TipoResiduo.CABOS, 5, "Cabos USB, carregadores e um mouse com fio");
        Residuo pilhas   = residuos.salvar(TipoResiduo.BATERIAS, 12, "Pilhas AA usadas de controle remoto");
        Residuo tv       = residuos.salvar(TipoResiduo.OUTROS, 1, "TV de tubo 21 polegadas");
        Residuo tablet   = residuos.salvar(TipoResiduo.CELULARES, 1, "Tablet antigo com bateria estufada");

        Residuo[] todos = {notebook, celular, cabos, pilhas, tv, tablet};
        for (Residuo r : todos) {
            conferir("cadastra com UUID: " + r.getDescricao(), r.getId() != null);
            conferir("começa DISPONIVEL: " + r.getDescricao(), r.getStatus() == StatusResiduo.DISPONIVEL);
        }

        conferir("buscarPorId acha o notebook",
                residuos.buscarPorId(notebook.getId()).getId().equals(notebook.getId()));
        conferir("buscar pelo código acha o notebook",
                residuos.buscar(notebook.getCodigo()).getId().equals(notebook.getId()));
        conferir("código do computador termina em 0 ou 5 (resto 0)",
                Integer.parseInt(notebook.getCodigo()) % 5 == TipoResiduo.COMPUTADORES.getResto());
        conferir("código do celular tem resto 1",
                Integer.parseInt(celular.getCodigo()) % 5 == TipoResiduo.CELULARES.getResto());
        conferir("os seis aparecem nos disponíveis", contemTodos(residuos, todos));

        esperaErro("quantidade 0 dá erro", () -> residuos.salvar(TipoResiduo.CABOS, 0, "x"));
        esperaErro("quantidade negativa dá erro", () -> residuos.salvar(TipoResiduo.CABOS, -3, "x"));
        esperaErro("tipo null dá erro", () -> residuos.salvar(null, 1, "x"));
        esperaErro("código com letra dá erro", () -> residuos.buscar("12a"));
        esperaErro("código inexistente dá erro", () -> residuos.buscar("999999"));
        esperaErro("buscarPorId null dá erro", () -> residuos.buscarPorId(null));

        residuos.editar(cabos.getCodigo(), 7, "Cabos USB, carregadores, mouse e teclado");
        conferir("editar muda a quantidade", residuos.buscarPorId(cabos.getId()).getQuantidade() == 7);
        esperaErro("editar com quantidade 0 dá erro", () -> residuos.editar(cabos.getCodigo(), 0, "x"));

        // ================================================================
        System.out.println("=== SOLICITAÇÃO + RN13 ===");
        Solicitacao sNotebook = solicitacoes.solicitar(maria, notebook.getId());
        conferir("solicitação nasce PENDENTE", sNotebook.getStatus() == StatusSolicitacao.PENDENTE);
        conferir("resíduo vira EM_SOLICITACAO",
                residuos.buscarPorId(notebook.getId()).getStatus() == StatusResiduo.EM_SOLICITACAO);
        conferir("notebook some dos disponíveis", naoContem(residuos, notebook));
        conferir("celular continua nos disponíveis", !naoContem(residuos, celular));

        esperaErro("pedir o mesmo resíduo de novo dá erro", () -> solicitacoes.solicitar(maria, notebook.getId()));
        esperaErro("outra pessoa pedir o mesmo dá erro", () -> solicitacoes.solicitar(joao, notebook.getId()));
        esperaErro("solicitante null dá erro", () -> solicitacoes.solicitar(null, celular.getId()));
        esperaErro("resíduo null dá erro", () -> solicitacoes.solicitar(maria, null));

        Solicitacao sCelular = solicitacoes.solicitar(maria, celular.getId());
        Solicitacao sPilhas  = solicitacoes.solicitar(joao, pilhas.getId());
        Solicitacao sTv      = solicitacoes.solicitar(joao, tv.getId());

        conferir("Maria tem pelo menos 2 solicitações", solicitacoes.listarDoUsuario(maria.getId()).size() >= 2);
        conferir("posição do celular vem depois do notebook",
                solicitacoes.posicaoNaFila(sCelular.getId()) > solicitacoes.posicaoNaFila(sNotebook.getId()));

        System.out.println("--- cancelar ---");
        esperaAcessoNegado("João não cancela pedido da Maria", () -> solicitacoes.cancelar(joao, sCelular.getId()));
        solicitacoes.cancelar(maria, sCelular.getId());
        conferir("dona cancela: vira CANCELADA", sCelular.getStatus() == StatusSolicitacao.CANCELADA);
        conferir("cancelar devolve o celular pra DISPONIVEL",
                residuos.buscarPorId(celular.getId()).getStatus() == StatusResiduo.DISPONIVEL);
        conferir("celular volta pros disponíveis", !naoContem(residuos, celular));

        solicitacoes.cancelar(gerente, sTv.getId());
        conferir("gerente cancela pedido de outro", sTv.getStatus() == StatusSolicitacao.CANCELADA);
        conferir("TV volta pra DISPONIVEL",
                residuos.buscarPorId(tv.getId()).getStatus() == StatusResiduo.DISPONIVEL);

        Solicitacao sCelular2 = solicitacoes.solicitar(joao, celular.getId());
        conferir("resíduo cancelado pode ser pedido de novo", sCelular2.getStatus() == StatusSolicitacao.PENDENTE);

        System.out.println("--- rota e conclusão ---");
        esperaAcessoNegado("usuário comum não pega da fila", () -> solicitacoes.pegarProxima(maria));
        esperaAcessoNegado("usuário comum não conclui", () -> solicitacoes.concluir(maria, sNotebook.getId()));
        esperaErro("concluir sem estar em rota dá erro", () -> solicitacoes.concluir(coletor, sPilhas.getId()));

        // a fila é FIFO: as pendentes antigas do JSON saem antes, então pega até chegar no notebook
        pegarAte(solicitacoes, coletor, sNotebook);
        conferir("pegarProxima põe o notebook EM_ROTA", sNotebook.getStatus() == StatusSolicitacao.EM_ROTA);

        solicitacoes.concluir(coletor, sNotebook.getId());
        conferir("concluir vira CONCLUIDA", sNotebook.getStatus() == StatusSolicitacao.CONCLUIDA);
        conferir("notebook vira DESCARTADO",
                residuos.buscarPorId(notebook.getId()).getStatus() == StatusResiduo.DESCARTADO);
        conferir("notebook descartado não volta pros disponíveis", naoContem(residuos, notebook));
        esperaErro("concluir duas vezes dá erro", () -> solicitacoes.concluir(coletor, sNotebook.getId()));
        esperaErro("pedir resíduo descartado dá erro", () -> solicitacoes.solicitar(maria, notebook.getId()));

        pegarAte(solicitacoes, coletor, sPilhas);
        solicitacoes.cancelar(gerente, sPilhas.getId());
        conferir("gerente cancela pedido em rota", sPilhas.getStatus() == StatusSolicitacao.CANCELADA);
        conferir("pilhas voltam pra DISPONIVEL",
                residuos.buscarPorId(pilhas.getId()).getStatus() == StatusResiduo.DISPONIVEL);

        // ================================================================
        System.out.println("=== PONTOS DE COLETA (ecopontos da Comurg) ===");
        // os 5 ficam salvos no JSON; se já existirem, reaproveita em vez de duplicar
        List<PontoColeta> ecopontos = new ArrayList<>();
        ecopontos.add(garantirPonto(pontos, gerente, "Ecoponto Jardim Guanabara II",
                "Rua GB-5 com Rua GB-6", Bairro.JARDIM_GUANABARA));
        ecopontos.add(garantirPonto(pontos, gerente, "Ecoponto Setor Faiçalville",
                "Av. Nadra Bufaiçal com Av. Madri", Bairro.SETOR_FAICALVILLE));
        ecopontos.add(garantirPonto(pontos, gerente, "Ecoponto Jardim São José",
                "Rua Frei Nazareno Confaloni com Rua Irmã Maria Bernarda", Bairro.JARDIM_SAO_JOSE));
        ecopontos.add(garantirPonto(pontos, gerente, "Ecoponto Residencial Campos Dourados",
                "Rua São João Del Rei, APM 07", Bairro.RESIDENCIAL_CAMPOS_DOURADOS));
        ecopontos.add(garantirPonto(pontos, gerente, "Ecoponto Parque Eldorado Oeste",
                "Rua Elo, 43", Bairro.PARQUE_ELDORADO_OESTE));

        for (PontoColeta p : ecopontos) {
            conferir("gerente cadastra: " + p.getNome(), pontos.buscarPorId(p.getId()) != null);
        }
        conferir("listar traz os 5 ecopontos", contemTodosPontos(pontos, ecopontos));

        esperaAcessoNegado("usuário não cadastra",
                () -> pontos.cadastrar(maria, "X", "Y", Bairro.SETOR_CENTRAL));
        esperaAcessoNegado("funcionário não cadastra",
                () -> pontos.cadastrar(coletor, "X", "Y", Bairro.SETOR_CENTRAL));
        esperaErro("usuário null dá erro",
                () -> pontos.cadastrar(null, "X", "Y", Bairro.SETOR_CENTRAL));
        esperaErro("nome vazio dá erro",
                () -> pontos.cadastrar(gerente, " ", "Y", Bairro.SETOR_CENTRAL));
        esperaErro("endereço null dá erro",
                () -> pontos.cadastrar(gerente, "X", null, Bairro.SETOR_CENTRAL));
        esperaErro("bairro null dá erro",
                () -> pontos.cadastrar(gerente, "X", "Y", null));

        PontoColeta guanabara = ecopontos.get(0);
        pontos.editar(gerente, guanabara.getId(), null, "Rua GB-5, esquina com GB-6", null);
        PontoColeta editado = pontos.buscarPorId(guanabara.getId());
        conferir("editar só o endereço muda o endereço", editado.getEndereco().equals("Rua GB-5, esquina com GB-6"));
        conferir("editar só o endereço mantém o nome", editado.getNome().equals("Ecoponto Jardim Guanabara II"));
        conferir("editar só o endereço mantém o bairro", editado.getBairro() == Bairro.JARDIM_GUANABARA);

        pontos.editar(gerente, guanabara.getId(), "  Ecoponto Guanabara  ", " ", null);
        editado = pontos.buscarPorId(guanabara.getId());
        conferir("nome novo vem sem espaços nas pontas", editado.getNome().equals("Ecoponto Guanabara"));
        conferir("endereço em branco mantém o antigo", editado.getEndereco().equals("Rua GB-5, esquina com GB-6"));

        // volta o Guanabara pro nome e endereço reais
        pontos.editar(gerente, guanabara.getId(), "Ecoponto Jardim Guanabara II", "Rua GB-5 com Rua GB-6", null);
        conferir("Guanabara voltou ao nome real",
                pontos.buscarPorId(guanabara.getId()).getNome().equals("Ecoponto Jardim Guanabara II"));

        esperaAcessoNegado("usuário não edita",
                () -> pontos.editar(maria, guanabara.getId(), "Hack", null, null));
        esperaAcessoNegado("funcionário não exclui", () -> pontos.excluir(coletor, guanabara.getId()));
        esperaErro("buscar ponto com id null dá erro", () -> pontos.buscarPorId(null));

        // testa o excluir num ponto temporário, sem mexer nos 5 reais
        PontoColeta temporario = pontos.cadastrar(gerente, "Ponto Temporário de Teste", "Rua Teste, 1", Bairro.SETOR_CENTRAL);
        pontos.excluir(gerente, temporario.getId());
        esperaErro("depois de excluir não acha mais", () -> pontos.buscarPorId(temporario.getId()));
        esperaErro("excluir de novo dá erro", () -> pontos.excluir(gerente, temporario.getId()));

        esperaErro("usuário sem bairro dá erro", () -> new Usuario("Sem Bairro", "529.982.247-25",
                "sem@teste.com", "senha123", CargoUsuario.USUARIO, "10/05/2000", null));

        // ================================================================
        System.out.println("=== GRAFO ===");
        Grafo<Bairro> grafo = GrafoBairros.criar();
        conferir("grafo foi criado", grafo != null);

        Dijkstra<Bairro> dijkstra = new Dijkstra<>(grafo);
        Map<Bairro, Double> d = dijkstra.calcular(Bairro.PARQUE_AMAZONIA);
        conferir("Pq Amazônia -> Campos Dourados = 15.0 (não 15.3)",
                Math.abs(d.get(Bairro.RESIDENCIAL_CAMPOS_DOURADOS) - 15.0) < 0.01);

        Map<Bairro, Double> c = dijkstra.calcular(Bairro.SETOR_CENTRAL);
        conferir("Central -> Pedro Ludovico = 7.1 (pelo Marista)",
                Math.abs(c.get(Bairro.SETOR_PEDRO_LUDOVICO) - 7.1) < 0.01);
        conferir("Central -> Central = 0", c.get(Bairro.SETOR_CENTRAL) == 0.0);

        System.out.println("=== PONTO MAIS PRÓXIMO ===");
        conferir("Maria (Bueno) -> Faiçalville",
                pontos.pontoMaisProximo(maria).getBairro() == Bairro.SETOR_FAICALVILLE);
        conferir("João (Jd Curitiba) -> Jardim Guanabara",
                pontos.pontoMaisProximo(joao).getBairro() == Bairro.JARDIM_GUANABARA);
        conferir("Carlos (Central) -> Jardim Guanabara",
                pontos.pontoMaisProximo(coletor).getBairro() == Bairro.JARDIM_GUANABARA);
        conferir("Ana (Pq Amazônia) -> Faiçalville",
                pontos.pontoMaisProximo(gerente).getBairro() == Bairro.SETOR_FAICALVILLE);
        esperaErro("usuário null dá erro", () -> pontos.pontoMaisProximo(null));

        System.out.println();
        System.out.println("Passaram: " + ok + " | Falharam: " + falhou);
    }

    // ---------------- ajudantes ----------------

    static void pegarAte(GerenciarSolicitacoesService solicitacoes, Usuario coletor, Solicitacao alvo) {
        for (int i = 0; i < 200; i++) {
            Solicitacao pega = solicitacoes.pegarProxima(coletor);
            if (pega.getId().equals(alvo.getId())) {
                return;
            }
        }
    }

    // cadastra o ponto só se ainda não existir um com esse nome
    static PontoColeta garantirPonto(GerenciarPontoColetaService pontos, Usuario gerente,
                                     String nome, String endereco, Bairro bairro) {
        for (PontoColeta p : pontos.listar())
            if (p.getNome().equals(nome)) return p;
        return pontos.cadastrar(gerente, nome, endereco, bairro);
    }

    static boolean naoContem(GerenciarResiduosService residuos, Residuo r) {
        for (Residuo x : residuos.listarDisponiveis())
            if (r.getId().equals(x.getId())) return false;
        return true;
    }

    static boolean contemTodos(GerenciarResiduosService residuos, Residuo[] lista) {
        for (Residuo r : lista)
            if (naoContem(residuos, r)) return false;
        return true;
    }

    static boolean contemTodosPontos(GerenciarPontoColetaService pontos, List<PontoColeta> lista) {
        for (PontoColeta p : lista) {
            boolean achou = false;
            for (PontoColeta x : pontos.listar())
                if (p.getId().equals(x.getId())) achou = true;
            if (!achou) return false;
        }
        return true;
    }

    static void conferir(String nome, boolean passou) {
        if (passou) ok++; else falhou++;
        System.out.println((passou ? "[OK]    " : "[FALHA] ") + nome);
    }

    static void esperaErro(String nome, Runnable acao) {
        try {
            acao.run();
            conferir(nome, false);
        } catch (IllegalArgumentException | IllegalStateException e) {
            conferir(nome, true);
        }
    }

    static void esperaAcessoNegado(String nome, Runnable acao) {
        try {
            acao.run();
            conferir(nome, false);
        } catch (AcessoNegadoException e) {
            conferir(nome, true);
        }
    }
}
