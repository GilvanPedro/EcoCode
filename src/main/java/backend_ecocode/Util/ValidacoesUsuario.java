package backend_ecocode.Util;

import backend_ecocode.Domain.CargoUsuario;
import backend_ecocode.Service.GerenciarUsuariosService;

public class ValidacoesUsuario {
    private GerenciarUsuariosService service = new GerenciarUsuariosService();

    public void validarUsuario(String nomeCompleto, String email, String cpf, CargoUsuario cargoUsuario){

        validarEmailUsuario(email);
        validarCpfUsuario(cpf);
        validarNomeUsuario(nomeCompleto);
        validarCargoUsuario(cargoUsuario);
    }

    private void validarEmailUsuario(String email){

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório");
        }

        if(!ValidarEmail.ehValido(email)){
            throw new IllegalArgumentException("E-mail inválido");
        }

        if(ValidarEmail.emailExiste(email)){
            throw new IllegalArgumentException("Email já cadastrado para um usuário");
        }
    }

    private void validarCpfUsuario(String cpf) {
        if (!ValidadorCpf.ehValido(cpf)) {
            throw new IllegalArgumentException("O CPF informado é inválido");
        }

        if(service.buscar(cpf) != null){
            throw new IllegalArgumentException("CPF já está cadastrado para outro usuário");
        }
    }

    private void validarNomeUsuario(String nomeCompleto){

        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            throw new IllegalArgumentException("O nome completo é obrigatório");
        }
    }

    private void validarCargoUsuario(CargoUsuario cargoUsuario){

        if (cargoUsuario == null) {
            throw new IllegalArgumentException("O cargo do usuário é obrigatório");
        }
    }
}
