package backend_ecocode.Testes;

import backend_ecocode.Domain.Entities.Residuo;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.TipoResiduo;
import backend_ecocode.Instanciador;
import backend_ecocode.Service.GerenciarResiduosService;
import backend_ecocode.Service.GerenciarUsuariosService;

public class TesteResiduos {

    public static void main(String[] args) {
        Instanciador inst = new Instanciador();
        GerenciarUsuariosService usuarios = inst.getUsuariosService();
        GerenciarResiduosService residuos = inst.getResiduosService();

        // usuários que já estão no banco (do TesteUsuarios)
        Usuario maria = usuarios.buscar("52998224725");
        Usuario joao = usuarios.buscar("12345678909");
        Usuario bia = usuarios.buscar("71460238001");

        System.out.println("=== CADASTRANDO ===");
        Residuo notebook = cadastrar(residuos, maria, TipoResiduo.COMPUTADORES, 1, "Notebook velho que não liga");
        cadastrar(residuos, maria, TipoResiduo.CABOS, 5, "Cabos USB e carregadores");
        Residuo celular = cadastrar(residuos, joao, TipoResiduo.CELULARES, 2, "Dois celulares com tela quebrada");
        cadastrar(residuos, bia, TipoResiduo.BATERIAS, 4, "Pilhas AA usadas");
        cadastrar(residuos, bia, TipoResiduo.OUTROS, 1, "Controle remoto");

        System.out.println("\n=== EDITANDO ===");
        tentarOk("Maria edita o próprio notebook", () ->
                residuos.editar(maria, notebook.getCodigo(), 2, "Dois notebooks agora"));

        System.out.println("\n=== EXCLUINDO ===");
        tentarOk("João exclui o próprio celular", () ->
                residuos.excluir(joao, celular.getCodigo()));

        System.out.println("\n=== REGRAS ===");
        tentarBarrar("João editando resíduo da Maria", () ->
                residuos.editar(joao, notebook.getCodigo(), 3, "invadindo"));
        tentarBarrar("Bia excluindo resíduo da Maria", () ->
                residuos.excluir(bia, notebook.getCodigo()));
        tentarBarrar("quantidade zero", () ->
                residuos.salvar(maria, TipoResiduo.CABOS, 0, "nada"));
        tentarBarrar("sem tipo", () ->
                residuos.salvar(maria, null, 1, "sem tipo"));
        tentarBarrar("sem dono", () ->
                residuos.salvar(null, TipoResiduo.CABOS, 1, "sem dono"));

        System.out.println("\n=== COMO FICOU NO BANCO ===");
        for (Residuo r : residuos.listar()) {
            Usuario dono = usuarios.buscarPorId(r.getUsuarioId());
            System.out.printf("%-12s | %-13s | qtd %d | %-10s | %s%n",
                    r.getCodigo(), r.getResiduo(), r.getQuantidade(),
                    r.getStatus(), dono.getNomeCompleto());
        }
    }

    static Residuo cadastrar(GerenciarResiduosService s, Usuario dono, TipoResiduo tipo,
                             int qtd, String descricao) {
        Residuo r = s.salvar(dono, tipo, qtd, descricao);
        System.out.println("[OK] " + r.getCodigo() + " de " + dono.getNomeCompleto());
        return r;
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