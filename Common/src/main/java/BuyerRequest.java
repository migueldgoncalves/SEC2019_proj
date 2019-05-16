public class BuyerRequest extends AbstractRequest{

    private int GoodId;
    private int WriteTimeStamp;

    public int getGoodId() {
        return GoodId;
    }

    public void setGoodId(int goodId) {
        GoodId = goodId;
    }

    public int getWriteTimeStamp() {
        return WriteTimeStamp;
    }

    public void setWriteTimeStamp(int writeTimeStamp) {
        WriteTimeStamp = writeTimeStamp;
    }
}
