import java.security.PublicKey;

public class CartaoCidadao {

    public static byte[] sign(String message) {
        eIDLib_PKCS11_test.setUp();
        byte[] signature = eIDLib_PKCS11_test.sign(message);
        eIDLib_PKCS11_test.exitPteid();
        return signature;
    }

    public static boolean verify(String message, byte[] signature) {
        PublicKey key = CartaoCidadao.getPublicKeyFromCC();
        return SignatureGenerator.verifySignatureCartaoCidadao(key, signature, message);
    }

    public static PublicKey getPublicKeyFromCC() {
        eIDLib_PKCS11_test.setUp();
        PublicKey key = eIDLib_PKCS11_test.getPublicKeyFromCertificate();
        eIDLib_PKCS11_test.exitPteid();
        return key;
    }
}
