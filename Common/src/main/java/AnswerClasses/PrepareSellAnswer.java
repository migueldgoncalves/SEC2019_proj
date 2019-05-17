package AnswerClasses;

public class PrepareSellAnswer extends AbstractAnswer {

    private Good good;
    private int ReadId;

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
