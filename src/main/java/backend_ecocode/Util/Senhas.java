package backend_ecocode.Util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class Senhas {
    private static final String PREFIXO = "pbkdf2-sha256$600000$";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Senhas() {}

    public static String proteger(String senha) {
        if (senha == null || senha.isBlank()) throw new IllegalArgumentException("Informe uma senha");
        byte[] sal = new byte[16];
        RANDOM.nextBytes(sal);
        Base64.Encoder encoder = Base64.getEncoder();
        return PREFIXO + encoder.encodeToString(sal) + "$" + encoder.encodeToString(derivar(senha, sal));
    }

    public static boolean confere(String senha, String salvo) {
        if (!salvo.startsWith(PREFIXO)) return new CifraAfim().cifraAfim(senha).equals(salvo);
        String[] partes = salvo.substring(PREFIXO.length()).split("\\$", -1);
        if (partes.length != 2) return false;
        Base64.Decoder decoder = Base64.getDecoder();
        return MessageDigest.isEqual(decoder.decode(partes[1]), derivar(senha, decoder.decode(partes[0])));
    }

    private static byte[] derivar(String senha, byte[] sal) {
        PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), sal, 600_000, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException erro) {
            throw new IllegalStateException("Falha ao proteger a senha", erro);
        } finally {
            spec.clearPassword();
        }
    }
}
