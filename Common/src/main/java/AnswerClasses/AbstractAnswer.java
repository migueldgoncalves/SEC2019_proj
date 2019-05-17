package AnswerClasses;

public abstract class AbstractAnswer {

    long Nounce;
    private byte[] Signature;
    int NotaryId;
    String answer;

    public long getNounce() {
        return Nounce;
    }

    public void setNounce(long nounce) {
        Nounce = nounce;
    }

    public byte[] getSignature() {
        return Signature;
    }

    public void setSignature(byte[] signature) {
        Signature = signature;
    }

    public int getNotaryId() {
        return NotaryId;
    }

    public void setNotaryId(int notaryId) {
        NotaryId = notaryId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
