import com.google.gson.Gson;

import java.security.PublicKey;

public class SecurityValidator {

    public static boolean validatePrepareSellAnswer(PrepareSellAnswer pedido, boolean USING_CC) {
        Gson gson = new Gson();
        byte[] signature = pedido.getSignature();
        pedido.setSignature(null);

        try{
            if(USING_CC){
                PublicKey notaryPubKey = iCartaoCidadao.getPublicKeyFromCC();
                return SignatureGenerator.verifySignatureCartaoCidadao(notaryPubKey, signature, gson.toJson(pedido));
            }else {
                PublicKey notaryPubKey = RSAKeyLoader.getPub(Client.baseDirGenerator() + "\\src\\main\\resources\\Notary.pub");
                return SignatureGenerator.verifySignature(notaryPubKey, signature, gson.toJson(pedido));
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}