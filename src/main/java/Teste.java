import Util.CifraCesar;

import java.util.List;

public class Teste {
    static void main() {
        CifraCesar cifraCesar = new CifraCesar();
        String senha = "Rogério Carlos Pedro V3";

        System.out.println(cifraCesar.cifraCesar(senha));

        String senhaCriptografada = cifraCesar.cifraCesar(senha);

        System.out.println(cifraCesar.descriptografarCifra(senhaCriptografada));

        List<String> tentativas = cifraCesar.descriptografarTodasChaves(senhaCriptografada);

        for (int chave = 0; chave < tentativas.size(); chave++) {
            System.out.println("Chave " + chave + ": " + tentativas.get(chave));
        }
    }
}
