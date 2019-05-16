import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

public class Server extends UnicastRemoteObject implements iProxy {

    private static final int HASHCASH_VERSION = 1;
    private static final int ZERO_BITS = 20;

    //The Hash Table mapping each user ID to their owned Goods
    private Hashtable<Integer, ArrayList<Good>> goods = new Hashtable<>();
    //The Hash Table mapping each user ID to their public keys
    private Hashtable<Integer, PublicKey> publicKeys = new Hashtable<>();
    int PORT;
    private PrivateKey privKey;
    private PublicKey pubKey;
    private boolean USING_CC = false;
    //The Concurrent HashMap containing the IDs of each notary and corresponding ports in the system
    private ConcurrentHashMap<Integer, Integer> serverPorts = new ConcurrentHashMap<>();
    //The Concurrent Hashmap containing the writeTimeStamps for every user
    private ConcurrentHashMap<Integer, Integer> usersWriteTimeStamps = new ConcurrentHashMap<>();
    private int ID;

    /**
     * Enum used for the Log Update Operations
     */
    private enum OPCODE {
        TRANSFERGOOD, SELLGOOD, GETSTATEOFGOOD
    }

    public Server() throws RemoteException{

    }

    /**
     * The Server Constructor used for test reasons. This method was implemented to be called during
     * test phase of the program in order to verify the correct behaviour of the system
     * @param FilePath The path of the file that contains the goods that will be loaded into the server
     */
    public Server(String FilePath) throws RemoteException {
        super();
        try {
            FileReader fileReader = new FileReader();
            goods = (Hashtable) fileReader.goodsListConstructor(FilePath);

            // TODO Allow create test notaries with different ids
            ID = 1;

            KeyStoreInterface.createBaseKeyStore(); //Setting up key store and populating it with client keys if first server
            KeyStoreInterface.addNotaryKeysToKeyStore(ID, USING_CC);

            //Loading client public keys in memory
            for (int i = 1; i <= 9; i++) {
                publicKeys.put(i, (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.CLIENT, i));
            }

            //Loading server keys in memory
            privKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, ID);
            pubKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, ID);

            System.out.println(publicKeys.size() + " Keys Have Been Loaded Into The Notary");
        } catch (Exception e) {
            System.out.println("Something Went Wrong");
            e.printStackTrace();
        }
    }

    /**
     * Default server constructor used during actual program execution
     */
    Server(int mode) throws RemoteException {
        super();
        try {
            FileReader fileReader = new FileReader();
            goods = (Hashtable) fileReader.goodsListConstructor( baseDirGenerator() + "\\src\\main\\resources\\GoodsFile1.xml");

            KeyStoreInterface.createBaseKeyStore(); //Setting up key store and populating it with client keys if first server

            Gson gson = new Gson();
            for(Integer uid : goods.keySet()){
                for(Good i : goods.get(uid)){
                    i.setWriteTimeStampOfGood(0);
                    //Initial system state - Goods already have an initial client signature
                    i.setClientByzantineSignature(SignatureGenerator.generateSignature((PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.CLIENT, uid), gson.toJson(i)));
                    usersWriteTimeStamps.put(uid, 0);
                }
            }

            //Loading client public keys in memory
            for (int i = 1; i <= 9; i++) {
                publicKeys.put(i, (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.CLIENT, i));
            }

            System.out.println(publicKeys.size() + " Keys Have Been Loaded Into The Notary!");

            //Prompt User To Decide Whether To Use Or Not Citizen Card
            System.out.println("Please Choose One Of The Following Options:");
            System.out.println("1. Run With Citizen Card.");
            System.out.println("2. Run Without Citizen Card");
            System.out.println("3. Multiple Nodes Running With Citizen Card");
            System.out.println("4. Multiple Nodes Running Without Citizen Card");
            System.out.print("Option:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            switch (reader.readLine()){
                case "1":
                    USING_CC = true;
                    ID = 1;
                    KeyStoreInterface.addNotaryKeysToKeyStore(ID, USING_CC);
                    break;
                case "2":
                    USING_CC = false;
                    ID = 1;
                    KeyStoreInterface.addNotaryKeysToKeyStore(ID, USING_CC);
                    privKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, ID);
                    pubKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, ID);
                    break;
                case "3":
                    USING_CC = true;
                    System.out.println("Is It The First Node? \n 1. Yes \n 2. No");
                    System.out.print("Option Number:");
                    switch (reader.readLine()){
                        case "1":
                            this.PORT = 8086;
                            this.ID = 1;
                            KeyStoreInterface.addNotaryKeysToKeyStore(ID, USING_CC);
                            serverPorts.put(ID, PORT);
                            break;
                        case "2":
                            initialSetup(); //Notary can only create its keys after knowing its ID
                            break;
                    }
                    break;
                case "4":
                    USING_CC = false;
                    System.out.println("Is It The First Node? \n 1. Yes \n 2. No");
                    System.out.print("Option Number:");
                    switch (reader.readLine()){
                        case "1":
                            this.PORT = 8086;
                            this.ID = 1;
                            KeyStoreInterface.addNotaryKeysToKeyStore(ID, USING_CC);
                            serverPorts.put(ID, PORT);
                            break;
                        case "2":
                            initialSetup(); //Notary can only create its keys after knowing its ID
                            break;
                    }
                    privKey = (PrivateKey) KeyStoreInterface.getPrivateKeyFromKeyStore(KeyStoreInterface.NOTARY, ID);
                    pubKey = (PublicKey) KeyStoreInterface.getPublicKeyFromKeyStore(KeyStoreInterface.NOTARY, ID);
                    break;
                default:
                    System.out.println("Invalid Option. Exiting...");
                    System.exit(-1);
                    break;
            }


        } catch (Exception e) {
            System.out.println("An Exception Was Thrown In Server Constructor!");
            e.printStackTrace();
        }

        if (goods.size() == 0) {
            System.out.println("WARNING: No Goods Were Loaded Into The Notary!");
        } else {
            System.out.println("All Goods Were Loaded! With a Total Of " + goods.size() + " Users");
        }
    }

    //####################################### Main Methods #############################################################

    /**
     * This method is responsible for returning the state of a requested good
     * @param jsonRequest The Request Object that contains the Parameters to validate request (Signature, Good ID, etc...)
     */
    public String getStateOfGood(String jsonRequest) throws RemoteException {
        Gson gson = new Gson();
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        if (pedido.getNotaryId()!=0) {
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "As a Notary, you cannot invoke this method!");
            return answerFactory("As a Notary, you cannot invoke this method!", pedido.getGood().getWriteTimeStampOfGood());
        }

        if (!NonceVerifier.isNonceValid(pedido)){
            updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "This message has already been processed!");
            return answerFactory("This message has already been processed!", pedido.getGood().getWriteTimeStampOfGood());
        }

        if (!validateRequest(pedido)) {
            updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "Invalid Authorization to Invoke Method Get State Of Good in Server!");
            return answerFactory("Invalid Authorization to Invoke Method Get State Of Good in Server!", pedido.getGood().getWriteTimeStampOfGood());
        }

        if(!validateWriteTimeStamp(pedido)){
            updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "Invalid WriteTimeStamp to Invoke Method Get State Of Good in Server!");
            return answerFactory("Invalid WriteTimeStamp to Invoke Method Get State Of Good in Server!", pedido.getGood().getWriteTimeStampOfGood());
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId()) {
                    updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "<" + i.getOwnerId() + ", " + (i.isOnSale() ? "On-Sale>" : "Not-On-Sale>"));
                    return answerFactory("<" + i.getOwnerId() + ", " + (i.isOnSale() ? "On-Sale>" : "Not-On-Sale>"), pedido.getGood().getWriteTimeStampOfGood());
                }
            }
        }
        updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "The GoodId " + pedido.getGoodId() + " Is Not Present In The Server!");
        return answerFactory("The GoodId " + pedido.getGoodId() + " Is Not Present In The Server!", pedido.getGood().getWriteTimeStampOfGood());
    }

    /**
     * This Method is responsible for transfering a good that is on sale from one user (Seller) to another (Buyer)
     * @param jsonRequest The Request Object containing all necessary data
     */
    public String transferGood(String jsonRequest) throws RemoteException {
        Gson gson = new Gson();
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        byte[] buyerSig = pedido.getBuyerSignature();
        byte[] sellerSig = pedido.getSignature();

        if (pedido.getNotaryId()!=0) {
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "As a Notary, you cannot invoke this method!");
            return answerFactory("As a Notary, you cannot invoke this method!", pedido.getGood().getWriteTimeStampOfGood());
        }

        if (!NonceVerifier.isNonceValid(pedido)){
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "This message has already been processed");
            return answerFactory("This message has already been processed", pedido.getGood().getWriteTimeStampOfGood());
        }

        if (!validateRequest(pedido)) {
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "Invalid Authorization to Transfer Good!");
            return answerFactory("Invalid Authorization to Transfer Good!", pedido.getGood().getWriteTimeStampOfGood());
        }

        if(!validateWriteTimeStamp(pedido)){
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "Invalid WriteTimeStamp to Transfer Good!");
            return answerFactory("Invalid WriteTimeStamp to Transfer Good!", pedido.getGood().getWriteTimeStampOfGood());
        }

        //Alter Request So It Matches The One Sent By The Buyer
        pedido.setUserId(pedido.getBuyerId());
        pedido.setNounce(pedido.getBuyerNounce());
        pedido.setBuyerNounce(0);
        pedido.setSignature(null);
        pedido.setBuyerSignature(null);
        //######################################################

        if(!(SignatureGenerator.verifySignature(publicKeys.get(pedido.getBuyerId()), buyerSig, gson.toJson(pedido)))){
            pedido.setSignature(sellerSig);
            pedido.setBuyerSignature(buyerSig);
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "Invalid Authorization to Transfer Good! Buyer Did Not Request To Purchase This Item");
            return answerFactory("Invalid Authorization to Transfer Good! Buyer Did Not Request To Purchase This Item", pedido.getGood().getWriteTimeStampOfGood());
        }

        pedido.setSignature(sellerSig);
        pedido.setBuyerSignature(buyerSig);

        if (!NonceVerifier.isNonceValid(pedido)){
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "This message from Buyer has already been processed");
            return answerFactory("This message from Buyer has already been processed", pedido.getGood().getWriteTimeStampOfGood());
        }

        if (pedido.getBuyerId() < 1 || pedido.getBuyerId() > 9) {
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "The Good Id, Owner Id or New Owner ID is not present in the server!");
            return answerFactory("The Good Id, Owner Id or New Owner ID is not present in the server!", pedido.getGood().getWriteTimeStampOfGood());
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId() && i.getOwnerId() == pedido.getSellerId() && i.isOnSale()) {
                    synchronized (i) {
                        if (i.getGoodId() == pedido.getGoodId() && i.getOwnerId() == pedido.getSellerId() && i.isOnSale()){
                            Good newOwner = new Good(pedido.getBuyerId(), i.getGoodId(), i.getName(), !i.isOnSale());
                            temp.set(temp.indexOf(i), newOwner);
                            saveServerState();
                            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "The Good with Good ID " + i.getGoodId() + " Has now Been transfered to the new Owner with Owner ID " + pedido.getBuyerId());
                            return answerFactory("The Good with Good ID " + i.getGoodId() + " Has now Been transfered to the new Owner with Owner ID " + pedido.getBuyerId(), pedido.getGood().getWriteTimeStampOfGood());
                        }else {
                            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "The Item was already Sold, Does not Exist or Is not On Sale");
                            return answerFactory("The Item was already Sold, Does not Exist or Is not On Sale", pedido.getGood().getWriteTimeStampOfGood());
                        }
                    }
                }
            }
        }
        updateServerLog(OPCODE.TRANSFERGOOD, pedido, "The Good Id, Owner Id or New Owner ID is not present in the server!");
        return answerFactory("The Good Id, Owner Id or New Owner ID is not present in the server!", pedido.getGood().getWriteTimeStampOfGood());
    }

    /**
     * Method Sell that is responsible for putting a given Good on sale
     * @param jsonRequest The Request Object that contains the Good ID to be put on sell
     */
    public String sell(String jsonRequest) throws RemoteException {
        Gson gson = new Gson();
        //Convert to Request Object
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        if (pedido.getNotaryId()!=0) {
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "As a Notary, you cannot invoke this method!");
            return answerFactory("As a Notary, you cannot invoke this method!", pedido.getGood().getWriteTimeStampOfGood());
        }

        //TODO: VALIDAR CADA REQUEST NO ARRAY DE REQUESTS RECEBIDOS DO CLIENTE AO INVOCAR ESTE METODO

        //Verify Signature withing Object
        //TODO: REFATORIZAR O VALIDATE REQUEST DADO QUE A VERIFICACAO DE ASSINATURA TEM DE SER DINAMICA CONSOANTE O ID DO NOTARIO
        if (!validateRequest(pedido)) {
            updateServerLog(OPCODE.SELLGOOD, pedido, "Invalid Authorization To Invoke Method Sell on Server!");
            return answerFactory("Invalid Authorization To Invoke Method Sell on Server!", pedido.getAnswersFromNotaries().get(0).getGood().getWriteTimeStampOfGood());
        }

        //Replay Attack Prevention
        if (!NonceVerifier.isNonceValid(pedido)){
            updateServerLog(OPCODE.SELLGOOD, pedido, "This message has already been processed by The Server!");
            return answerFactory("This message has already been processed by The Server!", pedido.getAnswersFromNotaries().get(0).getGood().getWriteTimeStampOfGood());
        }

        //TODO: VERIFICAR ISTO EM TODO O ESPETRO DO METODO SELL E PREPARE TO SELL
        if(!validateWriteTimeStamp(pedido)){
            updateServerLog(OPCODE.SELLGOOD, pedido, "Invalid WriteTimeStamp To Invoke Method Sell on Server!");
            return answerFactory("Invalid WriteTimeStamp To Invoke Method Sell on Server!", pedido.getAnswersFromNotaries().get(0).getGood().getWriteTimeStampOfGood());
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getAnswersFromNotaries().get(0).getGood().getGoodId() && i.getOwnerId() == pedido.getUserId()) {
                    if (!i.isOnSale()) {
                        i.setOnSale(true);
                        i.setWriteTimeStampOfGood(pedido.getAnswersFromNotaries().get(0).getGood().getWriteTimeStampOfGood());
                        saveServerState();
                        updateServerLog(OPCODE.SELLGOOD, pedido, "The Item is Now on Sale");
                        return answerFactory("The Item is Now on Sale", pedido.getAnswersFromNotaries().get(0).getGood().getWriteTimeStampOfGood());
                    } else {
                        updateServerLog(OPCODE.SELLGOOD, pedido, "The Item was Already On Sale");
                        i.setWriteTimeStampOfGood(pedido.getAnswersFromNotaries().get(0).getGood().getWriteTimeStampOfGood());
                        return answerFactory("The Item was Already On Sale", pedido.getAnswersFromNotaries().get(0).getGood().getWriteTimeStampOfGood());
                    }
                }
            }
        }
        updateServerLog(OPCODE.SELLGOOD, pedido, "The Requested Item To Be Put on Sell Is Not Available In The System");
        return answerFactory("The Requested Item To Be Put on Sell Is Not Available In The System", pedido.getAnswersFromNotaries().get(0).getGood().getWriteTimeStampOfGood());
    }

    public String prepare_sell(String request) throws RemoteException {
        Gson gson = new Gson();
        //Convert to Request Object
        Request pedido = gson.fromJson(request, Request.class);

        //Replay Attack Prevention
        if (!NonceVerifier.isNonceValid(pedido)){
            updateServerLog(OPCODE.SELLGOOD, pedido, "This message has already been processed by The Server!");
            return answerFactory("This message has already been processed by The Server!", pedido.getGood().getWriteTimeStampOfGood());
        }

        //Verify Signature withing Object
        if (!validateRequest(pedido)) {
            updateServerLog(OPCODE.SELLGOOD, pedido, "Invalid Authorization To Invoke Method Sell on Server!");
            return answerFactory("Invalid Authorization To Invoke Method Sell on Server!", pedido.getGood().getWriteTimeStampOfGood());
        }

        //TODO: VALIDATE READ ID INSTEAD OF WRITE TIME STAMP
//        if(!validateWriteTimeStamp(pedido)){
//            updateServerLog(OPCODE.SELLGOOD, pedido, "Invalid WriteTimeStamp To Invoke Method Sell on Server!");
//            return answerFactory("Invalid WriteTimeStamp To Invoke Method Sell on Server!", pedido.getGood().getWriteTimeStampOfGood());
//        }

        for(Good i : goods.get(pedido.getUserId())){
            if(i.getGoodId() == pedido.getGoodId() && i.getOwnerId() == pedido.getUserId()){
                Good modifiedGood = new Good(i.getOwnerId(), i.getGoodId(), i.getName(), true);
                modifiedGood.setClientByzantineSignature(i.getClientByzantineSignature());
                modifiedGood.setWriteTimeStampOfGood(i.getWriteTimeStampOfGood());

                Request answer = new Request();
                answer.setNotaryId(ID);
                answer.setNounce(new Date().getTime());
                answer.setGood(modifiedGood);

                if(USING_CC){
                    answer.setSignature(iCartaoCidadao.sign(gson.toJson(answer)));
                }else {
                    answer.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(answer)));
                }

                return gson.toJson(answer);

            }else {
                return null;
            }
        }

        return null;

    }

    public String prepare_transferGood(String request) throws RemoteException {
        return null;
    }

    //####################################### Server State Methods #####################################################


    /**
     * Method The recovers a Server state (If a previous state exists in the directory)
     */
    protected synchronized void getSystemState() {
        Gson gson = new Gson();
        String finalBackupPath = getBackupPaths()[0];
        if (isBackupFileCreatedAndNotEmpty()) {
            try {
                String jsonString = FileUtils.readFileToString(new File(finalBackupPath), "UTF-8");
                jsonString = jsonString.replace("\n", "").replace("\r", "");
                Server temp = gson.fromJson(jsonString, Server.class);
                if(this.goods.size() <= temp.goods.size())
                    this.goods = temp.goods;
                System.out.println("Recovered Server State");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Backup file found is unreadable - Creating a new one");
                saveServerState();
            }
        } else {
            System.out.println("No written backup file found - Creating a new one");
            saveServerState();
        }
    }

    /**
     * Method That Atomically Saves The Server State to a given path
     */
    private synchronized void saveServerState() {
        Gson gson = new Gson();
        String finalBackupPath = getBackupPaths()[0];
        String temporaryBackupPath = getBackupPaths()[1];
        try {
            PrintWriter writer = new PrintWriter(new File(temporaryBackupPath));
            Server simplified = new Server();
            simplified.goods = this.goods;
            simplified.publicKeys = null;
            simplified.privKey = null;
            writer.println(gson.toJson(simplified));
            writer.close();
        } catch (Exception e) {
            System.out.println("A Crash Occurred During System Save State.");
            e.printStackTrace();
            return;
        }

        try {
            Files.move(Paths.get(temporaryBackupPath), Paths.get(finalBackupPath), ATOMIC_MOVE);
        } catch (AccessDeniedException e) {
            System.out.println("Run as Administrator!");
        } catch (Exception e) {
            System.out.println("An error occurred during system save!");
            e.printStackTrace();
        }

    }

    //########################################## Auxiliary Methods ####################################################

    private synchronized boolean synchronizeServerStatus(int userId, int writeTimeStamp){
        try{
            if(!(usersWriteTimeStamps.get(userId) >= writeTimeStamp) || !(writeTimeStamp == (usersWriteTimeStamps.get(userId) + 1))){

                ArrayList<String> answers = new ArrayList<>();

                for(int port : serverPorts.values()){
                    if(port != PORT){
                        iProxy notary = (iProxy) Naming.lookup("rmi://localhost:" + port + "/Notary");

                        ExecutorService executor = Executors.newCachedThreadPool();
                        Callable<Boolean> task = () -> {
                            try{
                                return answers.add(notary.getServerStatus(userId));
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
                }

                HashMap<String, Integer> qorum = new HashMap<>();
                Gson gson  = new Gson();
                for(String answer : answers){
                        UpdateRequest updateRequest = gson.fromJson(answer, UpdateRequest.class);
                        //HashMap Containing WriteTimeStamp and Arraylist of Goods of a certain user
                        HashMap<Integer, ArrayList<Good>> wtss = updateRequest.getPairs();
                        String jsonString = gson.toJson(wtss);
                        if (qorum.containsKey(jsonString)) {
                            qorum.replace(jsonString, qorum.get(jsonString) + 1);
                        } else {
                            qorum.put(jsonString, 1);
                        }

                        for (String p : qorum.keySet()) {
                            if (qorum.get(p) > (serverPorts.size() / 2)) {
                                System.out.println("WE HAVE A QORUM LADIES AND GENTS!!!!!");
                                HashMap<Integer, ArrayList<Good>> qorumWinner = gson.fromJson(p, HashMap.class);
                                for(Integer i : qorumWinner.keySet()){
                                    usersWriteTimeStamps.replace(userId, i);
                                    goods.replace(userId, qorumWinner.get(i));
                                    break;
                                }
                                return true;
                            }
                        }
                }

                return false;

            }
        }catch (Exception e){
            System.out.println("We Have A Problem In The Qorum System.");
            e.printStackTrace();
        }

        return false;

    }


    public String getServerStatus(int userId) throws RemoteException {
        try{
            HashMap<Integer, ArrayList<Good>> stateHashMap = new HashMap<>();
            Gson gson = new Gson();
            stateHashMap.put(usersWriteTimeStamps.get(userId), goods.get(userId));
            return gson.toJson(stateHashMap);
        }catch (Exception e){
            System.out.println("Exception In Acquiring Server Status");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This Method is responsible for generating the Request objects the server will send back to the clients. This Method was made to reduce the ammount of duplicate code.
     * @param answerMessage The Answer will send back to the client as a field of the Request object
     * @return A String corresponding to the Request object in JsonFormat
     */
    private String answerFactory(String answerMessage, int writeTimeStamp){
        Gson gson = new Gson();

        Request answer = new Request();
        answer.setAnswer(answerMessage);
        answer.setNotaryId(ID);
        answer.setNounce(new Date().getTime());
        //answer.setWriteTimeStamp(writeTimeStamp);

        if(USING_CC){
            answer.setSignature(iCartaoCidadao.sign(gson.toJson(answer)));
        }else {
            answer.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(answer)));
        }

        return gson.toJson(answer);

    }

    /**
     * This Method is Responsible for the Server initial Setup of Ports, ID, and getting the network of servers from a well known one.
     */
    private void initialSetup(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        try{
            System.out.println("Please Introduce The Port Number:");
            String tempPort = reader.readLine();
            while (!tryParseInt(tempPort)){
                System.out.println("Please Introduce a Valid Port Number!");
                System.out.print("Port Number:");
                tempPort = reader.readLine();
            }
            this.PORT = Integer.parseInt(tempPort);

            System.out.println("Please Introduce Notary ID:");
            String tempID = reader.readLine();
            while (!tryParseInt(tempID)){
                System.out.println("Please Introduce a Valid Number!");
                System.out.print("Port Number:");
                tempID = reader.readLine();
            }
            this.ID = Integer.parseInt(tempID);
            // Notary now knows if it is using CC and its ID, therefore it can create its keys
            KeyStoreInterface.addNotaryKeysToKeyStore(ID, USING_CC);

            System.out.println("Please Introduce The Port Of The Well Known Server:");
            String WellKnownServerPort = reader.readLine();
            while (!tryParseInt(WellKnownServerPort)){
                System.out.println("Please Introduce a Valid Number!");
                System.out.print("Port Number:");
                WellKnownServerPort = reader.readLine();
            }

            iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:" + WellKnownServerPort + "/Notary");

            serverPorts = proxy.getNetworkOfNotaries();
            proxy.joinNetwork(ID, PORT);

            for(int port : serverPorts.values()){
                if(port != Integer.parseInt(WellKnownServerPort)){
                    iProxy notary = (iProxy) Naming.lookup("rmi://localhost:" + port + "/Notary");
                    notary.joinNetwork(ID, PORT);
                    notary = null;
                }
            }

            serverPorts.put(ID, PORT);
            proxy=null; //This is here just to call garbage collector sooner but will delete if prooven to work without this

        }catch (IOException e){
            System.out.println("Problems with The Buffered Reader. Unlikely That The Program Reaches this Catch");
            e.printStackTrace();
        }catch (NotBoundException e){
            System.out.println("Probably Forgot To Bind Object To Address and Port");
            e.printStackTrace();
        }catch (Exception e){
            System.out.println("General Exception");
            e.printStackTrace();
        }



    }

    /**
     * Auxiliary method used to verify if the input introduced by the user is in fact an integer number
     * @param value The value to be verified
     * @return True if the value is actually an integer, false otherwise
     */
    private boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            System.out.println("The introduced Input could not be converted to an integer.");
            return false;
        }
    }

    /**
     * Method user to retreive the current network of servers from other servers.
     * @return The concurrent HashMap containing the IDs and Ports of the other servers in the Localhost Network
     */
    public ConcurrentHashMap<Integer, Integer> getNetworkOfNotaries(){
        return serverPorts;
    }

    /**
     * Method that new servers invoke to join the network
     * @param id The New Server ID
     * @param port The New Server Port
     */
    public void joinNetwork(Integer id, Integer port) {
        serverPorts.put(id, port);
        for(Integer i : serverPorts.values()){
            System.out.println(i);
        }
    }

    /**
     * Method that validates if a received Request object by the server is valid by checking if Signatures Match
     * @param pedido The Request object that will be verified
     */
    private boolean validateRequest(Request pedido) {
        Gson gson = new Gson();
        byte[] signature = pedido.getSignature();
        pedido.setSignature(null);
        boolean result = SignatureGenerator.verifySignature(publicKeys.get(pedido.getUserId()), signature, gson.toJson(pedido));
        pedido.setSignature(signature);

        return result;
    }

    private boolean validateNotaryRequests(ArrayList<Request> notaryAnswers){
        //TODO: Dar clear do good antes de verificar o request e da signature obviamente
        Gson gson = new Gson();
        for(Request i : notaryAnswers){
            byte[] signature = i.getSignature();
            Good good = i.getGood();
            i.setSignature(null);
            i.setGood(null);

            if(!SignatureGenerator.verifySignature(pubKey, signature, gson.toJson(i))){
                return false;
            }

        }
        return true;
    }

    private boolean validateWriteTimeStamp(Request pedido){
        int userId = pedido.getUserId();
        int writeTimeStampToValidate = pedido.getAnswersFromNotaries().get(0).getGood().getWriteTimeStampOfGood();
        int currentTimeStamp = usersWriteTimeStamps.get(userId);

        if(writeTimeStampToValidate == (currentTimeStamp + 1)){
            usersWriteTimeStamps.replace(userId, writeTimeStampToValidate);
            return true;
        }else if(writeTimeStampToValidate > (currentTimeStamp)){
            return synchronizeServerStatus(pedido.getUserId(), writeTimeStampToValidate);
        }else{
            return false;
        }
    }

    /**
     * Method That updates the logs of the Server
     * @param operation The operation executed
     * @param pedido The Request Object Sent By The Client
     * @param result The result of the executed operation that had as argument the Request object
     */
    private synchronized void updateServerLog(OPCODE operation, Request pedido, String result) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
            switch (operation) {
                case GETSTATEOFGOOD:
                    writer.write("Operation: Get State of Good\n");
                    writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
                    writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
                    writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
                    writer.write("Requested Good ID: " + pedido.getGoodId() + "\n");
                    writer.write("Nounce: " + pedido.getNounce() + "\n");
                    writer.write("Operation Result: " + result + "\n");
                    writer.write("---------------------------------------------------------------------------------------------------------------\n");
                    writer.close();
                    break;
                case SELLGOOD:
                    writer.write("Operation: Sell Good\n");
                    writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
                    writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
                    writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
                    writer.write("Requested Good ID: " + pedido.getGoodId() + "\n");
                    writer.write("Nounce: " + pedido.getNounce() + "\n");
                    writer.write("Operation Result: " + result + "\n");
                    writer.write("---------------------------------------------------------------------------------------------------------------\n");
                    writer.close();
                    break;
                case TRANSFERGOOD:
                    writer.write("Operation: Transfer Good\n");
                    writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
                    writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
                    writer.write("Seller ID: " + pedido.getSellerId() + "\n");
                    writer.write("Seller Signature: " + Arrays.toString(pedido.getSignature()) + "\n");
                    writer.write("Buyer ID: " + pedido.getBuyerId() + "\n");
                    writer.write("Buyer Signature: " + Arrays.toString(pedido.getBuyerSignature()) + "\n");
                    writer.write("Good ID: " + pedido.getGoodId() + "\n");
                    writer.write("Nounce: " + pedido.getNounce() + "\n");
                    writer.write("Operation Result: " + result + "\n");
                    writer.write("---------------------------------------------------------------------------------------------------------------\n");
                    writer.close();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something Went Wrong During Server Log Update");
        }
    }

    /**
     * Mehtod used to check if the backup file of the Server State is created and it is not empty
     * @return True if the file is created and not empty, false otherwise.
     */
    private synchronized boolean isBackupFileCreatedAndNotEmpty() {
        String finalBackupPath = getBackupPaths()[0];
        File f = new File(finalBackupPath);
        if (f.exists() && !f.isDirectory()) {
            try {
                String jsonString = FileUtils.readFileToString(new File(finalBackupPath), "UTF-8");
                jsonString = jsonString.replace("\n", "").replace("\r", "");
                jsonString = jsonString.trim();
                if (jsonString.length() > 0)
                    return true;
                System.out.println("Backup file is empty");
                return false;
            } catch (Exception e) {
                System.out.println("Failed to assert if backup file is not empty");
                return false;
            }
        }
        System.out.println("Backup file not found");
        return false;
    }

    /**
     * Auxiliary Method to Get The Paths of The Files Used For Backup Purposes
     * @return The Paths Of The Backup Files
     */
    private String[] getBackupPaths() {
        String[] paths = new String[2];
        String basePath = System.getProperty("user.dir");
        if(!basePath.contains("\\Notary"))
            basePath+="\\Notary";
        paths[0] = basePath + "\\Backups\\ServerState.old";
        paths[1] = basePath + "\\Backups\\ServerState.new";
        return paths;
    }

    /**
     * Method That Gets The Base Directory of The Program
     * @return The Path for The Base Directory
     */
    private String baseDirGenerator() {
        String basePath = System.getProperty("user.dir");
        if(!basePath.contains("\\Notary"))
            basePath+="\\Notary";
        return basePath;
    }

    private static String baseClientDirGenerator() {
        String basePath = System.getProperty("user.dir");
        if(basePath.contains("\\Notary")){
            //basePath = basePath.replaceAll("\\Notary", "\\Client");
        }

        return basePath;
    }
}