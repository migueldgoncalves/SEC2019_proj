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
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

public class Server extends UnicastRemoteObject implements iProxy {

    private Hashtable<Integer, ArrayList<Good>> goods = new Hashtable<>();
    private Hashtable<Integer, PublicKey> publicKeys = new Hashtable<>();
    int PORT;
    private PrivateKey privKey;
    private boolean USING_CC = false;
    private ConcurrentHashMap<Integer, Integer> serverPorts = new ConcurrentHashMap<>();
    private int ID;

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

            //Add public key from Cartao de Cidadao to file
            PublicKey key = iCartaoCidadao.getPublicKeyFromCC();
            assert key != null;
            FileOutputStream out = new FileOutputStream(baseDirGenerator() + "\\src\\main\\resources\\Notary_CC.pub");
            out.write(key.getEncoded());
            out.flush();
            out.close();

            for (int i = 1; i <= 9; i++) {
                publicKeys.put(i, RSAKeyLoader.getPub(baseDirGenerator() + "\\src\\main\\resources\\User" + i + ".pub"));
            }

            privKey = RSAKeyLoader.getPriv(baseDirGenerator() + "\\src\\main\\resources\\Notary.key");

            ID = 1;

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

            for (int i = 1; i <= 9; i++) {
                publicKeys.put(i, RSAKeyLoader.getPub(baseDirGenerator() + "\\src\\main\\resources\\User" + i + ".pub"));
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
                    PublicKey pubKeySingle = iCartaoCidadao.getPublicKeyFromCC();
                    assert pubKeySingle != null;

                    FileOutputStream out = new FileOutputStream(baseDirGenerator() + "\\src\\main\\resources\\Notary_CC.pub");
                    out.write(pubKeySingle.getEncoded());
                    out.flush();
                    out.close();

                    break;
                case "2":
                    ID = 1;
                    privKey = RSAKeyLoader.getPriv( baseDirGenerator() + "\\src\\main\\resources\\Notary.key");
                    break;
                case "3":
                    USING_CC = true;
                    PublicKey pubKey = iCartaoCidadao.getPublicKeyFromCC();

                    FileOutputStream out2 = new FileOutputStream(baseDirGenerator() + "\\src\\main\\resources\\Notary_CC.pub");
                    assert pubKey != null;
                    out2.write(pubKey.getEncoded());
                    out2.flush();
                    out2.close();

                    System.out.println("Is It The First Node? \n 1. Yes \n 2. No");
                    System.out.print("Option Number:");
                    switch (reader.readLine()){
                        case "1":
                            this.PORT = 8086;
                            this.ID = 1;
                            serverPorts.put(ID, PORT);
                            break;
                        case "2":
                            initialSetup();
                            break;
                    }
                    break;
                case "4":
                    System.out.println("Is It The First Node? \n 1. Yes \n 2. No");
                    System.out.print("Option Number:");
                    switch (reader.readLine()){
                        case "1":
                            this.PORT = 8086;
                            this.ID = 1;
                            break;
                        case "2":
                            initialSetup();
                            break;
                    }
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

        if (!NonceVerifier.isNonceValid(pedido)){
            updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "This message has already been processed!");
            return answerFactory("This message has already been processed!");
        }

        if (!validateRequest(pedido)) {
            updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "Invalid Authorization to Invoke Method Get State Of Good in Server!");
            return answerFactory("Invalid Authorization to Invoke Method Get State Of Good in Server!");
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId()) {
                    updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "<" + i.getOwnerId() + ", " + (i.isOnSale() ? "On-Sale>" : "Not-On-Sale>"));
                    return answerFactory("<" + i.getOwnerId() + ", " + (i.isOnSale() ? "On-Sale>" : "Not-On-Sale>"));
                }
            }
        }
        updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "The GoodId " + pedido.getGoodId() + " Is Not Present In The Server!");
        return answerFactory("The GoodId " + pedido.getGoodId() + " Is Not Present In The Server!");
    }

    /**
     * This Method is responsible for transfering a good that is on sale from one user (Seller) to another (Buyer)
     * @param jsonRequest The Request Object containing all necessary data
     */
    public String transferGood(String jsonRequest) throws RemoteException {
        Gson gson = new Gson();
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        if (!NonceVerifier.isNonceValid(pedido)){
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "This message has already been processed");
            return answerFactory("This message has already been processed");
        }

        if (!validateRequest(pedido)) {
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "Invalid Authorization to Transfer Good!");
            return answerFactory("Invalid Authorization to Transfer Good!");
        }

        if (pedido.getBuyerId() < 1 || pedido.getBuyerId() > 9) {
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "The Good Id, Owner Id or New Owner ID is not present in the server!");
            return answerFactory("The Good Id, Owner Id or New Owner ID is not present in the server!");
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
                            return answerFactory("The Good with Good ID " + i.getGoodId() + " Has now Been transfered to the new Owner with Owner ID " + pedido.getBuyerId());
                        }else {
                            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "The Item was already Sold, Does not Exist or Is not On Sale");
                            return answerFactory("The Item was already Sold, Does not Exist or Is not On Sale");
                        }
                    }
                }
            }
        }
        updateServerLog(OPCODE.TRANSFERGOOD, pedido, "The Good Id, Owner Id or New Owner ID is not present in the server!");
        return answerFactory("The Good Id, Owner Id or New Owner ID is not present in the server!");
    }

    /**
     * Method Sell that is responsible for putting a given Good on sale
     * @param jsonRequest The Request Object that contains the Good ID to be put on sell
     */
    public String sell(String jsonRequest) throws RemoteException {
        Gson gson = new Gson();
        //Transform to Request Object
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        //Replay Attack Prevention
        if (!NonceVerifier.isNonceValid(pedido)){
            updateServerLog(OPCODE.SELLGOOD, pedido, "This message has already been processed by The Server!");
            return answerFactory("This message has already been processed by The Server!");
        }

        //Verify Signature withing Object
        if (!validateRequest(pedido)) {
            updateServerLog(OPCODE.SELLGOOD, pedido, "Invalid Authorization To Invoke Method Sell on Server!");
            return answerFactory("Invalid Authorization To Invoke Method Sell on Server!");
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId() && i.getOwnerId() == pedido.getUserId()) {
                    if (!i.isOnSale()) {
                        i.setOnSale(true);
                        saveServerState();
                        updateServerLog(OPCODE.SELLGOOD, pedido, "The Item is Now on Sale");
                        return answerFactory("The Item is Now on Sale");
                    } else {
                        updateServerLog(OPCODE.SELLGOOD, pedido, "The Item was Already On Sale");
                        return answerFactory("The Item was Already On Sale");
                    }
                }
            }
        }
        updateServerLog(OPCODE.SELLGOOD, pedido, "The Requested Item To Be Put on Sell Is Not Available In The System");
        return answerFactory("The Requested Item To Be Put on Sell Is Not Available In The System");
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
    private synchronized boolean saveServerState() {
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
            return false;
        }

        try {
            Files.move(Paths.get(temporaryBackupPath), Paths.get(finalBackupPath), ATOMIC_MOVE);
            return true;
        } catch (AccessDeniedException e) {
            System.out.println("Run as Administrator!");
            return false;
        } catch (Exception e) {
            System.out.println("An error occurred during system save!");
            e.printStackTrace();
            return false;
        }

    }

    //########################################## Auxiliary Methods ####################################################

    private String answerFactory(String answerMessage){
        Gson gson = new Gson();

        Request answer = new Request();
        answer.setAnswer(answerMessage);
        answer.setNotaryId(ID);
        answer.setNounce(new Date().getTime());

        if(USING_CC){
            answer.setSignature(iCartaoCidadao.sign(gson.toJson(answer)));
        }else {
            answer.setSignature(SignatureGenerator.generateSignature(privKey, gson.toJson(answer)));
        }

        return gson.toJson(answer);

    }

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

    private boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            System.out.println("The introduced Input could not be converted to an integer.");
            return false;
        }
    }

    public ConcurrentHashMap<Integer, Integer> getNetworkOfNotaries(){
        return serverPorts;
    }

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

        return SignatureGenerator.verifySignature(publicKeys.get(pedido.getUserId()), signature, gson.toJson(pedido));
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
                    writer.write("Requested Good ID: " + pedido.getGoodId() + "\n");
                    writer.write("Operation Result: " + result + "\n");
                    writer.write("---------------------------------------------------------------------------------------------------------------\n");
                    writer.close();
                    break;
                case SELLGOOD:
                    writer.write("Operation: Sell Good\n");
                    writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
                    writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
                    writer.write("Requested Good ID: " + pedido.getGoodId() + "\n");
                    writer.write("Operation Result: " + result + "\n");
                    writer.write("---------------------------------------------------------------------------------------------------------------\n");
                    writer.close();
                    break;
                case TRANSFERGOOD:
                    writer.write("Operation: Transfer Good\n");
                    writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
                    writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
                    writer.write("Seller ID: " + pedido.getSellerId() + "\n");
                    writer.write("Buyer ID: " + pedido.getBuyerId() + "\n");
                    writer.write("Good ID: " + pedido.getGoodId() + "\n");
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

    private String[] getBackupPaths() {
        String[] paths = new String[2];
        String basePath = System.getProperty("user.dir");
        if(!basePath.contains("\\Notary"))
            basePath+="\\Notary";
        paths[0] = basePath + "\\Backups\\ServerState.old";
        paths[1] = basePath + "\\Backups\\ServerState.new";
        return paths;
    }

    private String baseDirGenerator() {
        String basePath = System.getProperty("user.dir");
        if(!basePath.contains("\\Notary"))
            basePath+="\\Notary";
        return basePath;
    }
}