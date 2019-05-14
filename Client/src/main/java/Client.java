import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.BindException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Client extends UnicastRemoteObject implements iClient {

    protected enum Sender {
        NOTARY, BUYER
    }

    private static PrivateKey privKey;
    private static Gson gson = new Gson();
    private static int UserID;
    private static boolean USING_CC = false;

    private static AtomicInteger writeTimeStamp = new AtomicInteger(0);
    private static AtomicInteger readTimeStamp = new AtomicInteger(0);

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
                    int writeTimeStampToSend;
                    switch (input) {
                        case "1":
                            writeTimeStampToSend = writeTimeStamp.incrementAndGet();
                            String data = promptForGoodId(writeTimeStampToSend);
                            Runnable r = () -> sell(data, writeTimeStampToSend);
                            new Thread(r).start();
                            break;
                        case "2":
                            writeTimeStampToSend = writeTimeStamp.incrementAndGet();
                            int seller = promptForSellerId();
                            int good = prompForGoodId();
                            int clientPort = promptForPortNumber();
                            Runnable r2 = () -> invokeSeller(seller, good, clientPort, writeTimeStampToSend);
                            new Thread(r2).start();
                            break;
                        case "3":
                            writeTimeStampToSend = writeTimeStamp.incrementAndGet();
                            String data3 = promptForGoodId(writeTimeStampToSend);
                            Runnable r3 = () -> getStateOfGood(data3, writeTimeStampToSend);
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

        }catch (BindException e){
            System.out.println("The Door You Tried Registering To Is Occupied By Another Process. Please Restart And Try Another Port");
            System.exit(-1);
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

    private static String promptForGoodId(int writeTimeStampToSend) {
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

    private static void getStateOfGood(String data, int writeTimeStamp) {
        try {

            ArrayList<String> answers = new ArrayList<>();
            for(Integer i : serverPorts.values()){
                iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:" + i + "/Notary");

                ExecutorService executor = Executors.newCachedThreadPool();
                Callable<Object> task = () -> {
                    try{
                        return answers.add(proxy.getStateOfGood(data));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                };
                Future<Object> future = executor.submit(task);
                try {
                    Object result = future.get(20, TimeUnit.SECONDS);
                } catch (TimeoutException ex) {
                    System.out.println("The Server Took Too Long To Answer! TimeOut Exception");
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception Found.");
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    System.out.println("Execution Exception Found.");
                    e.printStackTrace();
                } finally {
                    future.cancel(true); // may or may not desire this
                }
            }

            securityValidator(answers, writeTimeStamp);

        } catch (ConnectException e) {
            System.out.println("Could not connect to server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void invokeSeller(int sellerId, int goodId, int portNumber, int writeTimeStamp) {
        try {
            iClient clientProxy = (iClient) Naming.lookup("rmi://localhost:" + portNumber + "/" + sellerId);

            Request pedido = new Request(0, UserID, goodId, UserID, sellerId, new Date().getTime(), null, null);
            pedido.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(pedido)));

            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<Void> task = () -> {
                try{
                    ArrayList<String> answers  = clientProxy.Buy(gson.toJson(pedido));
                    securityValidator(answers, writeTimeStamp);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            };
            Future<Void> future = executor.submit(task);
            try {
                Object result = future.get(40, TimeUnit.SECONDS);
            } catch (TimeoutException ex) {
                System.out.println("The Server Took Too Long To Answer! TimeOut Exception");
            } catch (InterruptedException e) {
                System.out.println("Interrupted Exception Found.");
                e.printStackTrace();
            } catch (ExecutionException e) {
                System.out.println("Execution Exception Found.");
                e.printStackTrace();
            } finally {
                future.cancel(true); // may or may not desire this
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> Buy(String request) {
        Request received = null;
        try {
            received = gson.fromJson(request, Request.class);

            byte[] buyerSig = received.getSignature();

            if(!validateRequest(received, Sender.BUYER)){
                System.out.println("Message Has Been Tampered With!");
                return null;
            }else if(!NonceVerifier.isNonceValid(received)) {
                System.out.println("The Nounce Returned By The Client is Invalid! You Might Be Suffering From Replay Attack!");
                return null;
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

                ExecutorService executor = Executors.newCachedThreadPool();
                Request finalReceived = received;
                Callable<Boolean> task = () -> {
                    try{
                        return answers.add(proxy.transferGood(gson.toJson(finalReceived)));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                };
                Future<Boolean> future = executor.submit(task);
                try {
                    Object result = future.get(20, TimeUnit.SECONDS);
                } catch (TimeoutException ex) {
                    System.out.println("The Server Took Too Long To Answer! TimeOut Exception");
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception Found.");
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    System.out.println("Execution Exception Found.");
                    e.printStackTrace();
                } finally {
                    future.cancel(true); // may or may not desire this
                }
            }
            return answers;

        } catch (ConnectException e) {
            System.out.println("Could not connect to server on behalf of user " + received.getBuyerId());
            System.out.println("Could not connect to server");
            return null;
        } catch (Exception e) {
            System.out.println("Something Went Wrong During the Transfer");
            e.printStackTrace();
            System.out.println("The Good Transfer Has Failed. Please Try Again.");
        }

        return null;

    }

    private static void sell(String data, int writeTimeStamp) {
        try {

            //############################################### THIS BLOCK IS WORKING ######################################################

            ArrayList<String> answers = new ArrayList<>();
            Request proposition = gson.fromJson(data, Request.class);

            for (Integer i : serverPorts.values()) {
                iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:" + i + "/Notary");

                ExecutorService executor = Executors.newCachedThreadPool();
                Callable<Boolean> task = () -> {
                    try{
                        return answers.add(proxy.prepare_sell(data));
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                };
                Future<Boolean> future = executor.submit(task);
                try {
                    Object result = future.get(20, TimeUnit.SECONDS);
                } catch (TimeoutException ex) {
                    System.out.println("The Server Took Too Long To Answer! TimeOut Exception");
                } catch (InterruptedException e) {
                    System.out.println("Interrupted Exception Found.");
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    System.out.println("Execution Exception Found.");
                    e.printStackTrace();
                } finally {
                    future.cancel(true); // may or may not desire this
                }
            }

            //A partir deste momento temos todas as repostas do servidor no melhor caso possivel e podemos fazer qorum delas a ver se realmente temos um concenso

            HashMap<String, Integer> qorum = new HashMap<>();
            boolean hasQorum = false;
            String qorumWinner = null;

            //#############################################################################################################################

            //############################################### THE QORUM WORKS #############################################################

            for (String i : answers){
                if(!hasQorum && i != null) {
                    Request answer = gson.fromJson(i, Request.class);
                    if (!validateRequest(answer, Sender.NOTARY)) {
                        System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
                    } else if (!NonceVerifier.isNonceValid(answer)) {
                        System.out.println("The Nounce Returned By The Server is Invalid! You Might Be Suffering From Replay Attack!");
                        //TODO: Alterar o else if abaixo para verificar o Read ID e nao o write time stamp
                    } else if (answer.getGood().getWriteTimeStampOfGood() != writeTimeStamp - 1) {
                        System.out.println("The WriteTimeStamp Returned From The Notary Does Not Correspond To The WriteTimeStamp Expected. Byzantine Notary.");
                    } else {
                        Good goodSentFromServer = answer.getGood();
                        if (goodSentFromServer.getGoodId() == proposition.getGoodId() && goodSentFromServer.isOnSale() && answer.getNotaryId() != 0) {

                            if (qorum.get(gson.toJson(goodSentFromServer)) != null) {
                                int newValue = qorum.get(gson.toJson(goodSentFromServer)) + 1;
                                qorum.replace(gson.toJson(goodSentFromServer), newValue);
                            } else {
                                qorum.put(gson.toJson(goodSentFromServer), 1);
                            }

                            for (String x : qorum.keySet()) {
                                if (qorum.get(x) > (serverPorts.size() / 2)) {
                                    System.out.println("Qorum Achieved on Good in Method Sell");
                                    qorumWinner = x;
                                    hasQorum = true;
                                }
                            }
                        } else {
                            System.out.println("Invalid Answer Sent From The Server. One of the Expected Parameters (GoodId, isOnSale or Notary ID) has failed verification. Byzantine Attack Detected!");
                        }
                    }
                }
            }

            //#############################################################################################################################

            if(qorumWinner != null) {
                ArrayList<Request> qorumWinningAnswers = new ArrayList<>();
                Request pedido = new Request(0, UserID, proposition.getGoodId(), 0, 0, new Date().getTime(), null, null);

                for (String i : answers) {
                    Request answer = gson.fromJson(i, Request.class);
                    if (gson.toJson(answer.getGood()).equals(qorumWinner)) {
                        Good temp = answer.getGood();
                        temp.setWriteTimeStampOfGood(writeTimeStamp);
                        temp.setClientByzantineSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(answer.getGood())));
                        answer.setGood(temp);
                        qorumWinningAnswers.add(answer);
                    }
                }

                pedido.setAnswersFromNotaries(qorumWinningAnswers);
                pedido.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(pedido)));
                answers.clear();

                for (Integer i : serverPorts.values()) {
                    iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:" + i + "/Notary");

                    ExecutorService executor = Executors.newCachedThreadPool();
                    Callable<Boolean> task = () -> {
                        try {
                            return answers.add(proxy.sell(gson.toJson(pedido)));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    };
                    Future<Boolean> future = executor.submit(task);
                    try {
                        Object result = future.get(20, TimeUnit.SECONDS);
                    } catch (TimeoutException ex) {
                        System.out.println("The Server Took Too Long To Answer! TimeOut Exception");
                    } catch (InterruptedException e) {
                        System.out.println("Interrupted Exception Found.");
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        System.out.println("Execution Exception Found.");
                        e.printStackTrace();
                    } finally {
                        future.cancel(true); // may or may not desire this
                    }
                }

                securityValidator(answers, writeTimeStamp);
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
            privKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, UserID);
            //pubKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.CLIENT, UserID);
            System.out.println("Public and Private Keys Loaded");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Thrown in Body of Method loadKeys! Public and Private keys unable to load!");
        }
    }

    //########################################## Auxiliary Methods ####################################################

    private static void securityValidator(ArrayList<String> jsonAnswer, int writeTimeStamp){
        HashMap<String, Integer> qorum = new HashMap<>();
        boolean hasQorum = false;
        for (String i : jsonAnswer){
            if(!hasQorum) {
                Request answer = gson.fromJson(i, Request.class);

                if (!validateRequest(answer, Sender.NOTARY)) {
                    System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
                } else if (!NonceVerifier.isNonceValid(answer)) {
                    System.out.println("The Nounce Returned By The Server is Invalid! You Might Be Suffering From Replay Attack!");
                    //TODO: Problema aqui porque quando a resposta ao Sell e retornada o Good vem a null o que pode acontecer em varias situacoes
                }else if(answer.getGood() != null && answer.getGood().getWriteTimeStampOfGood() != writeTimeStamp){
                    System.out.println("The WriteTimeStamp Returned From The Notary Does Not Correspond To The WriteTimeStamp Expected. Byzantine Notary.");
                } else {
                    if (qorum.containsKey(answer.getAnswer())) {
                        qorum.replace(answer.getAnswer(), qorum.get(answer.getAnswer()) + 1);
                    } else {
                        qorum.put(answer.getAnswer(), 1);
                    }
                }

                for (String p : qorum.keySet()) {
                    if (qorum.get(p) > (serverPorts.size() / 2)) {
                        System.out.println("Qorum Achieved On Security Validator!");
                        System.out.println(p);
                        hasQorum = true;
                    }
                }
            }
        }
    }

    private static boolean validateRequest(Request pedido, Sender invoker) {

        //Verify Signature withing Object
        byte[] signature = pedido.getSignature();
        pedido.setSignature(null);

        switch (invoker){
            case BUYER:
                try{
                    PublicKey notaryPubKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.CLIENT, pedido.getUserId());
                    return SignatureGenerator.verifySignature(notaryPubKey, signature, gson.toJson(pedido));
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            case NOTARY:
                try{
                    PublicKey notaryPubKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, pedido.getNotaryId());
                    return SignatureGenerator.verifySignature(notaryPubKey, signature, gson.toJson(pedido));
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
        }

        return false;

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