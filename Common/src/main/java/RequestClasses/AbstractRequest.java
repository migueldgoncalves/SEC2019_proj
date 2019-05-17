package RequestClasses;

public abstract class AbstractRequest {

    long Nounce;
    private byte[] Signature;
    int UserId;
    private String answer;

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

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
