import RequestClasses.GetStateRequest;
import RequestClasses.PrepareSellRequest;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.util.Date;

public class IOManager {

    public static String promptForGoodIdSell(int readId, int UserID, PrivateKey privKey) {
        try {
            Gson gson = new Gson();

            System.out.println("Please Introduce the AnswerClasses.Good ID:");
            System.out.print("AnswerClasses.Good ID: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            while (!tryParseInt(input)) {
                System.out.println("The Introduced ID is invalid, please type only the number of the AnswerClasses.Good ID you want");
                System.out.print("AnswerClasses.Good ID: ");
                input = reader.readLine();
            }

            PrepareSellRequest pedido = new PrepareSellRequest(Integer.parseInt(input), new Date().getTime(), UserID, readId);
            pedido.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(pedido)));

            return gson.toJson(pedido);

        } catch (Exception e) {
            System.out.println("Something went wrong during prompting for AnswerClasses.Good ID");
            e.printStackTrace();
            return null;
        }
    }

    static String promptForGoodIdGetState(int readId, int UserID, PrivateKey privKey) {
        try {
            Gson gson = new Gson();

            System.out.println("Please Introduce the AnswerClasses.Good ID:");
            System.out.print("AnswerClasses.Good ID: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            while (!tryParseInt(input)) {
                System.out.println("The Introduced ID is invalid, please type only the number of the AnswerClasses.Good ID you want");
                System.out.print("AnswerClasses.Good ID: ");
                input = reader.readLine();
            }

            GetStateRequest pedido = new GetStateRequest(Integer.parseInt(input), new Date().getTime(), UserID, readId);
            pedido.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(pedido)));

            return gson.toJson(pedido);

        } catch (Exception e) {
            System.out.println("Something went wrong during prompting for AnswerClasses.Good ID");
            e.printStackTrace();
            return null;
        }
    }

    private static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            System.out.println("The introduced Input could not be converted to an integer.");
            return false;
        }
    }

}
