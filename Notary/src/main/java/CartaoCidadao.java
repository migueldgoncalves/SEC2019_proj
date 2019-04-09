import java.security.PublicKey;

public class CartaoCidadao {

    public synchronized static byte[] sign(String message) {
        try {
            CartaoCidadaoInterface.setUp();
            byte[] signature = CartaoCidadaoInterface.sign(message);
            CartaoCidadaoInterface.exitPteid();
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static boolean verify(String message, byte[] signature) {
        try {
            CartaoCidadaoInterface.setUp();
            PublicKey key = CartaoCidadao.getPublicKeyFromCC();
            boolean verify = SignatureGenerator.verifySignatureCartaoCidadao(key, signature, message);
            CartaoCidadaoInterface.exitPteid();
            return verify;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized static PublicKey getPublicKeyFromCC() {
        try {
            CartaoCidadaoInterface.setUp();
            PublicKey key = CartaoCidadaoInterface.getPublicKeyFromCertificate();
            CartaoCidadaoInterface.exitPteid();
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static void writeCCPublicKeyToFile() {
        try {
            CartaoCidadaoInterface.setUp();
            PublicKey key = CartaoCidadaoInterface.getPublicKeyFromCertificate();
            String baseDir = System.getProperty("user.dir").replace("\\Notary", "");
            RSAKeySaverAsText.SavePublicKeyAsText(key, baseDir + "\\Client\\src\\main\\resources\\Notary_CC");
            RSAKeySaverAsText.SavePublicKeyAsText(key, baseDir + "\\Notary\\src\\main\\resources\\Notary_CC");
            CartaoCidadaoInterface.exitPteid();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
