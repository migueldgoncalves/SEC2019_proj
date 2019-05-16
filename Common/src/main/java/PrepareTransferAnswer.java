public class PrepareTransferAnswer extends AbstractAnswer{

    private Good good;
    private int ReadId;
    private PrepareTransferRequest sellerRequest;

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
