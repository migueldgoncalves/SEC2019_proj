public class Good {
    private int OwnerId;
    private int GoodId;
    private String Name;
    private boolean OnSale;

    public int getOwnerId() {
        return OwnerId;
    }

    public void setOwnerId(int ownerId) {
        OwnerId = ownerId;
    }

    public int getGoodId() {
        return GoodId;
    }

    public void setGoodId(int goodId) {
        GoodId = goodId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isOnSale() {
        return OnSale;
    }

    public void setOnSale(boolean onSale) {
        OnSale = onSale;
    }
}
