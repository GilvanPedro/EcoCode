package backend_ecocode.Domain.Entities;

import backend_ecocode.Domain.Enums.Bairro;
import backend_ecocode.Domain.Enums.CargoUsuario;
import backend_ecocode.Util.ValidacoesUsuario;
import backend_ecocode.Util.ValidadorCpf;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public class Usuario {
    private final UUID id;
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String senhaCriptografada;
    private CargoUsuario cargoUsuario;
    private LocalDate dataNascimento;
    private Bairro bairro;

    // Formatador estático para o padrão brasileiro DD/MM/AAAA
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final ValidacoesUsuario validar = new ValidacoesUsuario();

    public Usuario(String nomeCompleto, String cpf, String email, String senhaCriptografada, CargoUsuario cargoUsuario, String dataNascimento, Bairro bairro) {
        final String cpfLimpo = ValidadorCpf.limpar(cpf);

        // Validações de Obrigatoriedade (Fail-Fast)
        validar.validarUsuario(nomeCompleto, email, cpfLimpo, cargoUsuario, bairro);

        // Validação e Atribuição da Data de Nascimento
        try {
            this.dataNascimento = LocalDate.parse(dataNascimento, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido! Use o padrão DD/MM/AAAA.");
        }

        // Atribuição dos demais campos
        this.id = UUID.randomUUID();
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpfLimpo;
        this.email = email;
        this.senhaCriptografada = senhaCriptografada;
        this.cargoUsuario = cargoUsuario;
        this.bairro = bairro;
    }

    // construtor de carregamento do banco de dados
    public Usuario(UUID id, String nomeCompleto, String cpf, String email, String senhaCriptografada, CargoUsuario cargoUsuario, String dataNascimento, Bairro bairro) {
        final String cpfLimpo = ValidadorCpf.limpar(cpf);

        // Validações de Obrigatoriedade (Fail-Fast)
        validar.validarUsuario(nomeCompleto, email, cpfLimpo, cargoUsuario, bairro);

        // Validação e Atribuição da Data de Nascimento
        try {
            this.dataNascimento = LocalDate.parse(dataNascimento, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido! Use o padrão DD/MM/AAAA.");
        }

        // Atribuição dos demais campos
        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpfLimpo;
        this.email = email;
        this.senhaCriptografada = senhaCriptografada;
        this.cargoUsuario = cargoUsuario;
        this.bairro = bairro;
    }

    public UUID getId() {
        return id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaCriptografada() {
        return senhaCriptografada;
    }

    public CargoUsuario getCargoUsuario() {
        return cargoUsuario;
    }

    public String getDataNascimento() {
        return dataNascimento.format(FORMATTER);   // "14/05/2006"
    }

    public String getCpf() {
        return cpf;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenhaCriptografada(String senhaCriptografada) {
        this.senhaCriptografada = senhaCriptografada;
    }

    public void setCargoUsuario(CargoUsuario cargoUsuario) {
        this.cargoUsuario = cargoUsuario;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        if(bairro == null) {
            throw new IllegalArgumentException("Selecione algum bairro");
        }
        this.bairro = bairro;
    }
}
