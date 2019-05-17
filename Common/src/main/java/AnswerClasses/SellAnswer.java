package AnswerClasses;

public class SellAnswer extends AbstractAnswer {

    private String Answer;
    private Good good;

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }
}
