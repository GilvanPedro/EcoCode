package backend_ecocode.Testes;

import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.Bairro;
import backend_ecocode.Domain.Enums.CargoUsuario;
import backend_ecocode.Instanciador;
import backend_ecocode.Service.GerenciarUsuariosService;
import backend_ecocode.Util.LogarUsuario;

public class TesteUsuarios {

    public static void main(String[] args) {
        Instanciador inst = new Instanciador();
        GerenciarUsuariosService usuarios = inst.getUsuariosService();
        LogarUsuario login = new LogarUsuario(usuarios);

        // "chave mestra" só em memória, pra conseguir criar o primeiro gerente de verdade
        Usuario sistema = new Usuario("Sistema", "987.654.321-00", "sistema@ecocode.com",
                "x", CargoUsuario.GERENTE, "01/01/2000", Bairro.SETOR_CENTRAL);

        System.out.println("=== CADASTRANDO ===");
        Usuario ana = cadastrarGerenciado(usuarios, sistema, "390.533.447-05", "Ana Ribeiro",
                "ana@ecocode.com", "Gerente@2026", CargoUsuario.GERENTE, "20/03/1990", Bairro.PARQUE_AMAZONIA);

        cadastrarGerenciado(usuarios, ana, "111.444.777-35", "Carlos Mendes",
                "carlos@ecocode.com", "Coleta123", CargoUsuario.FUNCIONARIO, "15/08/1995", Bairro.SETOR_CENTRAL);

        cadastrarComum(usuarios, "529.982.247-25", "Maria Souza",
                "maria@email.com", "maria2000", "10/05/2000", Bairro.SETOR_BUENO);
        cadastrarComum(usuarios, "123.456.789-09", "João Lima",
                "joao@email.com", "Joao@98", "02/02/1998", Bairro.JARDIM_CURITIBA);
        cadastrarComum(usuarios, "714.602.380-01", "Beatriz Alves",
                "bia@email.com", "Bia1234", "30/11/2003", Bairro.JARDIM_SAO_JOSE);

        System.out.println("\n=== COMO FICOU NO JSON ===");
        for (Usuario u : usuarios.listar()) {
            System.out.printf("%-15s | %-11s | %-17s | senha salva: %s%n",
                    u.getNomeCompleto(), u.getCargoUsuario(), u.getBairro(), u.getSenhaCriptografada());
        }

        System.out.println("\n=== LOGIN ===");
        tentarLogin(login, "maria@email.com", "maria2000");   // certo
        tentarLogin(login, "maria@email.com", "maria2001");   // senha errada
        tentarLogin(login, "ana@ecocode.com", "Gerente@2026"); // certo
        tentarLogin(login, "ninguem@email.com", "abc");       // email que não existe

        System.out.println("\n=== REGRAS ===");
        tentar("CPF repetido", () -> usuarios.cadastrar("529.982.247-25", "Outra Maria",
                "outra@email.com", "abc123", "10/05/2000", Bairro.SETOR_BUENO));
        tentar("email repetido", () -> usuarios.cadastrar("935.411.347-80", "Outra",
                "maria@email.com", "abc123", "10/05/2000", Bairro.SETOR_BUENO));
        tentar("sem bairro", () -> usuarios.cadastrar("935.411.347-80", "Sem Bairro",
                "sem@email.com", "abc123", "10/05/2000", null));
    }

    // se já existir (rodou o teste antes), só busca em vez de quebrar
    static Usuario cadastrarComum(GerenciarUsuariosService s, String cpf, String nome, String email,
                                  String senha, String nasc, Bairro bairro) {
        try {
            Usuario u = s.cadastrar(cpf, nome, email, senha, nasc, bairro);
            System.out.println("[OK] " + nome + " cadastrado(a)");
            return u;
        } catch (IllegalArgumentException e) {
            System.out.println("[--] " + nome + ": " + e.getMessage());
            return s.buscar(cpf);
        }
    }

    static Usuario cadastrarGerenciado(GerenciarUsuariosService s, Usuario quem, String cpf, String nome,
                                       String email, String senha, CargoUsuario cargo, String nasc, Bairro bairro) {
        try {
            Usuario u = s.cadastroGerente(quem, cpf, nome, email, senha, cargo, nasc, bairro);
            System.out.println("[OK] " + nome + " cadastrado(a) como " + cargo);
            return u;
        } catch (IllegalArgumentException e) {
            System.out.println("[--] " + nome + ": " + e.getMessage());
            return s.buscar(cpf);
        }
    }

    static void tentarLogin(LogarUsuario login, String email, String senha) {
        try {
            Usuario u = login.logarUsuario(email, senha);
            System.out.println("[OK]  entrou: " + u.getNomeCompleto());
        } catch (RuntimeException e) {
            System.out.println("[NÃO] " + email + " -> " + e.getMessage());
        }
    }

    static void tentar(String nome, Runnable acao) {
        try {
            acao.run();
            System.out.println("[FALHA] " + nome + ": deixou passar");
        } catch (RuntimeException e) {
            System.out.println("[OK] " + nome + " barrado: " + e.getMessage());
        }
    }
}