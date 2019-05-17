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

            System.out.println("Please Introduce the Good ID:");
            System.out.print("Good ID: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            while (!tryParseInt(input)) {
                System.out.println("The Introduced ID is invalid, please type only the number of the Good ID you want");
                System.out.print("Good ID: ");
                input = reader.readLine();
            }

            PrepareSellRequest pedido = new PrepareSellRequest(Integer.parseInt(input), new Date().getTime(), UserID, readId);
            pedido.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(pedido)));

            return gson.toJson(pedido);

        } catch (Exception e) {
            System.out.println("Something went wrong during prompting for Good ID");
            e.printStackTrace();
            return null;
        }
    }

    static String promptForGoodIdGetState(int readId, int UserID, PrivateKey privKey) {
        try {
            Gson gson = new Gson();

            System.out.println("Please Introduce the Good ID:");
            System.out.print("Good ID: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            while (!tryParseInt(input)) {
                System.out.println("The Introduced ID is invalid, please type only the number of the Good ID you want");
                System.out.print("Good ID: ");
                input = reader.readLine();
            }

            GetStateRequest pedido = new GetStateRequest(Integer.parseInt(input), new Date().getTime(), UserID, readId);
            pedido.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(pedido)));

            return gson.toJson(pedido);

        } catch (Exception e) {
            System.out.println("Something went wrong during prompting for Good ID");
            e.printStackTrace();
            return null;
        }
    }

    static void printMenu() {
        System.out.print("Please Introduce The Desired Option Number: \n 1. Sell an Item. \n 2. Buy an Item. \n 3. Get Item State. \n Option Number: ");
    }

    static int promptForSellerId() {
        System.out.println("Please Introduce Seller ID:");
        System.out.print("Seller ID: ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String temp = reader.readLine();
            while (!tryParseInt(temp)) {
                System.out.println("The introduced ID is not a valid Number, please introduce ONLY numbers");
                System.out.print("Seller ID: ");
                temp = reader.readLine();
            }
            return Integer.parseInt(temp);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    static int promptForPortNumber() {
        try {
            System.out.println("Please Introduce Port Number:");
            System.out.print("Port Number: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String temp = reader.readLine();
            while (!tryParseInt(temp)) {
                System.out.println("The Introduced Port Number is not a valid Number, please introduce ONLY numbers");
                System.out.print("Port Number: ");
                temp = reader.readLine();
            }
            return Integer.parseInt(temp);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    static int promptForGoodId() {
        try {
            System.out.println("Please Introduce GoodId:");
            System.out.print("Good ID: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String temp = reader.readLine();
            while (!tryParseInt(temp)) {
                System.out.println("The Introduced ID is not a valid Number, please introduce ONLY numbers");
                System.out.print("Good ID: ");
                temp = reader.readLine();
            }
            return Integer.parseInt(temp);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
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
