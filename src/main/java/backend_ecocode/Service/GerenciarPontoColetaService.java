package backend_ecocode.Service;

import backend_ecocode.Domain.*;
import backend_ecocode.Exceptions.AcessoNegadoException;
import backend_ecocode.Repository.GerenciarPontoColetaRepository;

import java.util.List;
import java.util.UUID;

public class GerenciarPontoColetaService implements ServicePontoColetaInterface {

    private final GerenciarPontoColetaRepository repository;

    public GerenciarPontoColetaService(GerenciarPontoColetaRepository repository) {
        this.repository = repository;
    }

    private void exigirGerente(Usuario quem) {
        if (quem == null)
            throw new IllegalArgumentException("Usuário não informado");
        if (quem.getCargoUsuario() != CargoUsuario.GERENTE)
            throw new AcessoNegadoException("Só o gerente pode gerenciar pontos de coleta");
    }

    @Override
    public PontoColeta cadastrar(Usuario quem, String nome, String endereco, Bairro bairro) {
        exigirGerente(quem);
        PontoColeta ponto = new PontoColeta(nome, endereco, bairro);
        repository.salvar(ponto);
        return ponto;
    }

    @Override
    public PontoColeta editar(Usuario quem, UUID id, String nome, String endereco, Bairro bairro) {
        exigirGerente(quem);
        PontoColeta ponto = repository.buscarPorId(id);

        // o que não veio, continua como está
        String novoNome = (nome == null || nome.isBlank()) ? ponto.getNome() : nome;
        String novoEndereco = (endereco == null || endereco.isBlank()) ? ponto.getEndereco() : endereco;
        Bairro novoBairro = (bairro == null) ? ponto.getBairro() : bairro;

        ponto.setNome(novoNome);
        ponto.setEndereco(novoEndereco);
        ponto.setBairro(novoBairro);

        repository.atualizar(ponto);
        return ponto;
    }

    @Override
    public void excluir(Usuario quem, UUID id) {
        exigirGerente(quem);
        repository.remover(id);
    }

    @Override
    public PontoColeta buscarPorId(UUID id) {
        if (id == null)
            throw new IllegalArgumentException("O id do ponto é obrigatório");
        return repository.buscarPorId(id);
    }

    @Override
    public List<PontoColeta> listar() {
        return repository.listar();
    }
}