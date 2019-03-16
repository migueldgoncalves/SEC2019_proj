import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class RSAKeyGenerator {

    public RSAKeyGenerator() {

    }

    public static void main(String[] args) {
        RSAStoreInFile(11);
    }

    public static void RSAStoreInFile(int NumberOfKeysToGenerate) {
        try {
            for (int i = 0; i < NumberOfKeysToGenerate; i++) {

                KeyPair pair = generateRSAKeyPair();

                FileOutputStream out = new FileOutputStream("User" + i + ".key");
                out.write(pair.getPrivate().getEncoded());
                out.close();

                out = new FileOutputStream("User" + i + ".pub");
                out.write(pair.getPublic().getEncoded());
                out.close();

                System.out.println("Private Key Format: " + pair.getPrivate().getFormat());

                System.out.println("Public Key Format: " + pair.getPublic().getFormat());
            }

            System.out.println("Finished Key Generation!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();

            return kp;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
