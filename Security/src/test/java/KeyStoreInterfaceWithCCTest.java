import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyStoreInterfaceWithCCTest {

    @BeforeClass
    public static void setUp() {
        try {
            String keyStorePath = System.getProperty("user.dir");
            if (!keyStorePath.contains("\\Security"))
                keyStorePath += "\\Security";

            File keyStore = new File(keyStorePath + "\\src\\main\\resources\\KeyStore.jks");
            keyStore.delete();

            // In most of these tests the keystore is not changed, therefore it does not need to be created before each test
            KeyStoreInterface.createBaseKeyStore(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getKeysFromKeyStoreSuccessfulTest() {
        try {
            PublicKey publicKey;
            PrivateKey privateKey;

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 1);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 1);
            Assert.assertNotNull(publicKey);
            Assert.assertNull(privateKey);

            CartaoCidadao.setUp();
            byte[] signature = CartaoCidadao.sign("message");
            CartaoCidadao.exitPteid();
            Assert.assertTrue(SignatureGenerator.verifySignatureCartaoCidadao(publicKey, signature, "message"));
            Assert.assertFalse(SignatureGenerator.verifySignatureCartaoCidadao(publicKey, signature, "otherMessage"));

            for (int i = 1; i <= KeyStoreInterface.CLIENT_MAX_NUMBER; i++) {
                publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.CLIENT, i);
                privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, i);
                Assert.assertNotNull(publicKey);
                Assert.assertNotNull(privateKey);
                signature = SignatureGenerator.generateSignature(privateKey, "message");
                Assert.assertTrue(SignatureGenerator.verifySignature(publicKey, signature, "message"));
                Assert.assertFalse(SignatureGenerator.verifySignature(publicKey, signature, "otherMessage"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void getKeysFromKeyStoreInvalidUserType() {
        try {
            PublicKey publicKey;
            PrivateKey privateKey;

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(-123456789, 1);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(-123456789, 1);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void getKeysFromKeyStoreInvalidId() {
        try {
            PublicKey publicKey;
            PrivateKey privateKey;

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, -1);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, -1);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 0);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 0);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 2);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 2);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.CLIENT, -1);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, -1);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.CLIENT, 0);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, 0);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.CLIENT, KeyStoreInterface.CLIENT_MAX_NUMBER + 1);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, KeyStoreInterface.CLIENT_MAX_NUMBER + 1);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void addNotaryKeysToKeystoreSuccessful() {
        try {
            PublicKey publicKey;
            PrivateKey privateKey;

            KeyStoreInterface.addNotaryKeysToKeyStore(2, true);

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, -1);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, -1);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 0);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 0);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            for (int i = 1; i <= 2; i++) {
                publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, i);
                privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, i);
                Assert.assertNotNull(publicKey);
                Assert.assertNull(privateKey);

                CartaoCidadao.setUp();
                byte[] signature = CartaoCidadao.sign("message");
                CartaoCidadao.exitPteid();
                Assert.assertTrue(SignatureGenerator.verifySignatureCartaoCidadao(publicKey, signature, "message"));
                Assert.assertFalse(SignatureGenerator.verifySignatureCartaoCidadao(publicKey, signature, "otherMessage"));
            }

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 3);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 3);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            // Reset keystore to base state

            String keyStorePath = System.getProperty("user.dir");
            if (!keyStorePath.contains("\\Security"))
                keyStorePath += "\\Security";

            File keyStore = new File(keyStorePath + "\\src\\main\\resources\\KeyStore.jks");
            keyStore.delete();

            KeyStoreInterface.createBaseKeyStore(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void addNotaryKeysToKeystoreInvalidId() {
        try {
            PublicKey publicKey;
            PrivateKey privateKey;

            KeyStoreInterface.addNotaryKeysToKeyStore(-1, true);
            KeyStoreInterface.addNotaryKeysToKeyStore(0, true);
            KeyStoreInterface.addNotaryKeysToKeyStore(1, true);

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, -1);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, -1);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 0);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 0);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 1);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 1);
            Assert.assertNotNull(publicKey);
            Assert.assertNull(privateKey);

            CartaoCidadao.setUp();
            byte[] signature = CartaoCidadao.sign("message");
            CartaoCidadao.exitPteid();
            Assert.assertTrue(SignatureGenerator.verifySignatureCartaoCidadao(publicKey, signature, "message"));
            Assert.assertFalse(SignatureGenerator.verifySignatureCartaoCidadao(publicKey, signature, "otherMessage"));

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 2);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 2);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            // Reset keystore to base state

            String keyStorePath = System.getProperty("user.dir");
            if (!keyStorePath.contains("\\Security"))
                keyStorePath += "\\Security";

            File keyStore = new File(keyStorePath + "\\src\\main\\resources\\KeyStore.jks");
            keyStore.delete();

            KeyStoreInterface.createBaseKeyStore(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @AfterClass
    public static void tearDown() {
    }

}
