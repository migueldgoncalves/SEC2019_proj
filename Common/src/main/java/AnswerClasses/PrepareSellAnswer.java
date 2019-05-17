package AnswerClasses;

public class PrepareSellAnswer extends AbstractAnswer {

    private Good good;
    private int ReadId;

    public PrepareSellAnswer(String answer, int NotaryId, long Nounce, int ReadId, Good good){
        this.answer = answer;
        this.NotaryId = NotaryId;
        this.Nounce = Nounce;
        this.ReadId = ReadId;
        this.good = good;
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

}
