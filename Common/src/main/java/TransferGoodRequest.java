public class TransferGoodRequest extends AbstractRequest{

    private BuyerAnswer buyerAnswer;
    private int WriteTimeStamp;

    public BuyerAnswer getBuyerAnswer() {
        return buyerAnswer;
    }

    public void setBuyerAnswer(BuyerAnswer buyerAnswer) {
        this.buyerAnswer = buyerAnswer;
    }

    public int getWriteTimeStamp() {
        return WriteTimeStamp;
    }

    public void setWriteTimeStamp(int writeTimeStamp) {
        WriteTimeStamp = writeTimeStamp;
    }
}
