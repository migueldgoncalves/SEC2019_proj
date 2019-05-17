package RequestClasses;

import AnswerClasses.BuyerAnswer;

public class TransferGoodRequest extends AbstractRequest {

    private BuyerAnswer buyerAnswer;
    private int WriteTimeStamp;
    private String[] spamPrevention;

    public BuyerAnswer getBuyerAnswer() {
        return buyerAnswer;
    }

    public void setBuyerAnswer(BuyerAnswer buyerAnswer) {
        this.buyerAnswer = buyerAnswer;
    }

    public int getWriteTimeStamp() {
        return WriteTimeStamp;
    }

    public void setWriteTimeStamp(int writeTimeStamp) {
        WriteTimeStamp = writeTimeStamp;
    }

    public String[] getSpamPrevention() {
        return spamPrevention;
    }

    public void setSpamPrevention(String[] spamPrevention) {
        this.spamPrevention = spamPrevention;
    }
}
