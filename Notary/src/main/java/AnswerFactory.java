import AnswerClasses.Good;
import AnswerClasses.SellAnswer;
import com.google.gson.Gson;

import java.security.PrivateKey;
import java.util.Date;

class AnswerFactory {

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
