import java.security.PublicKey;

public class CartaoCidadao {

    public static byte[] sign(String message) {
        try {
            eIDLib_PKCS11_test.setUp();
            byte[] signature = eIDLib_PKCS11_test.sign(message);
            eIDLib_PKCS11_test.exitPteid();
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verify(String message, byte[] signature) {
        try {
            PublicKey key = CartaoCidadao.getPublicKeyFromCC();
            return SignatureGenerator.verifySignatureCartaoCidadao(key, signature, message);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static PublicKey getPublicKeyFromCC() {
        try {
            eIDLib_PKCS11_test.setUp();
            PublicKey key = eIDLib_PKCS11_test.getPublicKeyFromCertificate();
            eIDLib_PKCS11_test.exitPteid();
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
