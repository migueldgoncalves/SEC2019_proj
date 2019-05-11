public class Good {
    private int OwnerId;
    private int GoodId;
    private String Name;
    private boolean OnSale;

    public Good(int owner, int goodId, String Name, boolean isOnSale) {
        this.OwnerId = owner;
        this.GoodId = goodId;
        this.Name = Name;
        this.OnSale = isOnSale;
    }

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

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Good)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Good c = (Good) o;

        // Compare the data members and return accordingly
        return this.OwnerId == c.getOwnerId() && this.GoodId == c.getGoodId() && this.Name.equals(c.getName()) && this.OnSale == c.isOnSale();
    }

}
