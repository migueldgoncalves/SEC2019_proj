import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

public class Server extends UnicastRemoteObject implements iProxy {

    private Dictionary<Integer, ArrayList<Good>> goods;
    private SignatureGenerator signatureHandler;
    private Map<Integer, PublicKey> publicKeys = new HashMap<>();

    /**
     * The Server Constructor used for test reasons. This method was implemented to be called during
     * test phase of the program in order to verify the correct behaviour of the system
     * @param FilePath The path of the file that contains the goods that will be loaded into the server
     */
    public Server(String FilePath) throws RemoteException {
        super();
        try {
            FileReader fileReader = new FileReader();
            goods = fileReader.goodsListConstructor(FilePath);

            //Add public key from Cartao de Cidadao to file
            PublicKey key = CartaoCidadao.getPublicKeyFromCC();

            // User directory will include Notary directory, which we want to remove from path
            String baseDir = System.getProperty("user.dir").replace("\\Notary", "");
            RSAKeySaverAsText.SavePublicKeyAsText(key, baseDir + "\\Client\\src\\main\\resources\\Notary");

            for (int i = 0; i < 9; i++) {
                publicKeys.put(i, RSAKeyLoader.getPub("src\\main\\resources\\User" + i + ".pub"));
            }

            System.out.println(publicKeys.size() + " Keys Have Been Loaded Into The Notary");
        } catch (Exception e) {
            System.out.println("Something Went Wrong");
            e.printStackTrace();
        }
    }


    /**
     * Default server constructor used during actual program execution
     */
    Server() throws RemoteException {
        super();
        try {
            FileReader fileReader = new FileReader();
            goods = fileReader.goodsListConstructor("Notary\\src\\main\\resources\\GoodsFile1.xml");

            for (int i = 0; i < 9; i++) {
                publicKeys.put(i, RSAKeyLoader.getPub("Notary\\src\\main\\resources\\User" + i + ".pub"));
            }

            System.out.println(publicKeys.size() + " Keys Have Been Loaded Into The Notary!");
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

    /**
     * Method Sell that is responsible for putting a giving Good on sale
     * @param jsonRequest The Request Object that contains the Good ID to be put on sell
     */
    public String sell(String jsonRequest) throws RemoteException {
        //Transform to Request Object
        Gson gson = new Gson();
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        //Replay Attack Prevention
        if (!NonceVerifier.isNonceValid(pedido))
            return "This message has already been processed";

        //Verify Signature withing Object
        if (!validateRequest(pedido)) {
            updateServerLog(OPCODE.SELLGOOD, pedido, "Invalid Authorization To Invoke Method Sell on Server!");
            return "Invalid Authorization To Invoke Method Sell on Server!";
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId() && i.getOwnerId() == pedido.getUserId()) {
                    if (!i.isOnSale()) {
                        i.setOnSale(true);
                        updateServerLog(OPCODE.SELLGOOD, pedido, "The Item is Now on Sale");
                        return ("The Item is Now on Sale");
                    } else {
                        updateServerLog(OPCODE.SELLGOOD, pedido, "The Item was Already On Sale");
                        return "The Item was Already On Sale";
                    }
                }
            }
        }
        updateServerLog(OPCODE.SELLGOOD, pedido, "The Requested Item To Be Put on Sell Is Not Available In The System");
        return "The Requested Item To Be Put on Sell Is Not Available In The System";
    }

    /**
     * This method is responsible for returning the state of a requested good
     * @param jsonRequest The Request Object that contains the Parameters to validate request (Signature, Good ID, etc...)
     */
    public String getStateOfGood(String jsonRequest) throws RemoteException {
        Gson gson = new Gson();
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        if (!NonceVerifier.isNonceValid(pedido))
            return "This message has already been processed";

        if (!validateRequest(pedido)) {
            updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "Invalid Authorization to Invoke Method Get State Of Good in Server!");
            return "Invalid Authorization to Invoke Method Get State Of Good in Server!";
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId()) {
                    updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "<" + i.getOwnerId() + ", " + (i.isOnSale() ? "On-Sale>" : "Not-On-Sale>"));
                    return "<" + i.getOwnerId() + ", " + (i.isOnSale() ? "On-Sale>" : "Not-On-Sale>");
                }
            }
        }
        updateServerLog(OPCODE.GETSTATEOFGOOD, pedido, "The GoodId " + pedido.getGoodId() + " Is Not Present In The Server!");
        return "The GoodId " + pedido.getGoodId() + " Is Not Present In The Server!";
    }

    /**
     * This Method is responsible for transfering a good that is on sale from one user (Seller) to another (Buyer)
     * @param jsonRequest The Request Object containing all necessary data
     */
    public String transferGood(String jsonRequest) throws RemoteException {
        Gson gson = new Gson();
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        if (!NonceVerifier.isNonceValid(pedido))
            return "This message has already been processed";

        if (!validateRequest(pedido)) {
            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "Invalid Authorization to Transfer Good!");
            return "Invalid Authorization to Transfer Good!";
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId() && i.getOwnerId() == pedido.getSellerId() && i.isOnSale()) {
                    synchronized (i) {
                        if (i.getGoodId() == pedido.getGoodId() && i.getOwnerId() == pedido.getSellerId() && i.isOnSale()){
                            Good newOwner = new Good(pedido.getBuyerId(), i.getGoodId(), i.getName(), !i.isOnSale());
                            temp.set(temp.indexOf(i), newOwner);
                            updateServerLog(OPCODE.TRANSFERGOOD, pedido, "The Good with Good ID " + i.getGoodId() + " Has now Been transfered to the new Owner with Owner ID " + pedido.getBuyerId());
                            return "The Good with Good ID " + i.getGoodId() + " Has now Been transfered to the new Owner with Owner ID " + pedido.getBuyerId();
                        }else {
                            return "The Item was already Sold";
                        }
                    }
                }
            }
        }
        updateServerLog(OPCODE.TRANSFERGOOD, pedido, "The Good Id, Owner Id or New Owner ID is not present in the server!");
        return "The Good Id, Owner Id or New Owner ID is not present in the server!";
    }

    /**
     * Method The recovers a Server state (If a previous state exists in the directory)
     */
    private void getSystemState() {
        try {
            Gson gson = new Gson();
            String jsonString = FileUtils.readFileToString(new File("Backups/ServerState.old"));
            jsonString = jsonString.replace("\n", "").replace("\r", "");
            Server temp = gson.fromJson(jsonString, Server.class);
            this.publicKeys = temp.publicKeys;
            this.goods = temp.goods;
            System.out.println("Recovered Server State");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that validates if a received Request object by the server is valid by checking if Signatures Match
     * @param pedido The Request object that will be verified
     */
    private boolean validateRequest(Request pedido) {
        Gson gson = new Gson();
        //Verify Signature withing Object
        byte[] signature = pedido.getSignature();
        pedido.setSignature(null);

        return SignatureGenerator.verifySignature(publicKeys.get(pedido.getUserId()), signature, gson.toJson(pedido));
    }

    /**
     * Method That Atomically Saves The Server State to a given path
     * @param path The path to where the server state should be saved
     */
    private synchronized boolean saveServerState(String path) {
        try {
            Gson gson = new Gson();
            //File file = new File("ServerState.new"); TO BE DELETED IF MODIFICATION IS WORKING
            PrintWriter writer = new PrintWriter(new File("ServerState.new"));
            writer.println(gson.toJson(this));
            writer.close();
        } catch (Exception e) {
            System.out.println("A Crash Occurred During System Save State.");
            e.printStackTrace();
            return false;
        }

        try {
            Path path1 = Paths.get(path);
            Path path2 = path1.resolve("../ServerState.new");
            Path path3 = path1.resolve("ServerState.old");
            Files.move(path2, path3, ATOMIC_MOVE);
            return true;
        } catch (AccessDeniedException e) {
            System.out.println("Run as Administrator!");
            return false;
        } catch (Exception e) {
            System.out.println("An error ocurred during system save!");
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Method That updates the logs of the Server
     * @param operation The operation executed
     * @param pedido The Request Object Sent By The Client
     * @param result The result of the executed operation that had as argument the Request object
     */
    private void updateServerLog(OPCODE operation, Request pedido, String result) {
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

    private enum OPCODE {
        TRANSFERGOOD, SELLGOOD, GETSTATEOFGOOD
    }
}

