import AnswerClasses.AbstractAnswer;
import RequestClasses.AbstractRequest;
import com.google.gson.Gson;

import java.security.PublicKey;

class SecurityValidator {

    static boolean validateNotaryAnswer(AbstractAnswer answer, boolean USING_CC){
        Gson gson = new Gson();
        byte[] signature = answer.getSignature();
        answer.setSignature(null);

        try{
            if(USING_CC){
                PublicKey notaryPubKey = iCartaoCidadao.getPublicKeyFromCC();
                return SignatureGenerator.verifySignatureCartaoCidadao(notaryPubKey, signature, gson.toJson(answer));
            }else {
                PublicKey notaryPubKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, answer.getNotaryId());
                return SignatureGenerator.verifySignature(notaryPubKey, signature, gson.toJson(answer));
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    static boolean validateClientRequest(AbstractRequest request){
        Gson gson = new Gson();
        byte[] signature = request.getSignature();
        request.setSignature(null);

        try{
            PublicKey notaryPubKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.CLIENT, request.getUserId());
            return SignatureGenerator.verifySignature(notaryPubKey, signature, gson.toJson(request.getUserId()));
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
