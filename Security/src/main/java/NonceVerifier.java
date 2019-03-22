import java.util.ArrayList;
import java.util.TreeMap;

public class NonceVerifier {

    public TreeMap<Integer, ArrayList<Integer>> nonceMap = null;

    public boolean isNonceValid(Request request) {
        int senderId = request.getUserId();
        int nonce = request.getNonce();

        for (int i = 0; i < nonceMap.get(senderId).size(); i++)
            if (nonceMap.get(senderId).contains(nonce))
                return false;
        nonceMap.get(senderId).add(nonce);
        return true;
    }
}
