import java.util.concurrent.ConcurrentHashMap;

public class NonceVerifier {

    private static ConcurrentHashMap<Integer, Long> nonceMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Long> notaryNonceMap = new ConcurrentHashMap<>();

    public static boolean isNonceValid(Request request) {

        if(request.getNotaryId() != 0){
            if (notaryNonceMap.get(request.getNotaryId()) == null) {
                notaryNonceMap.put(request.getNotaryId(), request.getNounce());
                return true;
            } else{
                if(notaryNonceMap.get(request.getNotaryId()) <= request.getNounce()){
                    notaryNonceMap.put(request.getNotaryId(), request.getNounce());
                    return true;
                }else{
                    return false;
                }
            }
        }

        if (nonceMap.get(request.getUserId()) == null) {
            nonceMap.put(request.getUserId(), request.getNounce());
            return true;
        } else{
            if(nonceMap.get(request.getUserId()) < request.getNounce()){
                nonceMap.put(request.getUserId(), request.getNounce());
                return true;
            }else{
                return false;
            }
        }
    }
}
