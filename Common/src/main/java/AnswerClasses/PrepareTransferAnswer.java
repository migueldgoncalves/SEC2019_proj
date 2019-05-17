package AnswerClasses;

import RequestClasses.PrepareTransferRequest;

public class PrepareTransferAnswer extends AbstractAnswer {

    private Good good;
    private int ReadId;
    private PrepareTransferRequest sellerRequest;

    public PrepareTransferAnswer(String answer, int NotaryId, long Nounce, Good good, int ReadId, PrepareTransferRequest request){
        this.answer = answer;
        this.NotaryId = NotaryId;
        this.Nounce = Nounce;
        this.good = good;
        this.ReadId = ReadId;
        this.sellerRequest = request;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public int getReadId() {
        return ReadId;
    }

    public void setReadId(int readId) {
        ReadId = readId;
    }

    public PrepareTransferRequest getSellerRequest() {
        return sellerRequest;
    }

    public void setSellerRequest(PrepareTransferRequest sellerRequest) {
        this.sellerRequest = sellerRequest;
    }
}
