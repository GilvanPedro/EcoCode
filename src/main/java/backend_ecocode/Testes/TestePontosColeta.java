package backend_ecocode;

import backend_ecocode.Domain.Entities.PontoColeta;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.Bairro;
import backend_ecocode.Service.GerenciarPontoColetaService;
import backend_ecocode.Service.GerenciarUsuariosService;

public class TestePontosColeta {

    public static void main(String[] args) {
        Instanciador inst = new Instanciador();
        GerenciarUsuariosService usuarios = inst.getUsuariosService();
        GerenciarPontoColetaService pontos = inst.getPontoColetaService();

        Usuario ana = usuarios.buscar("39053344705");    // gerente
        Usuario carlos = usuarios.buscar("11144477735"); // funcionário
        Usuario maria = usuarios.buscar("52998224725");
        Usuario joao = usuarios.buscar("12345678909");
        Usuario bia = usuarios.buscar("71460238001");

        System.out.println("=== CADASTRANDO OS ECOPONTOS ===");
        cadastrar(pontos, ana, "Ecoponto Jardim São José",
                "Rua Frei Nazareno Confaloni com Rua Irmã Maria Bernarda", Bairro.JARDIM_SAO_JOSE);
        cadastrar(pontos, ana, "Ecoponto Jardim Guanabara II",
                "Rua GB-5 com Rua GB-6", Bairro.JARDIM_GUANABARA);
        cadastrar(pontos, ana, "Ecoponto Setor Faiçalville",
                "Av. Nadra Bufaiçal com Av. Madri", Bairro.SETOR_FAICALVILLE);
        cadastrar(pontos, ana, "Ecoponto Residencial Campos Dourados",
                "Rua São João Del Rei, APM 07", Bairro.RESIDENCIAL_CAMPOS_DOURADOS);
        cadastrar(pontos, ana, "Ecoponto Parque Eldorado Oeste",
                "Rua Elo, 43", Bairro.PARQUE_ELDORADO_OESTE);

        System.out.println("\n=== EDITAR E REMOVER (ponto temporário) ===");
        PontoColeta temp = pontos.cadastrar(ana, "Ponto Teste", "Rua Teste, 1", Bairro.SETOR_CENTRAL);
        System.out.println("[OK] criado: " + temp.getNome());
        tentarOk("Ana edita o ponto", () ->
                pontos.editar(ana, temp.getId(), "Ponto Teste Editado", "Rua Teste, 2", Bairro.SETOR_OESTE));
        System.out.println("     agora: " + pontos.buscarPorId(temp.getId()).getNome()
                + " | " + pontos.buscarPorId(temp.getId()).getBairro());

        System.out.println("\n=== REGRAS ===");
        tentarBarrar("Carlos (funcionário) cadastrando ponto", () ->
                pontos.cadastrar(carlos, "Ponto do Carlos", "Rua X", Bairro.SETOR_CENTRAL));
        tentarBarrar("Maria (usuária) editando ponto", () ->
                pontos.editar(maria, temp.getId(), "Invadido", "Rua Y", Bairro.SETOR_BUENO));
        tentarBarrar("Maria (usuária) excluindo ponto", () ->
                pontos.excluir(maria, temp.getId()));
        tentarBarrar("ponto sem nome", () ->
                pontos.cadastrar(ana, "", "Rua Z", Bairro.SETOR_CENTRAL));
        tentarBarrar("ponto sem bairro", () ->
                pontos.cadastrar(ana, "Sem Bairro", "Rua Z", null));

        tentarOk("Ana remove o ponto temporário", () -> pontos.excluir(ana, temp.getId()));

        System.out.println("\n=== PONTO MAIS PRÓXIMO (Dijkstra) ===");
        for (Usuario u : new Usuario[]{ana, carlos, maria, joao, bia}) {
            try {
                PontoColeta p = pontos.pontoMaisProximo(u);
                System.out.printf("%-14s | mora em %-17s -> %s%n",
                        u.getNomeCompleto(), u.getBairro(), p.getNome());
            } catch (RuntimeException e) {
                System.out.println(u.getNomeCompleto() + ": " + e.getMessage());
            }
        }

        System.out.println("\n=== COMO FICOU NO BANCO ===");
        for (PontoColeta p : pontos.listar()) {
            System.out.printf("%-38s | %s%n", p.getNome(), p.getBairro());
        }
    }

    // não duplica se rodar de novo
    static void cadastrar(GerenciarPontoColetaService s, Usuario quem, String nome,
                          String endereco, Bairro bairro) {
        boolean jaExiste = s.listar().stream().anyMatch(p -> p.getNome().equals(nome));
        if (jaExiste) {
            System.out.println("[--] " + nome + ": já cadastrado");
            return;
        }
        s.cadastrar(quem, nome, endereco, bairro);
        System.out.println("[OK] " + nome);
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