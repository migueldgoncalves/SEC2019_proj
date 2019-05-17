package AnswerClasses;

public class GetStateAnswer extends AbstractAnswer{

    private int readId;

    public GetStateAnswer(String answer, int NotaryId, long Nounce, int readId){
        this.answer = answer;
        this.NotaryId = NotaryId;
        this.Nounce = Nounce;
        this.readId = readId;
    }

    public int getReadId() {
        return readId;
    }
}
