import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class RSAKeyGenerator {

    public RSAKeyGenerator() {

    }

    public static void main(String[] args) {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.generateKeyPair();
            Key pub = kp.getPublic();
            Key pvt = kp.getPrivate();

            System.out.println("Please Insert Number of Keys To Generate:");
            System.out.print("Number of Keys to Generate: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String limit = reader.readLine();
            int max = Integer.parseInt(limit);

            for (int i = 0; i < max; i++) {
                FileOutputStream out = new FileOutputStream("User" + i + ".key");
                out.write(pvt.getEncoded());
                out.close();

                out = new FileOutputStream("User" + i + ".pub");
                out.write(pub.getEncoded());
                out.close();

                System.out.println("Private Key Format: " + pvt.getFormat());

                System.out.println("Public Key Format: " + pub.getFormat());
            }

            System.out.println("Finished Key Generation!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
