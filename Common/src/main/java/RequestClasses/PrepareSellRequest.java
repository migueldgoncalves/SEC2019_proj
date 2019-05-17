package RequestClasses;

public class PrepareSellRequest extends AbstractRequest {

    private int GoodId;
    private int ReadId;

    public PrepareSellRequest(int GoodId, long Nounce, int UserId, int ReadId){
        this.GoodId = GoodId;
        this.ReadId = ReadId;
        this.Nounce = Nounce;
        this.UserId = UserId;
    }

    public int getGoodId() {
        return GoodId;
    }

    public void setGoodId(int goodId) {
        GoodId = goodId;
    }

    public int getReadId() {
        return ReadId;
    }

    public void setReadId(int readId) {
        ReadId = readId;
    }

}
