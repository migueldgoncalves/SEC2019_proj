import java.util.concurrent.ConcurrentHashMap;

public class NonceVerifier {

    private static ConcurrentHashMap<Integer, Long> nonceMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Long> notaryNonceMap = new ConcurrentHashMap<>();

    public static boolean isClientNonceValid(int userId, long nounce) {
        if (nonceMap.get(userId) == null) {
            nonceMap.put(userId, nounce);
            return true;
        } else{
            if(nonceMap.get(userId) < nounce){
                nonceMap.put(userId, nounce);
                return true;
            }else{
                return false;
            }
        }
    }

    public static boolean isNotaryNonceValid(int notaryId, long nounce) {
        if(notaryId != 0){
            if (notaryNonceMap.get(notaryId) == null) {
                notaryNonceMap.put(notaryId, nounce);
                return true;
            } else{
                if(notaryNonceMap.get(notaryId) <= nounce){
                    notaryNonceMap.put(notaryId, nounce);
                    return true;
                }else{
                    return false;
                }
            }
        }

        return false;

    }

}
