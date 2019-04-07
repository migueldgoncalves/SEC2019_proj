import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;

public class Client extends UnicastRemoteObject implements iClient {

    private static iProxy proxy = null;
    private static PrivateKey privKey;
    private static PublicKey pubKey;
    private static Gson gson = new Gson();
    private static int UserID;
    private static boolean USING_CC = false;

    private static final String RESOURCES_DIR = "Client\\src\\main\\resources\\";

    private static void sell(String data) {
        try {
            String jsonAnswer = proxy.sell(data);
            Request answer = gson.fromJson(jsonAnswer, Request.class);

            if(!validateRequest(answer, Sender.NOTARY)){
                System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
            }else {
                System.out.println(answer.getAnswer());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Client() throws RemoteException {
        super();
    }

    public static void main(String[] args) {
        try {
            //Prompt User For Input of Port To Register
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            iClient ClientProxy = new Client();

            System.out.println("Is Server Running With CC? Introduce Option Number:");
            System.out.println("1. YES");
            System.out.println("2. NO");
            System.out.print("Option Number:");
            String option =reader.readLine();

            while (!tryParseInt(option)){
                System.out.println("Please Introduce only the Number of The Option You Select:");
                System.out.print("Option Number:");
                option = reader.readLine();
            }

            switch (Integer.parseInt(option)){
                case 1:
                    USING_CC = true;
                    break;
                case 2:
                    USING_CC = false;
                    break;
            }

            System.out.println("Please Introduce The Port You Want to Register:");
            System.out.print("PORT Number: ");
            String port = reader.readLine();

            while (!tryParseInt(port)) {
                System.out.println("Introduce a valid Port Number:");
                System.out.print("Port Number: ");
                port = reader.readLine();
            }
            int portNumber = Integer.parseInt(port);

            LocateRegistry.createRegistry(portNumber);

            //End Of Client Registration in RMI

            proxy = (iProxy) Naming.lookup("rmi://localhost:8086/Notary");

            System.out.println("Please Introduce User ID: ");
            String ID = reader.readLine();

            if (tryParseInt(ID)) {

                UserID = Integer.parseInt(ID);
                Naming.rebind("rmi://localhost:" + port + "/" + UserID, ClientProxy);
                loadKeys();
                printMenu();

                String input = reader.readLine();
                while (tryParseInt(input) && !input.equals("exit")) {

                    switch (input) {
                        case "1":
                            String data = promptForGoodId();
                            Runnable r = () -> sell(data);
                            new Thread(r).start();
                            break;
                        case "2":
                            int seller = promptForSellerId();
                            int good = prompForGoodId();
                            int clientPort = promptForPortNumber();
                            Runnable r2 = () -> invokeSeller(seller, good, clientPort);
                            new Thread(r2).start();
                            break;
                        case "3":
                            String data3 = promptForGoodId();
                            Runnable r3 = () -> getStateOfGood(data3);
                            new Thread(r3).start();
                            break;
                        default:
                            System.out.println("The Introduced Input is not a valid number, please try again or type 'exit' to exit program.");
                            break;
                    }

                    printMenu();
                    input = reader.readLine();
                }
                reader.close();
                System.exit(1);
            } else {
                throw new Exception("The Introduced value is not convertible to an Integer type variable or user ID does not exist in the server. Exiting ...");
            }

        }catch (ConnectException e){
            System.out.println("Could not connect to server. The server may be offline or unavailable due to network reasons.");
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    //########################################### Main Methods #####################################################

    private static int prompForGoodId() {
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

    private static String promptForGoodId() {
        try {
            System.out.println("Please Introduce the Good ID:");
            System.out.print("Good ID: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();

            while (!tryParseInt(input)) {
                System.out.println("The Introduced ID is invalid, please type only the number of the Good ID you want");
                System.out.print("Good ID: ");
                input = reader.readLine();
            }

            Request pedido = new Request();
            pedido.setGoodId(Integer.parseInt(input));
            pedido.setUserId(UserID);
            pedido.setNounce(new Random().nextInt());
            String jsonToString = gson.toJson(pedido);
            byte[] sig = SignatureGenerator.generateSignature(privKey, jsonToString);
            pedido.setSignature(sig);

            return gson.toJson(pedido);

        } catch (Exception e) {
            System.out.println("Something went wrong during prompting for Good ID");
            return null;
        }
    }

    private static void getStateOfGood(String data) {
        try {
            String jsonAnswer = proxy.getStateOfGood(data);
            Request answer = gson.fromJson(jsonAnswer, Request.class);

            if(!validateRequest(answer, Sender.NOTARY)){
                System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
            }else {
                System.out.println(answer.getAnswer());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void invokeSeller(int sellerId, int goodId, int portNumber) {
        try {
            iClient clientProxy = (iClient) Naming.lookup("rmi://localhost:" + portNumber + "/" + sellerId);

            Request pedido = new Request();

            pedido.setUserId(UserID);
            pedido.setBuyerId(UserID);
            pedido.setSellerId(sellerId);
            pedido.setGoodId(goodId);
            pedido.setSignature(null);
            pedido.setNounce(new Random().nextInt());

            String jsonInString = gson.toJson(pedido);

            pedido.setSignature(SignatureGenerator.generateSignature(privKey, jsonInString));

            String request = gson.toJson(pedido);

            System.out.println("This is the Jason Object: " + jsonInString);

            String jsonAnswer = clientProxy.Buy(request);
            Request answer = gson.fromJson(jsonAnswer, Request.class);
            if(!validateRequest(answer, Sender.NOTARY)){
                System.out.println("Message Has Been Tampered With");
            }else {
                System.out.println(answer.getAnswer());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //###################################### User Prompts For Input ################################################

    private static int promptForPortNumber() {
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

    private static int promptForSellerId() {
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

    private static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            System.out.println("The introduced Input could not be converted to an integer.");
            return false;
        }
    }

    private static void loadKeys() {
        try {
            privKey = RSAKeyLoader.getPriv(RESOURCES_DIR + "User" + UserID + ".key");
            pubKey = RSAKeyLoader.getPub(RESOURCES_DIR + "User" + UserID + ".pub");
            System.out.println("Public and Private Keys Loaded");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Thrown in Body of Method loadKeys! Public and Private keys unable to load!");
        }
    }

    //########################################## Auxiliary Methods ####################################################

    public String Buy(String request) {
        try {
            Request received = gson.fromJson(request, Request.class);

            if(!validateRequest(received, Sender.BUYER)){
                return "Message Has Been Tampered With!";
            }

            //Request To Transfer Item
            received.setUserId(UserID);
            received.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(received)));

            return proxy.transferGood(gson.toJson(received));
        } catch (Exception e) {
            System.out.println("Something Went Wrong During the Transfer");
            e.printStackTrace();
            return "The Good Transfer Has Failed. Please Try Again.";
        }
    }

    private static boolean validateRequest(Request pedido, Sender invoker) {

        //Verify Signature withing Object
        byte[] signature = pedido.getSignature();
        pedido.setSignature(null);

        switch (invoker){
            case BUYER:
                try{
                    PublicKey notaryPubKey = RSAKeyLoader.getPub(RESOURCES_DIR + "User" + pedido.getUserId() + ".pub");
                    return SignatureGenerator.verifySignature(notaryPubKey, signature, gson.toJson(pedido));
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            case NOTARY:
                try{
                    if(USING_CC){
                        PublicKey notaryPubKey = RSAKeyLoader.getPub(RESOURCES_DIR + "Notary_CC.pub");
                        return SignatureGenerator.verifySignature(notaryPubKey, signature, gson.toJson(pedido));
                    }else {
                        PublicKey notaryPubKey = RSAKeyLoader.getPub(RESOURCES_DIR + "Notary.pub");
                        return SignatureGenerator.verifySignature(notaryPubKey, signature, gson.toJson(pedido));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
        }

        return false;

    }

    private enum Sender {
        NOTARY, BUYER
    }

    private static void printMenu() {
        System.out.print("Please Introduce The Desired Option Number: \n 1. Sell an Item. \n 2. Buy an Item. \n 3. Get Item State. \n Option Number: ");
    }

}
