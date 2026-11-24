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
    public Usuario cadastrar(String cpf, String nomeCompleto, String email, String senha, String dataNascimento) {
        return salvarNovoUsuario(cpf, nomeCompleto, email, senha, CargoUsuario.USUARIO, dataNascimento);
    }

    @Override
    public Usuario cadastroGerente(CargoUsuario cargoUsuarioCadastrando, String cpf, String nomeCompleto, String email, String senha, CargoUsuario cargoUsuario, String dataNascimento){
        if(cargoUsuarioCadastrando != CargoUsuario.GERENTE){
            throw new AcessoNegadoException("Apenas gerente pode cadastrar um usuário com cargo diferente");
        }

        return salvarNovoUsuario(cpf, nomeCompleto, email, senha, cargoUsuario, dataNascimento);
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
    public Usuario promoverUsuario(Usuario gerenteLogado, String cpf, CargoUsuario novoCargo) {
        String cpfLimpo = ValidadorCpf.limpar(cpf);

        if(gerenteLogado.getCargoUsuario() != CargoUsuario.GERENTE){
            throw new AcessoNegadoException("Apenas gerente pode promover um usuário");
        }

        if(gerenteLogado.getCpf().equals(cpfLimpo)){
            throw new IllegalArgumentException("O gerente não pode mudar o seu próprio cargo");
        }

        if(novoCargo == null){
            throw new IllegalArgumentException("O novo cargo não pode estar vazio");
        }

        Usuario usuarioPromovido = buscar(cpfLimpo);

        usuarioPromovido.setCargoUsuario(novoCargo);
        gerenciarUsuariosRepository.editar(usuarioPromovido);

        return  usuarioPromovido;
    }

    @Override
    public List<Usuario> listar() {
        return gerenciarUsuariosRepository.listar();
    }

    @Override
    public void remover(CargoUsuario cargoUsuario, String cpf) {
        if(cargoUsuario != CargoUsuario.GERENTE){
            throw new AcessoNegadoException(
                    "Apenas Gerentes Podem Realizar a remoção de um Usuario"
            );
        }

        // a busca tem validação de encontrar ou não a pessoa
        Usuario usuario = buscar(cpf);
        gerenciarUsuariosRepository.remover(usuario.getCpf());
    }

    private Usuario salvarNovoUsuario(String cpf, String nomeCompleto, String email, String senha, CargoUsuario cargoUsuario, String dataNascimento){

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
}
