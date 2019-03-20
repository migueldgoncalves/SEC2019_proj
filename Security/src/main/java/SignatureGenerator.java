import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class SignatureGenerator {

    public static byte[] generateSignature(PrivateKey key, String message) {
        try {
            Signature sign = Signature.getInstance("SHA512withRSA");

            sign.initSign(key);
            byte[] bytes = message.getBytes();

            sign.update(bytes);

            byte[] signature = sign.sign();

            System.out.println("Digital Signature for given Message: " + new String(signature, StandardCharsets.UTF_8));

            return signature;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifySignature(PublicKey key, byte[] data, String messageToVerify) {
        try {
            Signature sign = Signature.getInstance("SHA512withRSA");
            sign.initVerify(key);
            sign.update(messageToVerify.getBytes());

            boolean valid = sign.verify(data);

            if (valid) {
                System.out.println("Signature verified");
            } else {
                System.out.println("Signature failed");
            }
            return valid;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
