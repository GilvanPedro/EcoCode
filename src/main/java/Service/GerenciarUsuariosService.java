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
    public Usuario cadastrar(Usuario usuarioCadastrando, String cpf, String nomeCompleto, String email, String senha, CargoUsuario cargoUsuario, String dataNascimento) {

        if(usuarioCadastrando.getCargoUsuario() != CargoUsuario.GERENTE){
            throw new AcessoNegadoException(
                    "Apenas Gerentes Podem Realizar o Cadastro de Usuario"
            );
        }

        String senhaCriptografada = cifraCesar.cifraCesar(senha);

        Usuario novoUsuario = new Usuario(
                nomeCompleto, cpf, email, senhaCriptografada, cargoUsuario, dataNascimento
        );

        gerenciarUsuariosRepository.cadastrar(novoUsuario);

        return novoUsuario;
    }

    @Override
    public Usuario editar(String cpf, String nomeEditado, String emailEditado, String senhaEditada) {

        Usuario usuarioEditado = buscar(cpf);

        if(usuarioEditado == null){
            throw new IllegalArgumentException("Usuário buscado para edição não foi encontrado.");
        }

        usuarioEditado.setNomeCompleto(nomeEditado);
        usuarioEditado.setEmail(emailEditado);
        usuarioEditado.setSenhaCriptografada(cifraCesar.cifraCesar(senhaEditada));

        return usuarioEditado;
    }

    @Override
    public Usuario buscar(String cpf) {

        if(!ValidadorCpf.ehValido(cpf)){
            throw new IllegalArgumentException("Informe Um CPF Válido");
        }

        Usuario usuarioBuscado =  gerenciarUsuariosRepository.buscar(cpf);

        if(usuarioBuscado == null) throw new IllegalArgumentException("Usuário não encontrado.");

        return usuarioBuscado;
    }

    @Override
    public List<Usuario> listar() {
        return gerenciarUsuariosRepository.listar();
    }

    @Override
    public void remover(String cpf) {
        // a busca tem validação de encontrar ou não a pessoa
        Usuario usuario = buscar(cpf);
        gerenciarUsuariosRepository.remover(usuario.getCpf());
    }
}
