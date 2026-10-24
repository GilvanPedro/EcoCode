import Domain.CargoUsuario;
import Domain.Usuario;
import Repository.GerenciarUsuariosRepository;
import Service.GerenciarResiduosService;
import Service.GerenciarUsuariosService;
import Util.CifraCesar;

import java.util.List;

public class Teste {

    static GerenciarUsuariosService gerenciarUsuariosService = new GerenciarUsuariosService();
    static CifraCesar cifraCesar = new CifraCesar();

    static void main() {
        String senha = "Rogério Carlos Pedro V3";

        System.out.println(cifraCesar.cifraCesar(senha));

        String senhaCriptografada = cifraCesar.cifraCesar(senha);

        System.out.println(cifraCesar.descriptografarCifra(senhaCriptografada));

        List<String> tentativas = cifraCesar.descriptografarTodasChaves(senhaCriptografada);

        for (int chave = 0; chave < tentativas.size(); chave++) {
            System.out.println("Chave " + chave + ": " + tentativas.get(chave));
        }

        Usuario usuario = new Usuario("Carlos", "06531492120", "gilvanpedro2006@gmail.com", senhaCriptografada, CargoUsuario.GERENTE, "31/07/2006");

        Usuario novo = gerenciarUsuariosService.cadastrar(
                usuario.getCargoUsuario(),
                "529.982.247-25",           // CPF válido de teste
                "Rafael Souza Lima",
                "rafael.lima@empresa.com",
                "Rafa@2026",
                CargoUsuario.FUNCIONARIO,
                "22/07/2001"
        );

        System.out.println(gerenciarUsuariosService.buscar("529.982.247-25").getNomeCompleto());
    }
}
