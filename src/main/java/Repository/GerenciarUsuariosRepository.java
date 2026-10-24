package Repository;

import Domain.RepositoryUsuarioInterface;
import Domain.Usuario;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GerenciarUsuariosRepository implements RepositoryUsuarioInterface {

    private Map<String, Usuario> usuarios = new TreeMap<>();

    @Override
    public void cadastrar(Usuario usuarioCadastrado) {
        usuarios.put(usuarioCadastrado.getCpf(), usuarioCadastrado);
    }

    @Override
    public void editar(Usuario usuarioEditado) {
        usuarios.put(usuarioEditado.getCpf(), usuarioEditado);
    }

    @Override
    public Usuario buscar(String cpf) {
        return usuarios.get(cpf);
    }

    @Override
    public List<Usuario> listar() {
        return List.of();
    }

    @Override
    public void remover(String cpf) {
        usuarios.remove(cpf);
    }
}
