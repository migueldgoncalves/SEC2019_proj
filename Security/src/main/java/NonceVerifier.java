import java.util.concurrent.ConcurrentHashMap;

public class NonceVerifier {

    private static ConcurrentHashMap<Integer, Long> nonceMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Long> notaryNonceMap = new ConcurrentHashMap<>();

    public static boolean isNonceValid(Request request) {

        if(request.getNotaryId() != 0 && request.getUserId() == 0){
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

    public static boolean isNonceValid(PrepareSellRequest request) {

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

    public static boolean isNonceValid(BuyerAnswer request) {

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

    public static boolean isNonceValid(TransferGoodRequest request) {

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

    public static boolean isNonceValid(BuyerRequest request) {

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

    public static boolean isNonceValid(PrepareTransferRequest request) {

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

    public static boolean isNonceValid(PrepareSellAnswer request) {

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

        return false;

    }

    public static boolean isNonceValid(TransferGoodAnswer request) {

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

        return false;

    }

    public static boolean isNonceValid(PrepareTransferAnswer request) {

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

        return false;

    }

    public static boolean isNonceValid(SellAnswer request) {

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

        return false;

    }

    public static boolean isNonceValid(SellRequest request) {
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
