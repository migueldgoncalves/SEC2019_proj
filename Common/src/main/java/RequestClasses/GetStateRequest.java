package RequestClasses;

public class GetStateRequest extends AbstractRequest {

    private int GoodId;
    private int ReadId;

    public GetStateRequest(int goodId, long nounce, int userId, int readId){
        this.Nounce = nounce;
        this.UserId = userId;
        this.GoodId = goodId;
        this.ReadId = readId;
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
