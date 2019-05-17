import RequestClasses.AbstractRequest;
import com.google.gson.Gson;

import java.security.PublicKey;

class SecurityManager {

    /**
     * Method that validates if a received Request object by the server is valid by checking if Signatures Match
     * @param pedido The Request object that will be verified
     */
    static boolean validateRequest(AbstractRequest pedido, PublicKey pubKey) {
        Gson gson = new Gson();
        byte[] signature = pedido.getSignature();
        pedido.setSignature(null);
        boolean result = SignatureGenerator.verifySignature(pubKey, signature, gson.toJson(pedido));
        pedido.setSignature(signature);

        return result;
    }

}
