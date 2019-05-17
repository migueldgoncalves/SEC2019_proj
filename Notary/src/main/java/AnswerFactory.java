import AnswerClasses.Good;
import AnswerClasses.PrepareSellAnswer;
import AnswerClasses.SellAnswer;
import com.google.gson.Gson;

import java.security.PrivateKey;
import java.util.Date;

class AnswerFactory {

    static String prepareSellAnswerFactory(String methodAnswer, Good good, int ID, int readId, boolean USING_CC, PrivateKey privKey){
        Gson gson = new Gson();

        PrepareSellAnswer answer = new PrepareSellAnswer();
        answer.setAnswer(methodAnswer);
        answer.setGood(good);
        answer.setNotaryId(ID);
        answer.setNounce(new Date().getTime());
        answer.setReadId(readId);

        if(USING_CC){
            answer.setSignature(iCartaoCidadao.sign(gson.toJson(answer)));
        }else {
            answer.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(answer)));
        }

        return gson.toJson(answer);
    }

    static String SellAnswerFactory(String methodAnswer, Good good, int NotaryId, boolean USING_CC, PrivateKey privKey){
        Gson gson = new Gson();
        SellAnswer answer = new SellAnswer();

        answer.setAnswer(methodAnswer);
        answer.setGood(good);
        answer.setNotaryId(NotaryId);
        answer.setNounce(new Date().getTime());

        if(USING_CC){
            answer.setSignature(iCartaoCidadao.sign(gson.toJson(answer)));
        }else {
            answer.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(answer)));
        }

        return gson.toJson(answer);

    }

}
