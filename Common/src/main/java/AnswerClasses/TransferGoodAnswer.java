package AnswerClasses;

public class TransferGoodAnswer extends AbstractAnswer {

    private Good good;

    public TransferGoodAnswer(String answer, int NotaryId, long Nounce, Good good){
        this.answer = answer;
        this.NotaryId = NotaryId;
        this.Nounce = Nounce;
        this.good = good;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }
}