public class PrepareTransferRequest extends AbstractRequest {

    private int ReadId;
    private BuyerRequest buyerRequest;

    public int getReadId() {
        return ReadId;
    }

    public void setReadId(int readId) {
        ReadId = readId;
    }

    public BuyerRequest getBuyerRequest() {
        return buyerRequest;
    }

    public void setBuyerRequest(BuyerRequest buyerRequest) {
        this.buyerRequest = buyerRequest;
    }
}
