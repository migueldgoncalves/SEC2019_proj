import java.util.ArrayList;

public class SellRequest extends AbstractRequest {

    ArrayList<Request> requests;

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }

}
