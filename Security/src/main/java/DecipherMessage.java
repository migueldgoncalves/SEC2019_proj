import javax.crypto.Cipher;
import java.security.PrivateKey;

public class DecipherMessage {

    public DecipherMessage() {

    }

    public String Decipher(PrivateKey key, byte[] cipheredText) {
        try {
            Cipher decipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            decipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decipheredText = decipher.doFinal(cipheredText);

            System.out.println(new String(decipheredText));

            return new String(decipheredText);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
