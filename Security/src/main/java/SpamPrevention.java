import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

public class SpamPrevention {

    static final int HASHCASH_VERSION = 1;
    static final int ZERO_BITS = 20;
    static final int BITS_PER_HEXA = 4;

    public static String[] xhashcashGenerator(int nounce, int serverPort) {
        try {
            String[] answer = new String[2];

            String finalHeader;
            String hash;
            int randomInt = new Random().nextInt();
            do {
                randomInt++;
                finalHeader = baseHeaderGenerator(nounce, serverPort);
                finalHeader += randomInt;

                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                hash = Hex.encodeHexString(digest.digest(finalHeader.getBytes(StandardCharsets.UTF_8)));

            } while (!hash.startsWith(zerosSubstringGenerator(ZERO_BITS)));
            answer[0] = finalHeader;
            answer[1] = hash;
            return answer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean xhashcashValidator(String[] spamPrevention, int correctNounce, int serverPort) {
        try {
            if(spamPrevention.length!=2)
                return false;

            if(!spamPrevention[1].startsWith(zerosSubstringGenerator(ZERO_BITS)))
                return false;

            if(!spamPrevention[0].startsWith(baseHeaderGenerator(correctNounce, serverPort)))
                return false;

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            String hash = Hex.encodeHexString(digest.digest(spamPrevention[0].getBytes(StandardCharsets.UTF_8)));

            if(!spamPrevention[1].equals(hash))
                return false;

            String[] splitHeader = spamPrevention[0].split(":");

            return splitHeader[3].equals(String.valueOf(correctNounce));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static String zerosSubstringGenerator(int number) {
        StringBuilder zeros = new StringBuilder();
        if(number > 0) {
            for (int i = 1; i <= number / BITS_PER_HEXA; i++)
                zeros.append("0");
        }
        return zeros.toString();
    }

    static String baseHeaderGenerator(int nounce, int serverPort) {
        return "X-Hashcash: " +
                HASHCASH_VERSION + ":" +
                ZERO_BITS + ":" +
                nounce + ":" +
                serverPort + ":";
    }
}
