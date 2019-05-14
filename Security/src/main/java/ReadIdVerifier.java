import java.util.concurrent.ConcurrentHashMap;

public class ReadIdVerifier {

    //Mapping UserId to correspondent Read ID
    public static ConcurrentHashMap<Integer, Integer> readIdMap = new ConcurrentHashMap<>();

    public static boolean validateReadId(int readId, int userId){
        if(readIdMap.get(userId) == null){
            readIdMap.put(userId, readId);
            return true;
        }else {
            return readId == (readIdMap.get(userId));
        }
    }

}
