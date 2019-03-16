import java.io.FileWriter;
import java.io.Writer;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class RSAKeySaverAsText {

    public static void SavePublicKeyAsText(PublicKey key, String fileName) {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            Writer out = new FileWriter(fileName + ".pub");
            out.write("-----BEGIN RSA PUBLIC KEY-----\n");
            out.write(encoder.encodeToString(key.getEncoded()));
            out.write("\n-----END RSA PUBLIC KEY-----\n");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SavePrivateKeyAsText(PrivateKey key, String fileName) {
        try {
            Base64.Encoder encoder = Base64.getEncoder();
            Writer out = new FileWriter(fileName + ".key");
            out.write("-----BEGIN RSA PRIVATE KEY-----\n");
            out.write(encoder.encodeToString(key.getEncoded()));
            out.write("\n-----END RSA PRIVATE KEY-----\n");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
