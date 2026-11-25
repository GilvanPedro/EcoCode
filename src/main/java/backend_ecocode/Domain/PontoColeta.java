    package backend_ecocode.Domain;

    import java.util.UUID;

    public class PontoColeta {
        private final UUID id;
        private String nome;
        private String endereco;
        private Bairro bairro;

        public PontoColeta(UUID id, String nome, String endereco, Bairro bairro) {
            validar(nome, endereco, bairro);
            this.id = id;
            this.nome = nome;
            this.endereco = endereco;
            this.bairro = bairro;
        }

        public PontoColeta(String nome, String endereco, Bairro bairro) {
            validar(nome, endereco, bairro);
            this.id = UUID.randomUUID();
            this.nome = nome.trim();
            this.endereco = endereco.trim();
            this.bairro = bairro;
        }

        public UUID getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public String getEndereco() {
            return endereco;
        }

        public Bairro getBairro() {
            return bairro;
        }

        public void setNome(String nome) {
            validarNome(nome);
            this.nome = nome.trim();
        }

        public void setEndereco(String endereco) {
            validarEndereco(endereco);
            this.endereco = endereco.trim();
        }

        public void setBairro(Bairro bairro) {
            validarBairro(bairro);
            this.bairro = bairro;
        }

        private static void validar(String nome, String endereco, Bairro bairro) {
            validarNome(nome);
            validarEndereco(endereco);
            validarBairro(bairro);
        }

        private static void validarNome(String nome) {
            if (nome == null || nome.isBlank())
                throw new IllegalArgumentException("O nome do ponto não pode estar vazio");
        }

        private static void validarEndereco(String endereco) {
            if (endereco == null || endereco.isBlank())
                throw new IllegalArgumentException("O endereço não pode estar vazio");
        }

        private static void validarBairro(Bairro bairro) {
            if (bairro == null)
                throw new IllegalArgumentException("Você precisa selecionar um bairro");
        }
    }
