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
    public Residuo salvar(TipoResiduo tipoResiduo, int quantidadeResiduo, String descricao) {

        // verifica se foi informado um tipo de residuo
        if (tipoResiduo == null)
            throw new IllegalArgumentException(
                    "O tipo do resíduo é obrigatório"
            );

        // verifica se a quantidade é maior que 0
        if (quantidadeResiduo <= 0)
            throw new IllegalArgumentException("" +
                    "A quantidade deve ser maior que zero"
            );

        String codigoResiduo = gerarCodigoResiduo.gerarCodigo(tipoResiduo);

        // gera um novo código para o residuo cadastrado caso o código gerado já esteja cadastrado
        while (gerarResiduosRepository.buscar(codigoResiduo) != null) {
            codigoResiduo = gerarCodigoResiduo.gerarCodigo(tipoResiduo);
        }

        Residuo residuoNovo = new Residuo(codigoResiduo, tipoResiduo, quantidadeResiduo, descricao);

        gerarResiduosRepository.salvar(residuoNovo);
        return residuoNovo;
    }

    @Override
    public Residuo editar(String codigoResiduo, int quantidadeResiduo, String descricao) {
        Residuo editarResiduo = buscar(codigoResiduo);

        if(editarResiduo == null){
            throw new IllegalArgumentException(
                    "O Resíduo Buscado Não Foi Encontrado!"
            );
        }

        if(quantidadeResiduo <= 0){
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero"
            );
        }

        editarResiduo.setQuantidade(quantidadeResiduo);
        editarResiduo.setDescricao(descricao);

        gerarResiduosRepository.editar(editarResiduo);
        return editarResiduo;
    }

    @Override
    public void excluir(String codigoResiduo) {

    }

    @Override
    public List<Residuo> listar() {
        return gerarResiduosRepository.listar();
    }

    @Override
    public Residuo buscar(String codigoResiduo) {
        return gerarResiduosRepository.buscar(codigoResiduo);
    }

}
