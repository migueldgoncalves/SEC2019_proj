import java.util.ArrayList;
import java.util.HashMap;

public class UpdateRequest {

    private HashMap<Integer, ArrayList<Good>> pairs = new HashMap<>();

    public HashMap<Integer, ArrayList<Good>> getPairs() {
        return pairs;
    }

    public void setPairs(HashMap<Integer, ArrayList<Good>> pairs) {
        this.pairs = pairs;
    }
}
