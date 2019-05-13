public abstract class AbstractAnswer {

    private long Nounce;
    private byte[] Signature;
    private int NotaryId;

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

}
