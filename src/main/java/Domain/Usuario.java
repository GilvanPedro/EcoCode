package Domain;

import Util.ValidadorCpf;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Usuario {
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String senhaCriptografada;
    private CargoUsuario cargoUsuario;
    private LocalDate dataNascimento;

    // Formatador estático para o padrão brasileiro DD/MM/AAAA
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Usuario(String nomeCompleto, String cpf, String email, String senhaCriptografada, CargoUsuario cargoUsuario, String dataNascimento) {
        // Validações de Obrigatoriedade (Fail-Fast)
        if (cargoUsuario == null) {
            throw new IllegalArgumentException("O cargo do usuário é obrigatório");
        }
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            throw new IllegalArgumentException("O nome completo é obrigatório");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório");
        }

        // Validação de Regra de Negócio (CPF)
        if (!ValidadorCpf.ehValido(cpf)) {
            throw new IllegalArgumentException("O CPF informado é inválido");
        }

        // Validação e Atribuição da Data de Nascimento
        try {
            this.dataNascimento = LocalDate.parse(dataNascimento, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de data inválido! Use o padrão DD/MM/AAAA.");
        }

        // Atribuição dos demais campos
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.senhaCriptografada = senhaCriptografada;
        this.cargoUsuario = cargoUsuario;
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

    public LocalDate getDataNascimento() {
        return dataNascimento;
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
}
