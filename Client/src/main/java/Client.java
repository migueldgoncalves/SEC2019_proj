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
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class Client extends UnicastRemoteObject implements iClient {

    private static PrivateKey privKey;
    private static Gson gson = new Gson();
    private static int UserID;
    private static boolean USING_CC = false;

    private static ConcurrentHashMap<Integer, Integer> serverPorts = new ConcurrentHashMap<>();

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

            //TODO: Tratar excecao de porta ocupada e voltar a pedir porta para tentar novamente numa outra em vez de se rebentar com o cliente
            LocateRegistry.createRegistry(portNumber);

            //End Of Client Registration in RMI
            System.out.println("Please Introduce The Port of a Well Known Server:");
            System.out.print("Port Number:");
            String serverPort = reader.readLine();

            while (!tryParseInt(serverPort)){
                System.out.println("Introduce a valid Port Number:");
                System.out.print("Port Number: ");
                serverPort = reader.readLine();
            }
            int wellKnownPort = Integer.parseInt(serverPort);

            iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:" + wellKnownPort + "/Notary");

            System.out.println("Acquiring Network of Notaries...");
            serverPorts = proxy.getNetworkOfNotaries();
            proxy = null;
            System.out.println("Acquired Network of Notaries!");

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

            Request pedido = new Request(0, UserID, Integer.parseInt(input), 0, 0, new Date().getTime(), null, null);
            pedido.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(pedido)));

            return gson.toJson(pedido);

        } catch (Exception e) {
            System.out.println("Something went wrong during prompting for Good ID");
            e.printStackTrace();
            return null;
        }
    }

    private static void getStateOfGood(String data) {
        try {

            ArrayList<String> answers = new ArrayList<>();
            for(Integer i : serverPorts.values()){
                iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:" + i + "/Notary");
                answers.add(proxy.getStateOfGood(data));
            }

            for(String jsonAnswer : answers){
                securityValidator(jsonAnswer);
            }

        } catch (ConnectException e) {
            System.out.println("Could not connect to server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void invokeSeller(int sellerId, int goodId, int portNumber) {
        try {
            iClient clientProxy = (iClient) Naming.lookup("rmi://localhost:" + portNumber + "/" + sellerId);

            Request pedido = new Request(0, UserID, goodId, UserID, sellerId, new Date().getTime(), null, null);
            pedido.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(pedido)));

            securityValidator(clientProxy.Buy(gson.toJson(pedido)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sell(String data) {
        try {
            ArrayList<String> answers = new ArrayList<>();
            for(Integer i : serverPorts.values()){
                iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:" + i + "/Notary");
                answers.add(proxy.sell(data));
            }

            for(String jsonAnswer : answers) {
                securityValidator(jsonAnswer);
            }

        } catch (ConnectException e) {
            System.out.println("Could not connect to server");
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
            privKey = RSAKeyLoader.getPriv( Client.baseDirGenerator() + "\\src\\main\\resources\\User" + UserID + ".key");
            //pubKey = RSAKeyLoader.getPub(Client.baseDirGenerator() + "\\src\\main\\resources\\User" + UserID + ".pub");
            System.out.println("Public and Private Keys Loaded");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Thrown in Body of Method loadKeys! Public and Private keys unable to load!");
        }
    }

    //########################################## Auxiliary Methods ####################################################

    private static void securityValidator(String jsonAnswer){
        Request answer = gson.fromJson(jsonAnswer, Request.class);

        if (!validateRequest(answer, Sender.NOTARY)) {
            System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
        } else if (!NonceVerifier.isNonceValid(answer)) {
            System.out.println("The Nounce Returned By The Server is Invalid! You Might Be Suffering From Replay Attack!");
        } else {
            System.out.println(answer.getAnswer());
        }
    }

    public String Buy(String request) {
        Request received = null;
        try {
            received = gson.fromJson(request, Request.class);

            byte[] buyerSig = received.getSignature();

            if(!validateRequest(received, Sender.BUYER)){
                return "Message Has Been Tampered With!";
            }else if(!NonceVerifier.isNonceValid(received)) {
                return "The Nounce Returned By The Client is Invalid! You Might Be Suffering From Replay Attack!";
            }

            //Request To Transfer Item
            received.setUserId(UserID);
            received.setBuyerSignature(buyerSig);
            received.setBuyerNounce(received.getNounce());
            received.setNounce(new Date().getTime());
            received.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(received)));

            ArrayList<String> answers = new ArrayList<>();

            for(Integer i : serverPorts.values()) {
                iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:" + i + "/Notary");
                answers.add(proxy.transferGood(gson.toJson(received)));
            }
            //ISTO TEM MESMO DE SER VISTO E ALTERADO
            return answers.get(0);

        } catch (ConnectException e) {
            System.out.println("Could not connect to server on behalf of user " + received.getBuyerId());
            return "Could not connect to server";
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
                    PublicKey notaryPubKey = RSAKeyLoader.getPub(Client.baseDirGenerator() + "\\src\\main\\resources\\User" + pedido.getUserId() + ".pub");
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
                        PublicKey notaryPubKey = RSAKeyLoader.getPub(Client.baseDirGenerator() + "\\src\\main\\resources\\Notary.pub");
                        return SignatureGenerator.verifySignature(notaryPubKey, signature, gson.toJson(pedido));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
        }

        return false;

    }

    protected enum Sender {
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