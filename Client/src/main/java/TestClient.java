import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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

public class TestClient extends UnicastRemoteObject implements iClient {

    private static iProxy proxy = null;
    private static PrivateKey privKey;
    private static PublicKey pubKey;
    private static Gson gson = new Gson();
    private static int UserID;
    private static boolean USING_CC = false;

    private static void sell(String data) {
        try {
            String jsonAnswer = proxy.sell(data);
            Request answer = gson.fromJson(jsonAnswer, Request.class);

            if(!validateRequest(answer, Client.Sender.NOTARY)){
                System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
            }else {
                System.out.println(answer.getAnswer());
            }
        } catch (ConnectException e) {
            System.out.println("Could not connect to server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TestClient(int port, int ID, String option) throws RemoteException {
        super();
        try {
            iClient ClientProxy = new TestClient(port, ID, option);

            LocateRegistry.createRegistry(port);
            //End Of Client Registration in RMI

            proxy = (iProxy) Naming.lookup("rmi://localhost:8086/Notary");
            Naming.rebind("rmi://localhost:" + port + "/" + UserID, ClientProxy);
            loadKeys();

            switch (option) {
                case "0":
                    while(true) {}
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

            if(!validateRequest(answer, Client.Sender.NOTARY)){
                System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
            }else {
                System.out.println(answer.getAnswer());
            }
        } catch (ConnectException e) {
            System.out.println("Could not connect to server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void invokeSeller(int sellerId, int goodId, int portNumber) {
        String jsonAnswer = null;
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

            jsonAnswer = clientProxy.Buy(request);
            Request answer = gson.fromJson(jsonAnswer, Request.class);
            if(!validateRequest(answer, Client.Sender.NOTARY)){
                System.out.println("Message Has Been Tampered With");
            }else {
                System.out.println(answer.getAnswer());
            }
        } catch (JsonSyntaxException e) {
            System.out.println(jsonAnswer);
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
            privKey = RSAKeyLoader.getPriv( TestClient.baseDirGenerator() + "\\src\\main\\resources\\User" + UserID + ".key");
            pubKey = RSAKeyLoader.getPub(TestClient.baseDirGenerator() + "\\src\\main\\resources\\User" + UserID + ".pub");
            System.out.println("Public and Private Keys Loaded");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Thrown in Body of Method loadKeys! Public and Private keys unable to load!");
        }
    }

    //########################################## Auxiliary Methods ####################################################

    public String Buy(String request) {
        Request received = null;
        try {
            received = gson.fromJson(request, Request.class);

            if(!validateRequest(received, Client.Sender.BUYER)){
                return "Message Has Been Tampered With!";
            }

            //Request To Transfer Item
            received.setUserId(UserID);
            received.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(received)));

            return proxy.transferGood(gson.toJson(received));
        } catch (ConnectException e) {
            System.out.println("Could not connect to server on behalf of user " + received.getBuyerId());
            return "Could not connect to server";
        } catch (Exception e) {
            System.out.println("Something Went Wrong During the Transfer");
            e.printStackTrace();
            return "The Good Transfer Has Failed. Please Try Again.";
        }
    }

    private static boolean validateRequest(Request pedido, Client.Sender invoker) {

        //Verify Signature withing Object
        byte[] signature = pedido.getSignature();
        pedido.setSignature(null);

        switch (invoker){
            case BUYER:
                try{
                    PublicKey notaryPubKey = RSAKeyLoader.getPub(TestClient.baseDirGenerator() + "\\src\\main\\resources\\User" + pedido.getUserId() + ".pub");
                    return SignatureGenerator.verifySignature(notaryPubKey, signature, gson.toJson(pedido));
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            case NOTARY:
                try{
                    if(USING_CC){
                        PublicKey notaryPubKey = iCartaoCidadao.getPublicKeyFromCC();
                        return SignatureGenerator.verifySignatureCartaoCidadao(notaryPubKey, signature, gson.toJson(pedido));
                    }else {
                        PublicKey notaryPubKey = RSAKeyLoader.getPub(TestClient.baseDirGenerator() + "\\src\\main\\resources\\Notary.pub");
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

    private static String baseDirGenerator() {
        String basePath = System.getProperty("user.dir");
        if(!basePath.contains("\\Client"))
            basePath+="\\Client";
        return basePath;
    }
}
