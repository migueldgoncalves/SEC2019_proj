package AnswerClasses;

import java.util.ArrayList;

public class BuyerAnswer{

    private ArrayList<PrepareTransferAnswer> notaryAnswers;
    private int UserId;
    private long nounce;
    private byte[] Signature;

    public ArrayList<PrepareTransferAnswer> getNotaryAnswers() {
        return notaryAnswers;
    }

    public void setNotaryAnswers(ArrayList<PrepareTransferAnswer> notaryAnswers) {
        this.notaryAnswers = notaryAnswers;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public long getNounce() {
        return nounce;
    }

    public void setNounce(long nounce) {
        this.nounce = nounce;
    }

    public byte[] getSignature() {
        return Signature;
    }

    public void setSignature(byte[] signature) {
        Signature = signature;
    }
}
