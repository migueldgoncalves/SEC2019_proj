import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

public class CipherMessage {

    public CipherMessage() {

    }

    public byte[] Cipher(PublicKey key, String message) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] input = message.getBytes();
            cipher.update(input);

            byte[] cipherText = cipher.doFinal();
            System.out.println(new String(cipherText, StandardCharsets.UTF_8));

            return cipherText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
