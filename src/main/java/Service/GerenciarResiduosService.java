package Service;

import Domain.ServiceResiduosInterface;
import Domain.Residuo;
import Domain.TipoResiduo;
import Repository.GerenciarResiduosRepository;
import Util.GerarCodigoResiduo;

import java.util.List;

public class GerenciarResiduosService implements ServiceResiduosInterface {

    private GerenciarResiduosRepository gerarResiduosRepository = new GerenciarResiduosRepository();
    GerarCodigoResiduo gerarCodigoResiduo = new GerarCodigoResiduo();

    @Override
    public void salvar(TipoResiduo tipoResiduo, int quantidadeResiduo) {

        if (tipoResiduo == null)
            throw new IllegalArgumentException("O tipo do resíduo é obrigatório");

        if (quantidadeResiduo <= 0)
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");

        String codigoResiduo = gerarCodigoResiduo.gerarCodigo(tipoResiduo);

        // loop para gerar um código novo até ele não estar cadastrado (evitar bugs)
        while (gerarResiduosRepository.buscar(codigoResiduo) != null) {
            codigoResiduo = gerarCodigoResiduo.gerarCodigo(tipoResiduo);
        }

        // proteção contra bug no gerador (só aceita números para evitar bugs e caso a adição de código seja manual no futuro)
        if (codigoResiduo == null || !codigoResiduo.matches("\\d{1,9}"))
            throw new IllegalStateException("Erro interno ao gerar o código do resíduo");

        Residuo residuoNovo = new Residuo(
                codigoResiduo,
                tipoResiduo,
                quantidadeResiduo
        );

        gerarResiduosRepository.salvar(residuoNovo);
    }

    @Override
    public void editar(String codigoResiduo) {

    }

    @Override
    public void excluir(String codigoResiduo) {

    }

    @Override
    public void listar(List<Residuo> residuos) {

    }

    @Override
    public Residuo buscar(String codigoResiduo) {
        return null;
    }

}
