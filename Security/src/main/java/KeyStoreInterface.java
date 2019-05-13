import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class KeyStoreInterface {

    public static final String KEYSTORE_PASSWORD = "keyStore";

    public static final String NOTARY_KEYS_PASSWORD = "Notary";
    public static final String CLIENT_KEYS_PASSWORD = "Client";

    public static final String NOTARY_KEY_ALIAS = "NotaryKey";
    public static final String CLIENT_KEY_ALIAS = "ClientKey";

    public static final int RSA_KEY_BYTES = 2048;
    public static final int CERTIFICATE_VALIDITY = 365; //days
    public static final int CLIENT_MAX_NUMBER = 9;

    public static final int NOTARY = 1;
    public static final int CLIENT = 2;

    public static void createBaseKeyStore(boolean usingCC) {
        try {
            // Create the keystore
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, KEYSTORE_PASSWORD.toCharArray());
            FileOutputStream stream = new FileOutputStream(securityBaseDirGenerator() + "\\src\\main\\resources\\KeyStore.jks");
            keyStore.store(stream, KEYSTORE_PASSWORD.toCharArray());

            // Populate the keystore
            addKeyPairToKeyStore(NOTARY, 1, usingCC); //Create key pair for initial server, there will always be at least one
            for(int i=1; i<=CLIENT_MAX_NUMBER; i++) {
                addKeyPairToKeyStore(CLIENT, i, usingCC);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not create base key store");
        }
    }

    public static void addNotaryKeysToKeyStore(int id, boolean usingCC) {
        try {
            if(id > 0) {
                addKeyPairToKeyStore(NOTARY, id, usingCC);
            }
        } catch (Exception e) {
            System.out.println("Could not add notary keys to keystore");
            e.printStackTrace();
        }
    }

    public static Key getPublicKeyFromKeyStore(int userType, int id) {
        String alias;
        if(userType==NOTARY)
            alias = NOTARY_KEY_ALIAS;
        else if(userType==CLIENT)
            alias = CLIENT_KEY_ALIAS;
        else
            return null;
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(writeCertificateToFileCommandGenerator(alias, id));
            process.waitFor();
            if(process.exitValue() != 0) {
                System.out.println("An error occurred while getting public " + alias + id);
            }

            InputStream inputStream = new FileInputStream(securityBaseDirGenerator() + "\\src\\main\\resources\\Temp.cert");
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            return certificate.getPublicKey();
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Could not get public " + alias + id);
            return null;
        }
    }

    public static Key getPrivateKeyFromKeyStore(int userType, int id) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(new FileInputStream(securityBaseDirGenerator() + "\\src\\main\\resources\\KeyStore.jks"), KEYSTORE_PASSWORD.toCharArray());

            if(userType==NOTARY && keyStore.isKeyEntry(NOTARY_KEY_ALIAS+id)) {
                return keyStore.getKey(NOTARY_KEY_ALIAS + id, (NOTARY_KEYS_PASSWORD + id).toCharArray());
            } else if (userType==CLIENT && keyStore.isKeyEntry(CLIENT_KEY_ALIAS+id)) {
                return keyStore.getKey(CLIENT_KEY_ALIAS + id, (CLIENT_KEYS_PASSWORD + id).toCharArray());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            if(userType==NOTARY)
                System.out.println("Could not get private " + NOTARY_KEY_ALIAS + id);
            else if(userType==CLIENT)
                System.out.println("Could not get private " + CLIENT_KEY_ALIAS + id);
            return null;
        }
    }

    private static void addKeyPairToKeyStore(int type, int id, boolean usingCC) {
        try {
            Runtime runtime = Runtime.getRuntime();

            if(type==NOTARY && !usingCC) {
                Process process = runtime.exec(addKeyPairCommandGenerator(NOTARY_KEY_ALIAS, id, NOTARY_KEYS_PASSWORD));
                process.waitFor();
                if(process.exitValue() != 0) {
                    throw new Exception("An error occurred while creating keypair for Notary " + id);
                }
            } else if (type==NOTARY) {
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(new FileInputStream(securityBaseDirGenerator() + "\\src\\main\\resources\\KeyStore.jks"), KEYSTORE_PASSWORD.toCharArray());

                X509Certificate[] certificateChain = new X509Certificate[1];
                CartaoCidadao.setUp();
                certificateChain[0] = CartaoCidadao.getCertFromByteArray(CartaoCidadao.getCertificateInBytes());
                CartaoCidadao.exitPteid();
                keyStore.setCertificateEntry(NOTARY_KEY_ALIAS + id, certificateChain[0]);

                FileOutputStream stream = new FileOutputStream(securityBaseDirGenerator() + "\\src\\main\\resources\\KeyStore.jks");
                keyStore.store(stream, KEYSTORE_PASSWORD.toCharArray());
            } else if (type==CLIENT) {
                Process process = runtime.exec(addKeyPairCommandGenerator(CLIENT_KEY_ALIAS, id, CLIENT_KEYS_PASSWORD));
                process.waitFor();
                if(process.exitValue() != 0) {
                    throw new Exception("An error occurred while creating keypair for Client " + id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not add key pair to keystore");
        }
    }

    private static String securityBaseDirGenerator() {
        String basePath = System.getProperty("user.dir");
        if(!basePath.contains("\\Security"))
            basePath+="\\Security";
        return basePath;
    }

    private static String addKeyPairCommandGenerator(String keyAlias, int id, String keyPassword) {
        String command = "keytool -genkeypair " +
                "-alias " + keyAlias + id + " " +
                "-keyalg RSA " +
                "-keysize " + RSA_KEY_BYTES + " " +
                "-dname \"CN=Group26, OU=SEC, O=IST, L=Taguspark, ST=Lisboa, C=PT\" " +
                "-keypass " + keyPassword + id + " " +
                "-validity " + CERTIFICATE_VALIDITY + " " +
                "-storetype JKS " +
                "-keystore " + securityBaseDirGenerator() + "\\src\\main\\resources\\KeyStore.jks" + " " +
                "-storepass " + KEYSTORE_PASSWORD;
        return command;
    }

    private static String writeCertificateToFileCommandGenerator(String keyAlias, int id) {
        String command = "keytool -export -keystore " + securityBaseDirGenerator() + "\\src\\main\\resources\\KeyStore.jks" + " " +
                "-alias " + keyAlias + id + " " + "-file " + securityBaseDirGenerator() + "\\src\\main\\resources\\Temp.cert" + " " +
                "-storepass " + KEYSTORE_PASSWORD;
        return command;
    }
}
