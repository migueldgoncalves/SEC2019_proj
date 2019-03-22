import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class NonceVerifier {

    public static ConcurrentHashMap<Integer, ArrayList<Integer>> nonceMap = new ConcurrentHashMap<>();

    public static boolean isNonceValid(Request request) {
        int senderId = request.getUserId();
        int nonce = request.getNounce();

        for (int i = 0; i < nonceMap.get(senderId).size(); i++)
            if (nonceMap.get(senderId).contains(nonce))
                return false;
        nonceMap.get(senderId).add(nonce);
        return true;
    }
}
