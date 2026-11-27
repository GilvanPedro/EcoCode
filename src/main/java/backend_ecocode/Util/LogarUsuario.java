package backend_ecocode.Util;

import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Service.GerenciarUsuariosService;

public class LogarUsuario {

    private final GerenciarUsuariosService service;

    public LogarUsuario(GerenciarUsuariosService service) {
        this.service = service;
    }

    public Usuario logarUsuario(String email, String senha) {
        if (email == null || !ValidarEmail.ehValido(email))
            throw new IllegalArgumentException("Insira um email válido");

        if (senha == null || senha.isBlank())
            throw new IllegalArgumentException("Insira uma senha válida");

        Usuario usuario = service.buscarEmail(email);   // lança exceção se não achar

        if (!Senhas.confere(senha, usuario.getSenhaCriptografada()))
            throw new IllegalArgumentException("Senha incorreta");

        return usuario;
    }

}
