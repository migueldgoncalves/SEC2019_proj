public abstract class AbstractRequest {

    private long Nounce;
    private byte[] Signature;
    private int UserId;

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

}
