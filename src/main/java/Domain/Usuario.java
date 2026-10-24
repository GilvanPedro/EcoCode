package Domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Usuario {
    private String nomeCompleto;
    private String email;
    private String senhaCriptografada;
    private CargoUsuario cargoUsuario;
    private LocalDate dataNascimento;

    // Formatador estático para o padrão brasileiro DD/MM/AAAA
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Usuario(String nomeCompleto, String email, String senhaCriptografada, CargoUsuario cargoUsuario, String dataNascimento) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senhaCriptografada = senhaCriptografada;
        this.cargoUsuario = cargoUsuario;

        //adicionar a data de nascimento no formato certo
        try {
            // Tenta converter a String "25/12/2000" em um objeto LocalDate
            this.dataNascimento = LocalDate.parse(dataNascimento, FORMATTER);
        } catch (DateTimeParseException e) {
            // Lança uma exceção se a String não estiver no formato correto ou for uma data inválida (ex: 31/02)
            throw new IllegalArgumentException("Formato de data inválido! Use o padrão DD/MM/AAAA.");
        }
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
