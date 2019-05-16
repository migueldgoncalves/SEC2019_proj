import java.security.PublicKey;

public class iCartaoCidadao {

    public synchronized static byte[] sign(String message) {
        try {
            CartaoCidadao.setUp();
            byte[] signature = CartaoCidadao.sign(message);
            CartaoCidadao.exitPteid();
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static boolean verify(String message, byte[] signature) {
        try {
            CartaoCidadao.setUp();
            PublicKey key = iCartaoCidadao.getPublicKeyFromCC();
            boolean verify = SignatureGenerator.verifySignatureCartaoCidadao(key, signature, message);
            CartaoCidadao.exitPteid();
            return verify;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized static PublicKey getPublicKeyFromCC() {
        try {
            CartaoCidadao.setUp();
            PublicKey key = CartaoCidadao.getPublicKeyFromCertificate();
            CartaoCidadao.exitPteid();
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
