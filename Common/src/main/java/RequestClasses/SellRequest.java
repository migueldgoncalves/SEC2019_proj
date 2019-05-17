package RequestClasses;

import AnswerClasses.PrepareSellAnswer;

import java.util.ArrayList;

public class SellRequest extends AbstractRequest {

    private ArrayList<PrepareSellAnswer> requests;

    public ArrayList<PrepareSellAnswer> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<PrepareSellAnswer> requests) {
        this.requests = requests;
    }

}
