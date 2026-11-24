package backend_ecocode;

import backend_ecocode.Domain.CargoUsuario;
import backend_ecocode.Domain.Solicitacao;
import backend_ecocode.Domain.Usuario;
import backend_ecocode.Service.GerenciarSolicitacoesService;
import backend_ecocode.Service.GerenciarUsuariosService;

import java.util.UUID;

public class Teste {

    public static void main(String[] args) {
        Instanciador instanciador = new Instanciador();
        GerenciarUsuariosService usuarios = instanciador.getUsuariosService();
        GerenciarSolicitacoesService solicitacoes = instanciador.getSolicitacoesService();

        Usuario ana = pegarOuCriar(usuarios, "529.982.247-25", "Ana Teste", "ana@teste.com", CargoUsuario.USUARIO);
        Usuario bruno = pegarOuCriar(usuarios, "111.444.777-35", "Bruno Teste", "bruno@teste.com", CargoUsuario.USUARIO);
        Usuario gerente = pegarOuCriar(usuarios, "935.411.347-80", "Gerente Teste", "gerente@teste.com", CargoUsuario.GERENTE);

        // 1. solicitar
        Solicitacao s1 = solicitacoes.solicitar(ana, UUID.randomUUID());
        Solicitacao s2 = solicitacoes.solicitar(bruno, UUID.randomUUID());
        Solicitacao s3 = solicitacoes.solicitar(ana, UUID.randomUUID());
        System.out.println("Criadas: " + s1.getStatus() + ", " + s2.getStatus() + ", " + s3.getStatus());

        // 2. posição na fila (pode não ser 1, 2, 3 se já houver pendentes de outras rodadas)
        System.out.println("Posições: " + solicitacoes.posicaoNaFila(s1.getId()) + ", "
                + solicitacoes.posicaoNaFila(s2.getId()) + ", " + solicitacoes.posicaoNaFila(s3.getId()));

        // 3. usuário comum tentando pegar a próxima (tem que dar erro)
        testarErro("Usuário pegando a próxima", () -> solicitacoes.pegarProxima(ana));

        // 4. bruno tentando cancelar a da ana (tem que dar erro)
        testarErro("Cancelar a de outra pessoa", () -> solicitacoes.cancelar(bruno, s3.getId()));

        // 5. ana cancelando a própria, ainda pendente
        solicitacoes.cancelar(ana, s3.getId());
        System.out.println("Ana cancelou a s3: " + s3.getStatus());

        // 6. gerente pega a próxima e conclui
        Solicitacao proxima = solicitacoes.pegarProxima(gerente);
        System.out.println("Gerente pegou: " + proxima.getId() + " -> " + proxima.getStatus());

        // 7. dono tentando cancelar depois de em rota (tem que dar erro)
        Usuario dono = proxima.getUsuarioId().equals(ana.getId()) ? ana : bruno;
        testarErro("Dono cancelando em rota", () -> solicitacoes.cancelar(dono, proxima.getId()));

        solicitacoes.concluir(gerente, proxima.getId());
        System.out.println("Concluída: " + proxima.getStatus());

        // 8. concluir de novo (tem que dar erro)
        testarErro("Concluir duas vezes", () -> solicitacoes.concluir(gerente, proxima.getId()));

        // 9. gerente cancelando uma em rota
        Solicitacao outra = solicitacoes.pegarProxima(gerente);
        solicitacoes.cancelar(gerente, outra.getId());
        System.out.println("Gerente cancelou em rota: " + outra.getStatus());

        // 10. acompanhamento da ana
        System.out.println("Solicitações da Ana: " + solicitacoes.listarDoUsuario(ana.getId()).size());

        System.out.println("\nFim. Roda de novo e confere se as pendentes antigas continuam na fila.");
    }

    private static Usuario pegarOuCriar(GerenciarUsuariosService usuarios, String cpf, String nome,
                                        String email, CargoUsuario cargo) {
        try {
            return usuarios.buscar(cpf);
        } catch (IllegalArgumentException naoExiste) {
            return usuarios.cadastroGerente(CargoUsuario.GERENTE, cpf, nome, email, "senha123", cargo, "01/01/2000");
        }
    }

    private static void testarErro(String descricao, Runnable acao) {
        try {
            acao.run();
            System.out.println("FALHOU: " + descricao + " deveria dar erro");
        } catch (RuntimeException e) {
            System.out.println("OK: " + descricao + " -> " + e.getMessage());
        }
    }
}