package backend_ecocode.Domain.Interfaces;

import backend_ecocode.Domain.Entities.Usuario;

import java.util.List;

public interface RepositoryUsuarioInterface {
    public void cadastrar(Usuario usuarioCadastrado);
    public void editar(Usuario usuarioEditado);
    public Usuario buscar(String cpf);
    public List<Usuario> listar();
    public void remover(String cpf);
    public Usuario buscarEmail(String email);
}
