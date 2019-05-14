import com.google.gson.Gson;

import java.security.PublicKey;

public class SecurityValidator {

    /**
     * Method that validates if a received Request object by the server is valid by checking if Signatures Match
     * @param pedido The Request object that will be verified
     */
    static boolean validateRequest(PrepareSellRequest pedido, PublicKey publicKey) {
        Gson gson = new Gson();
        byte[] signature = pedido.getSignature();
        pedido.setSignature(null);

        return SignatureGenerator.verifySignature(publicKey, signature, gson.toJson(pedido));
    }

}
