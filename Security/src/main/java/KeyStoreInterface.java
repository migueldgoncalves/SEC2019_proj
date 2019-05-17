import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
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

    private static final int DELETE_KEYSTORE = 0;
    private static final int ADD_KEYPAIR = 1;
    private static final int GET_PRIVATE_KEY = 2;
    private static final int GET_PUBLIC_KEY = 3;
    private static final int CREATE_BASE_KEYSTORE = 4;

    private static boolean isKeyStoreCreated = false;

    public static void addNotaryKeysToKeyStore(int id, boolean usingCC) {
        try {
            if(id > 0) {
                addKeyPairToKeyStore(NOTARY, id, usingCC);
                System.out.println("Keypair for Notary " + id + " was added");
            }
        } catch (Exception e) {
            System.out.println("Could not add notary keys to keystore");
            e.printStackTrace();
        }
    }

    public static void createBaseKeyStore() {
        // Only needs operation type
        keyStoreAccesser(KeyStoreInterface.CREATE_BASE_KEYSTORE, -123456789, -123456789, false);
    }

    public static Key getPublicKeyFromKeyStore(int userType, int id) {
        // Needs operation type, user type and id
        return keyStoreAccesser(KeyStoreInterface.GET_PUBLIC_KEY, userType, id, false);
    }

    public static Key getPrivateKeyFromKeyStore(int userType, int id) {
        // Needs operation type, user type and id
        return keyStoreAccesser(KeyStoreInterface.GET_PRIVATE_KEY, userType, id, false);
    }

    private static void addKeyPairToKeyStore(int type, int id, boolean usingCC) {
        // Operation type, user type, id and using_CC parameters are needed
        keyStoreAccesser(KeyStoreInterface.ADD_KEYPAIR, type, id, usingCC);
    }

    protected static void deleteKeystore() {
        // Only operation type is needed
        keyStoreAccesser(KeyStoreInterface.DELETE_KEYSTORE, -123456789, -123456789, false);
    }

    private static synchronized Key keyStoreAccesser(int operationType, int type, int id, boolean usingCC) {
        Runtime runtime = Runtime.getRuntime();
        if(operationType==DELETE_KEYSTORE) {
            try {
                File keyStore = new File(securityBaseDirGenerator() + "\\src\\main\\resources\\KeyStore.jks");
                if(keyStore.exists()) {
                    if (keyStore.delete()) {
                        System.out.println("Keystore successfully deleted");
                    } else {
                        System.out.println("Error while deleting keystore");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isKeyStoreCreated = false;
            return null;
        } else if (operationType==ADD_KEYPAIR) {
            try {
                if(type==NOTARY && !usingCC) {
                    Process process = runtime.exec(addKeyPairCommandGenerator(NOTARY_KEY_ALIAS, id, NOTARY_KEYS_PASSWORD));
                    process.waitFor();
                    if(process.exitValue() != 0) {
                        System.out.println("Process exit value: " + process.exitValue());
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
                        System.out.println("Process exit value: " + process.exitValue());
                        throw new Exception("An error occurred while creating keypair for Client " + id);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not add key pair to keystore");
            }
            return null;
        } else if (operationType==GET_PRIVATE_KEY) {
            try {
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(new FileInputStream(securityBaseDirGenerator() + "\\src\\main\\resources\\KeyStore.jks"), KEYSTORE_PASSWORD.toCharArray());

                if(type==NOTARY && keyStore.isKeyEntry(NOTARY_KEY_ALIAS+id)) {
                    return keyStore.getKey(NOTARY_KEY_ALIAS + id, (NOTARY_KEYS_PASSWORD + id).toCharArray());
                } else if (type==CLIENT && keyStore.isKeyEntry(CLIENT_KEY_ALIAS+id)) {
                    return keyStore.getKey(CLIENT_KEY_ALIAS + id, (CLIENT_KEYS_PASSWORD + id).toCharArray());
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                if(type==NOTARY)
                    System.out.println("Could not get private " + NOTARY_KEY_ALIAS + id);
                else if(type==CLIENT)
                    System.out.println("Could not get private " + CLIENT_KEY_ALIAS + id);
                return null;
            }
        } else if (operationType==GET_PUBLIC_KEY) {
            String alias;
            if(type==NOTARY)
                alias = NOTARY_KEY_ALIAS;
            else if(type==CLIENT)
                alias = CLIENT_KEY_ALIAS;
            else
                return null;
            try {
                Process process = runtime.exec(writeCertificateToFileCommandGenerator(alias, id));
                process.waitFor();
                if(process.exitValue() != 0) {
                    System.out.println("Process exit value: " + process.exitValue());
                    System.out.println("An error occurred while getting public " + alias + id);
                }

                InputStream inputStream = new FileInputStream(securityBaseDirGenerator() + "\\src\\main\\resources\\Temp.cert");
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                Certificate certificate = certificateFactory.generateCertificate(inputStream);
                return certificate.getPublicKey();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not get public " + alias + id);
                return null;
            }
        } else if (operationType==CREATE_BASE_KEYSTORE) {
            try {
                if (!isKeyStoreCreated) { //Server needs to have a keystore created before even knowing if it is first or not
                    File baseKeyStore = new File(securityBaseDirGenerator() + "\\src\\main\\resources\\BaseKeyStore.jks");
                    if(!baseKeyStore.exists()) {
                        // Create the keystore
                        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        keyStore.load(null, KEYSTORE_PASSWORD.toCharArray());
                        FileOutputStream stream = new FileOutputStream(securityBaseDirGenerator() + "\\src\\main\\resources\\BaseKeyStore.jks");
                        keyStore.store(stream, KEYSTORE_PASSWORD.toCharArray());

                        // Populate the keystore with the clients
                        for (int i = 1; i <= CLIENT_MAX_NUMBER; i++) {
                            addKeyPairToKeyStore(CLIENT, i, false); //"usingCC" parameter is only relevant for notaries
                            System.out.println("Keypair for Client " + i + " was added");
                        }
                    }
                    File finalKeyStore = new File(securityBaseDirGenerator() + "\\src\\main\\resources\\KeyStore.jks");
                    FileUtils.copyFile(baseKeyStore, finalKeyStore);
                    isKeyStoreCreated = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not create base key store");
            }
            return null;
        }
        return null;
    }

    private static String writeCertificateToFileCommandGenerator(String keyAlias, int id) {
        return "keytool -export -keystore " + securityBaseDirGenerator() + "\\src\\main\\resources\\KeyStore.jks" + " " +
                "-alias " + keyAlias + id + " " + "-file " + securityBaseDirGenerator() + "\\src\\main\\resources\\Temp.cert" + " " +
                "-storepass " + KEYSTORE_PASSWORD;
    }

    private static String addKeyPairCommandGenerator(String keyAlias, int id, String keyPassword) {
        String keyStoreFile = null;
        if(keyAlias.equals(NOTARY_KEY_ALIAS))
            keyStoreFile = "KeyStore";
        else if (keyAlias.equals(CLIENT_KEY_ALIAS))
            keyStoreFile = "BaseKeyStore";

        return "keytool -genkeypair " +
                "-alias " + keyAlias + id + " " +
                "-keyalg RSA " +
                "-keysize " + RSA_KEY_BYTES + " " +
                "-dname \"CN=Group26, OU=SEC, O=IST, L=Taguspark, ST=Lisboa, C=PT\" " +
                "-keypass " + keyPassword + id + " " +
                "-validity " + CERTIFICATE_VALIDITY + " " +
                "-storetype JKS " +
                "-keystore " + securityBaseDirGenerator() + "\\src\\main\\resources\\" + keyStoreFile + ".jks" + " " +
                "-storepass " + KEYSTORE_PASSWORD;
    }

    private static String securityBaseDirGenerator() {
        String keyStorePath = System.getProperty("user.dir");
        keyStorePath = keyStorePath.replace("\\Client", "\\Security");
        keyStorePath = keyStorePath.replace("\\Notary", "\\Security");
        if (!keyStorePath.contains("\\Security"))
            keyStorePath += "\\Security";
        return keyStorePath;
    }
}
