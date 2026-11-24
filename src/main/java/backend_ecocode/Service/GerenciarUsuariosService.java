package backend_ecocode.Service;

import backend_ecocode.Domain.CargoUsuario;
import backend_ecocode.Domain.ServiceUsuarioInterface;
import backend_ecocode.Domain.Usuario;
import backend_ecocode.Exceptions.AcessoNegadoException;
import backend_ecocode.Repository.GerenciarUsuariosRepository;
import backend_ecocode.Util.CifraCesar;
import backend_ecocode.Util.ValidadorCpf;
import backend_ecocode.Util.ValidarEmail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GerenciarUsuariosService implements ServiceUsuarioInterface {
    private final GerenciarUsuariosRepository gerenciarUsuariosRepository;
    private final CifraCesar cifraCesar = new CifraCesar();

    public GerenciarUsuariosService(GerenciarUsuariosRepository gerenciarUsuariosRepository) {
        this.gerenciarUsuariosRepository = gerenciarUsuariosRepository;
    }

    @Override
    public Usuario cadastrar(CargoUsuario cargoUsuarioCadastrando, String cpf, String nomeCompleto, String email, String senha, CargoUsuario cargoUsuario, String dataNascimento) {

        if(cargoUsuarioCadastrando != CargoUsuario.GERENTE){
            throw new AcessoNegadoException(
                    "Apenas Gerentes Podem Realizar o Cadastro de Usuario"
            );
        }

        // GerenciarUsuariosService.cadastrar, logo depois da checagem do CPF
        if (gerenciarUsuariosRepository.buscarEmail(email) != null) {
            throw new IllegalArgumentException("Já existe um usuário com esse e-mail.");
        }

        if (gerenciarUsuariosRepository.buscar(ValidadorCpf.limpar(cpf)) != null) {
            throw new IllegalArgumentException("Já existe um usuário com esse CPF.");
        }

        Usuario novoUsuario = new Usuario(
                nomeCompleto, cpf, email, cifraCesar.cifraCesar(senha), cargoUsuario, dataNascimento
        );

        gerenciarUsuariosRepository.cadastrar(novoUsuario);
        return novoUsuario;
    }

    @Override
    public Usuario editar(String cpf, String nomeEditado, String emailEditado, String senhaEditada) {

        Usuario usuarioEditado = buscar(ValidadorCpf.limpar(cpf));

        if(!emailEditado.isBlank()){
            if(!ValidarEmail.ehValido(emailEditado)){
                throw new IllegalArgumentException("E-mail inválido");
            }

            Usuario encontrarEmail = gerenciarUsuariosRepository.buscarEmail(emailEditado);

            if(encontrarEmail == null || emailEditado.equals(usuarioEditado.getEmail())) {
                usuarioEditado.setEmail(emailEditado);
            } else{
                throw new IllegalArgumentException("Email já está em uso");

            }
        }

        if(!nomeEditado.isBlank()){
            usuarioEditado.setNomeCompleto(nomeEditado);
        }

        if(!senhaEditada.isBlank()) {
            usuarioEditado.setSenhaCriptografada(cifraCesar.cifraCesar(senhaEditada));
        }

        gerenciarUsuariosRepository.editar(usuarioEditado);

        return usuarioEditado;
    }

    @Override
    public Usuario buscar(String cpf) {
        Usuario usuario = gerenciarUsuariosRepository.buscar(ValidadorCpf.limpar(cpf));
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        return usuario;
    }

    @Override
    public Usuario buscarEmail(String email) {
        if(!ValidarEmail.ehValido(email)) throw new AcessoNegadoException("Insira um e-mail válido");

        Usuario usuario = gerenciarUsuariosRepository.buscarEmail(email);
        if(usuario == null){
            throw new IllegalArgumentException("Email não encontrado ou registrado");
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
}
