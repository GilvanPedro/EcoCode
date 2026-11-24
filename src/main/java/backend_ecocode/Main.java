package backend_ecocode;

import java.util.Scanner;

import backend_ecocode.Domain.CargoUsuario;
import backend_ecocode.Domain.Usuario;
import backend_ecocode.Service.GerenciarResiduosService;
import backend_ecocode.Service.GerenciarUsuariosService;
import backend_ecocode.TelaTerminal.TelaCadastroResiduoTerminal;
import backend_ecocode.TelaTerminal.TelaUsuarioTerminal;

public class Main {

    public static void main(String[] args) {

        Instanciador instanciador = new Instanciador();
        instanciador.getTelaUsuario().telaPrincipalUsuario();
    }
}