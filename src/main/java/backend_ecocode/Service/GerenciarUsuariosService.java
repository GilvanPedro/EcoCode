package backend_ecocode.Service;

import backend_ecocode.Domain.Enums.AcaoLog;
import backend_ecocode.Domain.Enums.Bairro;
import backend_ecocode.Domain.Enums.CargoUsuario;
import backend_ecocode.Domain.Interfaces.ServiceUsuarioInterface;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Exceptions.AcessoNegadoException;
import backend_ecocode.Repository.GerenciarUsuariosRepository;
import backend_ecocode.Util.Senhas;
import backend_ecocode.Util.ValidadorCpf;
import backend_ecocode.Util.ValidarEmail;

import java.util.List;
import java.util.UUID;

public class GerenciarUsuariosService implements ServiceUsuarioInterface {
    private final GerenciarUsuariosRepository gerenciarUsuariosRepository;
    private final GerenciarLogService log;

    public GerenciarUsuariosService(GerenciarUsuariosRepository gerenciarUsuariosRepository, GerenciarLogService log) {
        this.gerenciarUsuariosRepository = gerenciarUsuariosRepository;
        this.log = log;
    }

    @Override
    public Usuario cadastrar(String cpf, String nomeCompleto, String email, String senha, String dataNascimento, Bairro bairro) {
        return salvarNovoUsuario(cpf, nomeCompleto, email, senha, CargoUsuario.USUARIO, dataNascimento, bairro);
    }

    @Override
    public Usuario cadastroGerente(Usuario quem, String cpf, String nomeCompleto, String email, String senha, CargoUsuario cargoUsuario, String dataNascimento, Bairro bairro){
        if(quem.getCargoUsuario() != CargoUsuario.GERENTE){
            throw new AcessoNegadoException("Apenas gerente pode cadastrar um usuário com cargo diferente");
        }

        Usuario novo = salvarNovoUsuario(cpf, nomeCompleto, email, senha, cargoUsuario, dataNascimento, bairro);
        log.registrar(quem, AcaoLog.CADASTROU_USUARIO, novo.getCpf() + " (" + cargoUsuario + ")");

        return novo;
    }

    @Override
    public Usuario editar(Usuario usuarioLogado, String nomeEditado, String emailEditado, String senhaEditada, Bairro bairro) {

        if (usuarioLogado == null){
            throw new IllegalArgumentException("É preciso estar logado para editar o perfil");
        }

        Usuario usuarioEditado = buscar(usuarioLogado.getCpf());

        if(emailEditado != null && !emailEditado.isBlank()){
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

        if(nomeEditado != null && !nomeEditado.isBlank()){
            usuarioEditado.setNomeCompleto(nomeEditado);
        }

        if(senhaEditada != null && !senhaEditada.isBlank()) {
            usuarioEditado.setSenhaCriptografada(Senhas.proteger(senhaEditada));
        }

        if(bairro != null) {
            usuarioEditado.setBairro(bairro);
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
    public Usuario buscarPorId(UUID id) {
        return gerenciarUsuariosRepository.buscarPorId(id);
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
        CargoUsuario cargoAnterior = usuarioPromovido.getCargoUsuario(); // antes do set

        usuarioPromovido.setCargoUsuario(novoCargo);
        gerenciarUsuariosRepository.editar(usuarioPromovido);

        log.registrar(gerenteLogado, AcaoLog.PROMOVEU_USUARIO,
                usuarioPromovido.getCpf() + ": " + cargoAnterior + " -> " + novoCargo);

        return  usuarioPromovido;
    }

    @Override
    public List<Usuario> listar() {
        return gerenciarUsuariosRepository.listar();
    }

    @Override
    public void remover(Usuario quem, String cpf) {
        if(quem.getCargoUsuario() != CargoUsuario.GERENTE){
            throw new AcessoNegadoException(
                    "Apenas Gerentes Podem Realizar a remoção de um Usuario"
            );
        }

        // a busca tem validação de encontrar ou não a pessoa
        Usuario usuario = buscar(cpf);

        if (quem.getCpf().equals(usuario.getCpf())){
            throw new IllegalArgumentException("O gerente não pode se excluir");
        }

        log.registrar(quem, AcaoLog.REMOVEU_USUARIO, usuario.getCpf() + " (" + usuario.getNomeCompleto() + ")");

        gerenciarUsuariosRepository.remover(usuario.getCpf());

    }

    private Usuario salvarNovoUsuario(String cpf, String nomeCompleto, String email, String senha, CargoUsuario cargoUsuario, String dataNascimento, Bairro bairro){

        if (gerenciarUsuariosRepository.buscarEmail(email) != null) {
            throw new IllegalArgumentException("Já existe um usuário com esse e-mail.");
        }

        if (gerenciarUsuariosRepository.buscar(ValidadorCpf.limpar(cpf)) != null) {
            throw new IllegalArgumentException("Já existe um usuário com esse CPF.");
        }

        if(bairro == null){
            throw new IllegalArgumentException("Selecione um bairro com um endereço próximo a sua localidade");
        }

        Usuario novoUsuario = new Usuario(
                nomeCompleto, cpf, email, Senhas.proteger(senha), cargoUsuario, dataNascimento, bairro
        );

        gerenciarUsuariosRepository.cadastrar(novoUsuario);
        return novoUsuario;
    }
}
