import java.util.ArrayList;

public class Request {

    private int notaryId;
    private int userId;
    private int goodId;
    private int buyerId;
    private int sellerId;

    private long nounce;
    private long buyerNounce;

    private byte[] signature;
    private byte[] buyerSignature;

    private String answer;
    private Good good;

    private ArrayList<Request> answersFromNotaries = new ArrayList<>();

    public Request(){}

    public Request(int notaryId, int userId, int goodId, int buyerId, int sellerId, long nounce, byte[] signature, String answer){
        this.notaryId = notaryId;
        this.userId = userId;
        this.goodId = goodId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.nounce = nounce;
        this.signature = signature;
        this.answer = answer;
    }

    public int getNotaryId(){
        return notaryId;
    }

    public void setNotaryId(int notaryId){
        this.notaryId = notaryId;
    }

    public long getNounce() {
        return nounce;
    }

    public void setNounce(long nounce) {
        this.nounce = nounce;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getAnswer(){
        return answer;
    }

    public void setAnswer(String answer){
        this.answer = answer;
    }

    public byte[] getBuyerSignature() {
        return buyerSignature;
    }

    public void setBuyerSignature(byte[] buyerSignature) {
        this.buyerSignature = buyerSignature;
    }

    public long getBuyerNounce() {
        return buyerNounce;
    }

    public void setBuyerNounce(long buyerNounce) {
        this.buyerNounce = buyerNounce;
    }

    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public ArrayList<Request> getAnswersFromNotaries() {
        return answersFromNotaries;
    }

    public void setAnswersFromNotaries(ArrayList<Request> answersFromNotaries) {
        this.answersFromNotaries = answersFromNotaries;
    }
}
