package backend_ecocode.Util;

import backend_ecocode.Domain.Bairro;
import backend_ecocode.Domain.CargoUsuario;

// valida só o formato dos dados; se CPF e e-mail já existem quem confere é o GerenciarUsuariosService
public class ValidacoesUsuario {

    public void validarUsuario(String nomeCompleto, String email, String cpf, CargoUsuario cargoUsuario, Bairro bairro) {
        validarEmailUsuario(email);
        validarCpfUsuario(cpf);
        validarNomeUsuario(nomeCompleto);
        validarCargoUsuario(cargoUsuario);
        validarBairro(bairro);
    }

    private void validarEmailUsuario(String email){
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório");
        }

        if(!ValidarEmail.ehValido(email)){
            throw new IllegalArgumentException("E-mail inválido");
        }
    }

    private void validarCpfUsuario(String cpf) {
        if (!ValidadorCpf.ehValido(cpf)) {
            throw new IllegalArgumentException("O CPF informado é inválido");
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

    private void validarBairro(Bairro bairro){
        if(bairro == null) {
            throw new IllegalArgumentException("Selecione algum bairro");
        }
    }
}