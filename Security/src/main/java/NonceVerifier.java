import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class NonceVerifier {

    public static ConcurrentHashMap<Integer, ArrayList<Integer>> nonceMap = new ConcurrentHashMap<>();

    public static boolean isNonceValid(Request request) {
        if (nonceMap.get(request.getUserId()) == null) {
            nonceMap.put(request.getUserId(), new ArrayList<>());
            nonceMap.get(request.getUserId()).add(request.getNounce());
            return true;
        } else if (nonceMap.get(request.getUserId()).contains(request.getNounce())) {
            return false;
        } else {
            return nonceMap.get(request.getUserId()).add(request.getNounce());
        }
    }
}
