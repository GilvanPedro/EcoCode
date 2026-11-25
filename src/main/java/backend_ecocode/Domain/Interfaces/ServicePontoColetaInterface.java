package backend_ecocode.Domain.Interfaces;

import backend_ecocode.Domain.Entities.PontoColeta;
import backend_ecocode.Domain.Entities.Usuario;
import backend_ecocode.Domain.Enums.Bairro;

import java.util.List;
import java.util.UUID;

public interface ServicePontoColetaInterface {
    public PontoColeta cadastrar(Usuario quem, String nome, String endereco, Bairro bairro);
    public PontoColeta editar(Usuario quem, UUID id, String nome, String endereco, Bairro bairro);
    public void excluir(Usuario quem, UUID id);
    public PontoColeta buscarPorId(UUID id);
    public List<PontoColeta> listar();
    public PontoColeta pontoMaisProximo(Usuario usuario);
}