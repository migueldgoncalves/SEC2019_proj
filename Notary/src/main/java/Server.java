import com.google.gson.Gson;

import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.security.PublicKey;
import java.util.*;

public class Server extends UnicastRemoteObject implements iProxy {

    private Dictionary<Integer, ArrayList<Good>> goods;
    private SignatureGenerator signatureHandler;
    private Map<Integer, PublicKey> publicKeys = new HashMap<>();

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
            System.out.println(publicKeys.size() + " Keys have been loaded into the Notary");
        } catch (Exception e) {
            System.out.println("Something Went Wrong");
            e.printStackTrace();
        }
    }

    Server() throws RemoteException {
        super();
        try {
            FileReader fileReader = new FileReader();
            goods = fileReader.goodsListConstructor("Notary\\src\\main\\resources\\GoodsFile1.xml");
            for (int i = 0; i < 9; i++) {
                publicKeys.put(i, RSAKeyLoader.getPub("Notary\\src\\main\\resources\\User" + i + ".pub"));
            }
            System.out.println(publicKeys.size() + " Keys have been loaded into the Notary");
        } catch (Exception e) {
            System.out.println("File Was Not Found! ERROR.");
        }

        if (goods.size() == 0) {
            System.out.println("WARNING: No good were loaded into the Notary!");
        } else {
            System.out.println("All goods were loaded! With a Total of " + goods.size() + " Users");
        }
    }

    public String sell(String jsonRequest) throws RemoteException {
        //Transform to Request Object
        Gson gson = new Gson();
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        //Verify Signature withing Object
        if (!validateRequest(jsonRequest, pedido)) {
            return "Invalid Authorization To Invoke Method Sell on Server!";
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId() && i.getOwnerId() == pedido.getUserId()) {
                    if (!i.isOnSale()) {
                        i.setOnSale(true);
                        return ("The Item is Now on Sale");
                    } else {
                        return "The Item was Already On Sale";
                    }
                }
            }
        }
        return "The Requested Item To Be Put on Sell Is Not Available In The System";
    }

    //return OwnerId and State of The Good
    public String getStateOfGood(String jsonRequest) throws RemoteException {
        Gson gson = new Gson();
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        //Verify Signature withing Object
        if (!validateRequest(jsonRequest, pedido)) {
            return "Invalid Authorization to Invoke Method Get State Of Good in Server!";
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId()) {
                    return "<" + i.getOwnerId() + ", " + (i.isOnSale() ? "On-Sale>" : "Not-On-Sale>");
                }
            }
        }
        return "The GoodId " + pedido.getGoodId() + " Is Not Present In The Server!";
    }

    public String transferGood(String jsonRequest) throws RemoteException {

        Gson gson = new Gson();
        Request pedido = gson.fromJson(jsonRequest, Request.class);

        if (!validateRequest(jsonRequest, pedido)) {
            return "Invalid Authorization to Transfer Good!";
        }

        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId() && i.getOwnerId() == pedido.getSellerId() && i.isOnSale()) {
                    Good newOwner = new Good(pedido.getBuyerId(), i.getGoodId(), i.getName(), !i.isOnSale());
                    temp.set(temp.indexOf(i), newOwner);
                    return "The Good with Good ID " + i.getGoodId() + " Has now Been transfered to the new Owner with Owner ID " + pedido.getBuyerId();
                }
            }
        }
        return "The Good Id, Owner Id or New Owner ID is not present in the server!";
    }

    private boolean validateRequest(String jsonRequest, Request pedido) {
        Gson gson = new Gson();
        //Verify Signature withing Object
        byte[] signature = pedido.getSignature();
        pedido.setSignature(null);

        return SignatureGenerator.verifySignature(publicKeys.get(pedido.getUserId()), signature, gson.toJson(pedido));
    }

    private synchronized void saveServerState() {
        try {
            Gson gson = new Gson();
            PrintWriter writer = new PrintWriter("ServerState.old");
            writer.println(gson.toJson(this));
        } catch (Exception e) {

        }
    }

}
