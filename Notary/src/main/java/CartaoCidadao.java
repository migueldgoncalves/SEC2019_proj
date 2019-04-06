import java.security.PublicKey;

public class CartaoCidadao {

    public static byte[] sign(String message) {
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
            CartaoCidadaoInterface.setUp();
            PublicKey key = CartaoCidadaoInterface.getPublicKeyFromCertificate();
            CartaoCidadaoInterface.exitPteid();
            return key;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeCCPublicKeyToFile() {
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
