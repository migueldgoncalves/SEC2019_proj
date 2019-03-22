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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client extends UnicastRemoteObject implements iClient {

    private static iProxy proxy = null;
    private static PrivateKey privKey;
    private static PublicKey pubKey;
    private static int UserID;

    private static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();

    public static void runButNotOn(Runnable toRun, Thread notOn) {
        if (Thread.currentThread() == notOn) {
            THREADPOOL.submit(toRun);
        } else {
            toRun.run();
        }
    }

    Client() throws RemoteException {
        super();
    }

    private static void loadKeys() {
        try {
            privKey = RSAKeyLoader.getPriv("F:\\Documentos\\GitHub\\SEC2019_proj\\Client\\src\\main\\resources\\User" + UserID + ".key");
            pubKey = RSAKeyLoader.getPub("F:\\Documentos\\GitHub\\SEC2019_proj\\Client\\src\\main\\resources\\User" + UserID + ".pub");
            System.out.println("KEYS LOADED MY DUDE");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SOMETHING WENT TO HELL WHILE LOADING THE DARNED KEYS!");
        }
    }

    public static void main(String[] args) {
        try {
            //Prompt User For Input of Port To Register
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            iClient Clientproxy = new Client();

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

            Naming.rebind("rmi://localhost:" + port + "/" + UserID, Clientproxy);

            //End Of Client Registration in RMI

            proxy = (iProxy) Naming.lookup("rmi://localhost:8086/Notary");

            System.out.println("Please Introduce User ID: ");
            String ID = reader.readLine();

            if (tryParseInt(ID)) {

                UserID = Integer.parseInt(ID);
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

    private static void sell(String data) {
        try {
            System.out.println(proxy.sell(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getStateOfGood(String data) {
        try {
            System.out.println(proxy.getStateOfGood(data));
        } catch (Exception e) {
            e.printStackTrace();
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

    //DO NOT TOUCH THIS FUCKING METHOD, DO NOT ATTEMPT TO UNDERSTAND WITHOUT PROPER ANALYSIS
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

            Gson gson = new Gson();
            String jsonInString = gson.toJson(pedido);

            pedido.setSignature(SignatureGenerator.generateSignature(privKey, jsonInString));

            String request = gson.toJson(pedido);

            System.out.println("This is the Jason Object: " + jsonInString + " THIS FUCKING PRINT IS IN INVOKE SELLER YOU FOOL");

            System.out.println(clientProxy.Buy(request));

        } catch (Exception e) {
            e.printStackTrace();
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
            Gson gson = new Gson();
            String jsonToString = gson.toJson(pedido);
            byte[] sig = SignatureGenerator.generateSignature(privKey, jsonToString);
            pedido.setSignature(sig);

            return gson.toJson(pedido);

        } catch (Exception e) {
            System.out.println("Something went wrong during prompting for Good ID");
            return null;
        }
    }

    private static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            System.out.println("The introduced Input could not be converted to an integer. Exiting...");
            return false;
        }
    }

    private static void printMenu() {
        System.out.print("Please Introduce The Desired Option Number: \n 1. Sell an Item. \n 2. Buy an Item. \n 3. Get Item State. \n Option Number: ");
    }


    public String Buy(String request) {
        try {
            //Signature Verification
            Gson gson = new Gson();
            Request received = gson.fromJson(request, Request.class);
            byte[] temp = received.getSignature();
            received.setSignature(null);
            SignatureGenerator.verifySignature(RSAKeyLoader.getPub("User" + received.getBuyerId() + ".pub"), temp, gson.toJson(received));

            //Request To Transfer Item
            received.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(received)));

            return proxy.transferGood(gson.toJson(received));
        } catch (Exception e) {
            System.out.println("Something Went Wrong During the Transfer");
            return "The Good Transfer Has Failed. Please Try Again.";
        }
    }

}
