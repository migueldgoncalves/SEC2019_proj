import AnswerClasses.*;
import RequestClasses.*;
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

    private static AtomicInteger writeTimeStamp = new AtomicInteger(0); //This value must be persisted
    private static AtomicInteger readTimeStamp = new AtomicInteger(0); //This value must be persisted

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
            //proxy = null;
            System.out.println("Acquired Network of Notaries!");

            System.out.println("Please Introduce User ID: ");
            String ID = reader.readLine();

            if (tryParseInt(ID)) {

                UserID = Integer.parseInt(ID);
                Naming.rebind("rmi://localhost:" + port + "/" + UserID, ClientProxy);
                loadTimestamps();
                loadKeys();
                IOManager.printMenu();

                String input = reader.readLine();
                while (tryParseInt(input) && !input.equals("exit")) {
                    int writeTimeStampToSend;
                    int readIdToSend;
                    switch (input) {
                        case "1":
                            writeTimeStampToSend = writeTimeStamp.incrementAndGet();
                            readIdToSend = readTimeStamp.incrementAndGet();
                            FileInterface.writeTimestamps(UserID, writeTimeStamp.get(), readTimeStamp.get());
                            String data = IOManager.promptForGoodIdSell(readIdToSend, UserID, privKey);
                            Runnable r = () -> sell(data, writeTimeStampToSend, readIdToSend);
                            new Thread(r).start();
                            break;
                        case "2":
                            writeTimeStampToSend = writeTimeStamp.incrementAndGet();
                            FileInterface.writeTimestamps(UserID, writeTimeStamp.get(), readTimeStamp.get());
                            int seller = IOManager.promptForSellerId();
                            int good = IOManager.promptForGoodId();
                            int clientPort = IOManager.promptForPortNumber();
                            Runnable r2 = () -> invokeSeller(seller, good, clientPort, writeTimeStampToSend);
                            new Thread(r2).start();
                            break;
                        case "3":
                            readIdToSend = readTimeStamp.incrementAndGet();
                            FileInterface.writeTimestamps(UserID, writeTimeStamp.get(), readTimeStamp.get());
                            String data3 = IOManager.promptForGoodIdGetState(readIdToSend, UserID, privKey);
                            Runnable r3 = () -> getStateOfGood(data3);
                            new Thread(r3).start();
                            break;
                        default:
                            System.out.println("The Introduced Input is not a valid number, please try again or type 'exit' to exit program.");
                            break;
                    }

                    IOManager.printMenu();
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



    private static void getStateOfGood(String data) {
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

            securityValidator(answers, gson.fromJson(data, GetStateRequest.class).getReadId());

        } catch (ConnectException e) {
            System.out.println("Could not connect to server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //################################ GET STATE OF GOOD WORKING PROPERLY ##############################################

    private static void invokeSeller(int sellerId, int goodId, int portNumber, int writeTimeStamp) {
        try {
            iClient clientProxy = (iClient) Naming.lookup("rmi://localhost:" + portNumber + "/" + sellerId);

            BuyerRequest pedido = new BuyerRequest();
            pedido.setGoodId(goodId);
            pedido.setWriteTimeStamp(writeTimeStamp);
            pedido.setNounce(new Date().getTime());
            pedido.setUserId(UserID);
            pedido.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(pedido)));

            byte[] sig = pedido.getSignature();
            final String[] answer = new String[1];

            ExecutorService executor = Executors.newCachedThreadPool();
            Callable<Boolean> task = () -> {
                try{
                    answer[0] = clientProxy.Buy(gson.toJson(pedido));
                    ArrayList<String> answerFromSeller = new ArrayList<>();
                    answerFromSeller.add(answer[0]);
                    //transferGoodSecurityValidator(answerFromSeller, writeTimeStamp);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            };
            Future<Boolean> future = executor.submit(task);
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

            //###################################### Qorum With Answers From Notaries ####################################

            HashMap<String, Integer> qorum = new HashMap<>();
            boolean hasQorum = false;
            String qorumWinner = null;

            assert answer[0] != null;
            BuyerAnswer buyerAnswer = gson.fromJson(answer[0], BuyerAnswer.class);

            for (PrepareTransferAnswer i : buyerAnswer.getNotaryAnswers()){
                if(i != null && !hasQorum) {
                    if (!SecurityValidator.validateNotaryAnswer(i, USING_CC)) {
                        System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
                    } else if (!NonceVerifier.isNotaryNonceValid(i.getNotaryId(), i.getNounce())) {
                        System.out.println("The Nounce Returned By The Server is Invalid! You Might Be Suffering From Replay Attack!");
                    } else {
                        Good goodSentFromServer = i.getGood();
                        if (goodSentFromServer.getGoodId() == pedido.getGoodId() && !goodSentFromServer.isOnSale() && i.getNotaryId() != 0) {

                            if (qorum.get(gson.toJson(goodSentFromServer)) != null) {
                                int newValue = qorum.get(gson.toJson(goodSentFromServer)) + 1;
                                qorum.replace(gson.toJson(goodSentFromServer), newValue);
                            } else {
                                qorum.put(gson.toJson(goodSentFromServer), 1);
                            }

                            for (String x : qorum.keySet()) {
                                if (qorum.get(x) > (serverPorts.size() / 2)) {
                                    System.out.println("Qorum Achieved on AnswerClasses.Good in Method Invoke Seller");
                                    hasQorum = true;
                                    qorumWinner = x;
                                }
                            }
                        } else {
                            System.out.println("Invalid Answer Sent From The Server. One of the Expected Parameters (GoodId, isOnSale or Notary ID) has failed verification. Byzantine Attack Detected!");
                        }
                    }
                }
            }

            //####################################### Qorum Complete With or Without Success ########################################
            buyerAnswer.setSignature(sig);
            ArrayList<String> answers = new ArrayList<>();
            if (qorumWinner != null){
                TransferGoodRequest notaryAnswers = new TransferGoodRequest();
                notaryAnswers.setBuyerAnswer(buyerAnswer);
                notaryAnswers.setWriteTimeStamp(writeTimeStamp);
                notaryAnswers.setNounce(new Date().getTime());
                notaryAnswers.setUserId(UserID);
                notaryAnswers.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(notaryAnswers)));

                for (int i : serverPorts.values()){
                    iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:" + i + "/Notary");

                    ExecutorService executor2 = Executors.newCachedThreadPool();
                    Callable<Boolean> task2 = () -> {
                        try{
                            return answers.add(proxy.transferGood(gson.toJson(notaryAnswers)));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    };
                    Future<Boolean> future2 = executor2.submit(task2);
                    try {
                        Object result = future2.get(20, TimeUnit.SECONDS);
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
                hasQorum = false;
                for (String i : answers){
                    if (i != null && !hasQorum){
                        //Esta resposta esta a vir a null por alguma razao
                        TransferGoodAnswer transferGoodAnswer = gson.fromJson(i, TransferGoodAnswer.class);

                        if (!SecurityValidator.validateNotaryAnswer(transferGoodAnswer, USING_CC)) {
                            System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
                        } else if (!NonceVerifier.isNotaryNonceValid(transferGoodAnswer.getNotaryId(), transferGoodAnswer.getNounce())) {
                            System.out.println("The Nounce Returned By The Server is Invalid! You Might Be Suffering From Replay Attack!");
                        } else {
                            if (transferGoodAnswer.getGood().getWriteTimeStampOfGood() == writeTimeStamp && transferGoodAnswer.getNotaryId() != 0) {

                                if (qorum.get(gson.toJson(transferGoodAnswer.getAnswer())) != null) {
                                    int newValue = qorum.get(gson.toJson(transferGoodAnswer.getAnswer())) + 1;
                                    qorum.replace(gson.toJson(transferGoodAnswer.getAnswer()), newValue);
                                } else {
                                    qorum.put(gson.toJson(transferGoodAnswer.getAnswer()), 1);
                                }

                                for (String x : qorum.keySet()) {
                                    if (qorum.get(x) > (serverPorts.size() / 2)) {
                                        System.out.println("Qorum Achieved on AnswerClasses.Good in Method Invoke Seller For Transfer AnswerClasses.Good");
                                        hasQorum = true;
                                        qorumWinner = x;
                                    }
                                }
                            } else {
                                System.out.println("Invalid Answer Sent From The Server. One of the Expected Parameters (GoodId, isOnSale or Notary ID) has failed verification. Byzantine Attack Detected!");
                            }
                        }
                    }
                }

                if(qorumWinner != null){
                    System.out.println(gson.fromJson(qorumWinner, TransferGoodAnswer.class).getAnswer());
                }else {
                    System.out.println("Quorum Not Achieved");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String Buy(String request) {
        BuyerRequest received = null;
        try {
            received = gson.fromJson(request, BuyerRequest.class);

            byte[] buyerSig = received.getSignature();

            if(!SecurityValidator.validateClientRequest(received)){
                System.out.println("Message Has Been Tampered With!");
                return null;
            }else if(!NonceVerifier.isClientNonceValid(received.getUserId(), received.getNounce())) {
                System.out.println("The Nounce Returned By The Client is Invalid! You Might Be Suffering From Replay Attack!");
                return null;
            }

            //Request To Transfer Item
            received.setSignature(buyerSig);
            int readId = readTimeStamp.incrementAndGet();
            PrepareTransferRequest notaryRequest = new PrepareTransferRequest();
            notaryRequest.setBuyerRequest(received);
            notaryRequest.setReadId(readId);
            notaryRequest.setNounce(new Date().getTime());
            notaryRequest.setUserId(UserID);
            notaryRequest.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(notaryRequest)));

            ArrayList<String> answers = new ArrayList<>();

            for(Integer i : serverPorts.values()) {
                iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:" + i + "/Notary");

                ExecutorService executor = Executors.newCachedThreadPool();
                Callable<Boolean> task = () -> {
                    try{
                        return answers.add(proxy.prepare_transferGood(gson.toJson(notaryRequest)));
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

            //######################################### TIME TO QORUM SERVER ANSWERS #######################################

            HashMap<String, Integer> qorum = new HashMap<>();
            boolean hasQorum = false;
            String qorumWinner = null;

            for (String i : answers){
                if(!hasQorum && i != null) {
                    PrepareTransferAnswer answer = gson.fromJson(i, PrepareTransferAnswer.class);

                    if (!SecurityValidator.validateNotaryAnswer(answer, USING_CC)) {
                        System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
                    } else if (!NonceVerifier.isNotaryNonceValid(answer.getNotaryId(), answer.getNounce())) {
                        System.out.println("The Nounce Returned By The Server is Invalid! You Might Be Suffering From Replay Attack!");
                    } else if (answer.getReadId() != readId) {
                        System.out.println("This Is The Answer Read ID: " + answer.getReadId());
                        System.out.println("This Is The Client Read ID: " + readId);
                        System.out.println("The Read ID Returned From The Notary Does Not Correspond To The Read ID Expected. Byzantine Notary.");
                    } else {
                        Good goodSentFromServer = answer.getGood();
                        if (goodSentFromServer.getGoodId() == answer.getGood().getGoodId() && !goodSentFromServer.isOnSale() && answer.getNotaryId() != 0) {

                            if (qorum.get(gson.toJson(goodSentFromServer)) != null) {
                                int newValue = qorum.get(gson.toJson(goodSentFromServer)) + 1;
                                qorum.replace(gson.toJson(goodSentFromServer), newValue);
                            } else {
                                qorum.put(gson.toJson(goodSentFromServer), 1);
                            }

                            for (String x : qorum.keySet()) {
                                if (qorum.get(x) > (serverPorts.size() / 2)) {
                                    System.out.println("Qorum Achieved on AnswerClasses.Good in Method Sell");
                                    hasQorum = true;
                                    qorumWinner = x;
                                }
                            }
                        } else {
                            System.out.println("Invalid Answer Sent From The Server. One of the Expected Parameters (GoodId, isOnSale or Notary ID) has failed verification. Byzantine Attack Detected!");
                        }
                    }

                }
            }

            if(hasQorum){
                ArrayList<PrepareTransferAnswer> qorumWinningAnswers = new ArrayList<>();
                BuyerAnswer buyerAnswer = new BuyerAnswer();

                for (String i : answers) {
                    PrepareTransferAnswer answer = gson.fromJson(i, PrepareTransferAnswer.class);
                    if (gson.toJson(answer.getGood()).equals(qorumWinner)) {
                        qorumWinningAnswers.add(answer);
                    }
                }

                buyerAnswer.setNotaryAnswers(qorumWinningAnswers);
                buyerAnswer.setNounce(new Date().getTime());
                buyerAnswer.setUserId(UserID);
                buyerAnswer.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(buyerAnswer)));

                return gson.toJson(buyerAnswer);
            }

            return null;

        } catch (ConnectException e) {
            System.out.println("Could not connect to server on behalf of user " + received.getUserId());
            System.out.println("Could not connect to server");
            return null;
        } catch (Exception e) {
            System.out.println("Something Went Wrong During the Transfer");
            e.printStackTrace();
            System.out.println("The AnswerClasses.Good Transfer Has Failed. Please Try Again.");
        }

        return null;

    }

    private static void sell(String data, int writeTimeStamp, int readId) {
        try {

            //############################################### THIS BLOCK IS WORKING ######################################################

            ArrayList<String> answers = new ArrayList<>();
            PrepareSellRequest proposition = gson.fromJson(data, PrepareSellRequest.class);

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
                    Object result = future.get(8888, TimeUnit.SECONDS);
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
                    PrepareSellAnswer answer = gson.fromJson(i, PrepareSellAnswer.class);
                    if (!SecurityValidator.validateNotaryAnswer(answer, USING_CC)) {
                        System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
                    } else if (!NonceVerifier.isNotaryNonceValid(answer.getNotaryId(), answer.getNounce())) {
                        System.out.println("The Nounce Returned By The Server is Invalid! You Might Be Suffering From Replay Attack!");
                    } else if (answer.getReadId() != readId) {
                        System.out.println("This Is The Answer Read ID: " + answer.getReadId());
                        System.out.println("This Is The Client Read ID: " + readId);
                        System.out.println("The Read ID Returned From The Notary Does Not Correspond To The Read ID Expected. Byzantine Notary.");
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
                                    System.out.println("Qorum Achieved on AnswerClasses.Good in Method Sell");
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

            //##########################################################################################################################################################################

            if(qorumWinner != null) {
                ArrayList<PrepareSellAnswer> qorumWinningAnswers = new ArrayList<>();
                SellRequest pedido = new SellRequest();

                for (String i : answers) {
                    PrepareSellAnswer answer = gson.fromJson(i, PrepareSellAnswer.class);
                    if (gson.toJson(answer.getGood()).equals(qorumWinner)) {
                        Good temp = answer.getGood();
                        temp.setClientByzantineSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(answer.getGood())));
                        answer.setGood(temp);
                        qorumWinningAnswers.add(answer);
                    }
                }

                pedido.setRequests(qorumWinningAnswers);
                pedido.setUserId(UserID);
                pedido.setNounce(new Date().getTime());
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
                        Object result = future.get(8888, TimeUnit.SECONDS);
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

                sellSecurityValidator(answers, writeTimeStamp);
                answers.clear();
            }

        } catch (ConnectException e) {
            System.out.println("Could not connect to server");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //###################################### User Prompts For Input ################################################



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
            System.out.println("Public and Private Keys Loaded");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Thrown in Body of Method loadKeys! Public and Private keys unable to load!");
        }
    }

    private static void loadTimestamps() {
        int[] timestamps = FileInterface.readTimestamps(UserID);
        Client.writeTimeStamp = new AtomicInteger(timestamps[0]);
        Client.readTimeStamp = new AtomicInteger(timestamps[1]);
        System.out.println("Timestamps loaded");
    }

    //########################################## Auxiliary Methods ####################################################

    private static void securityValidator(ArrayList<String> jsonAnswer, int readId){
        HashMap<String, Integer> qorum = new HashMap<>();
        boolean hasQorum = false;
        for (String i : jsonAnswer){
            if(!hasQorum) {
                GetStateAnswer answer = gson.fromJson(i, GetStateAnswer.class);

                if (!SecurityValidator.validateNotaryAnswer(answer, USING_CC)) {
                    System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
                } else if (!NonceVerifier.isNotaryNonceValid(answer.getNotaryId(), answer.getNounce())) {
                    System.out.println("The Nounce Returned By The Server is Invalid! You Might Be Suffering From Replay Attack!");
                    //TODO: Problema aqui porque quando a resposta ao Sell e retornada o Good vem a null o que pode acontecer em varias situacoes
                }else if(answer.getAnswer() == null || answer.getReadId() != readId){
                    System.out.println("The Read ID Returned From The Notary Does Not Correspond To The Read ID Expected. Byzantine Notary.");
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

    private static void sellSecurityValidator(ArrayList<String> jsonAnswer, int writeTimeStamp){
        HashMap<String, Integer> qorum = new HashMap<>();
        boolean hasQorum = false;
        for (String i : jsonAnswer){
            if(!hasQorum) {
                SellAnswer answer = gson.fromJson(i, SellAnswer.class);

                if (!SecurityValidator.validateNotaryAnswer(answer, USING_CC)) {
                    System.out.println("The Signature of The Message is Invalid. Message Has Been Tampered With");
                } else if (!NonceVerifier.isNotaryNonceValid(answer.getNotaryId(), answer.getNounce())) {
                    System.out.println("The Nounce Returned By The Server is Invalid! You Might Be Suffering From Replay Attack!");
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

}