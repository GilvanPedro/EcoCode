package backend_ecocode.Domain.Interfaces;

import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.Bairro;
import backend_ecocode.Domain.Enums.CargoUsuario;

import java.util.List;

public interface ServiceUsuarioInterface {
    public Usuario cadastrar(String cpf, String nomeCompleto, String email, String senha, String dataNascimento, Bairro bairro);
    public Usuario cadastroGerente(Usuario quem, String cpf, String nomeCompleto, String email, String senha, CargoUsuario cargoUsuario, String dataNascimento, Bairro bairro);
    public Usuario editar(String cpf, String nomeEditado, String emailEditado, String senhaEditada, Bairro bairro);
    public Usuario buscar(String cpf);
    public List<Usuario> listar();
    public void remover(Usuario quem, String cpf);
    public Usuario buscarEmail(String email);
    public Usuario promoverUsuario(Usuario gerenteLogado, String cpf, CargoUsuario novoCargo);
}
