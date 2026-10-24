package Service;

import Domain.CargoUsuario;
import Domain.ServiceUsuarioInterface;
import Domain.Usuario;
import Exceptions.AcessoNegadoException;
import Repository.GerenciarUsuariosRepository;
import Util.CifraCesar;
import Util.ValidadorCpf;

import java.util.List;

public class GerenciarUsuariosService implements ServiceUsuarioInterface {
    GerenciarUsuariosRepository gerenciarUsuariosRepository = new GerenciarUsuariosRepository();
    CifraCesar cifraCesar = new CifraCesar();

    @Override
    public Usuario cadastrar(CargoUsuario cargoUsuarioCadastrando, String cpf, String nomeCompleto, String email, String senha, CargoUsuario cargoUsuario, String dataNascimento) {

        if(cargoUsuarioCadastrando != CargoUsuario.GERENTE){
            throw new AcessoNegadoException(
                    "Apenas Gerentes Podem Realizar o Cadastro de Usuario"
            );
        }

        String cpfLimpo = validarCpf(cpf);

        if (gerenciarUsuariosRepository.buscar(cpfLimpo) != null) {
            throw new IllegalArgumentException("Já existe um usuário com esse CPF.");
        }

        Usuario novoUsuario = new Usuario(
                nomeCompleto, cpfLimpo, email, cifraCesar.cifraCesar(senha), cargoUsuario, dataNascimento
        );

        gerenciarUsuariosRepository.cadastrar(novoUsuario);
        return novoUsuario;
    }

    @Override
    public Usuario editar(String cpf, String nomeEditado, String emailEditado, String senhaEditada) {

        Usuario usuarioEditado = buscar(cpf);

        usuarioEditado.setNomeCompleto(nomeEditado);
        usuarioEditado.setEmail(emailEditado);
        usuarioEditado.setSenhaCriptografada(cifraCesar.cifraCesar(senhaEditada));

        gerenciarUsuariosRepository.editar(usuarioEditado);

        return usuarioEditado;
    }

    @Override
    public Usuario buscar(String cpf) {
        String cpfLimpo = validarCpf(cpf);

        Usuario usuario = gerenciarUsuariosRepository.buscar(cpfLimpo);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        return usuario;
    }

    @Override
    public List<Usuario> listar() {
        return gerenciarUsuariosRepository.listar();
    }

    @Override
    public void remover(CargoUsuario cargoUsuario, String cpf) {
        if(cargoUsuario != CargoUsuario.GERENTE){
            throw new AcessoNegadoException(
                    "Apenas Gerentes Podem Realizar o Cadastro de Usuario"
            );
        }

        // a busca tem validação de encontrar ou não a pessoa
        Usuario usuario = buscar(cpf);
        gerenciarUsuariosRepository.remover(usuario.getCpf());
    }

    private String validarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("Informe um CPF.");
        }
        String cpfLimpo = cpf.replaceAll("\\D", ""); // só os dígitos
        if (!ValidadorCpf.ehValido(cpfLimpo)) {
            throw new IllegalArgumentException("Informe um CPF válido.");
        }
        return cpfLimpo;
    }
}
