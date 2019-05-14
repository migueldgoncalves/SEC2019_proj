import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyStoreInterfaceWithoutCCTest {

    @Before
    public void setUp() {
        try {
            KeyStoreInterface.deleteKeystore();

            KeyStoreInterface.createBaseKeyStore();
            KeyStoreInterface.addNotaryKeysToKeyStore(1, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Having all test cases in one test will allow to create base key store only once, speeding the tests
    @Test
    public void getKeysFromKeyStoreTest() {
        try {
            PublicKey publicKey;
            PrivateKey privateKey;

            // Invalid UserType

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(-123456789, 1);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(-123456789, 1);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            // Invalid Id

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

            // Get Successful

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 1);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 1);
            Assert.assertNotNull(publicKey);
            Assert.assertNotNull(privateKey);
            byte[] signature = SignatureGenerator.generateSignature(privateKey, "message");
            Assert.assertTrue(SignatureGenerator.verifySignature(publicKey, signature, "message"));
            Assert.assertFalse(SignatureGenerator.verifySignature(publicKey, signature, "otherMessage"));

            for (int i = 1; i <= KeyStoreInterface.CLIENT_MAX_NUMBER; i++) {
                publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.CLIENT, i);
                privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, i);
                Assert.assertNotNull(publicKey);
                Assert.assertNotNull(privateKey);
                signature = SignatureGenerator.generateSignature(privateKey, "message");
                Assert.assertTrue(SignatureGenerator.verifySignature(publicKey, signature, "message"));
                Assert.assertFalse(SignatureGenerator.verifySignature(publicKey, signature, "otherMessage"));
            }

            // Add key invalid Id

            KeyStoreInterface.addNotaryKeysToKeyStore(-1, false);
            KeyStoreInterface.addNotaryKeysToKeyStore(0, false);
            KeyStoreInterface.addNotaryKeysToKeyStore(1, false);

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
            Assert.assertNotNull(privateKey);
            signature = SignatureGenerator.generateSignature(privateKey, "message");
            Assert.assertTrue(SignatureGenerator.verifySignature(publicKey, signature, "message"));
            Assert.assertFalse(SignatureGenerator.verifySignature(publicKey, signature, "otherMessage"));

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 2);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 2);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);

            // Add key successful

            KeyStoreInterface.addNotaryKeysToKeyStore(2, false);

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
                Assert.assertNotNull(privateKey);
                signature = SignatureGenerator.generateSignature(privateKey, "message");
                Assert.assertTrue(SignatureGenerator.verifySignature(publicKey, signature, "message"));
                Assert.assertFalse(SignatureGenerator.verifySignature(publicKey, signature, "otherMessage"));
            }

            publicKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, 3);
            privateKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, 3);
            Assert.assertNull(publicKey);
            Assert.assertNull(privateKey);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @After
    public void tearDown() {
        KeyStoreInterface.deleteKeystore();
    }
}
