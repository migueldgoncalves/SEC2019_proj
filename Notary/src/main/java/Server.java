import com.google.gson.Gson;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;

public class Server extends UnicastRemoteObject implements iProxy {

    private Dictionary<Integer, ArrayList<Good>> goods;
    private SignatureGenerator signatureHandler;

    public Server(String FilePath) throws RemoteException {
        super();
        try {
            FileReader fileReader = new FileReader();
            goods = fileReader.goodsListConstructor(FilePath);
        } catch (Exception e) {
            System.out.println("Something Went Wrong");
        }
    }

    Server() throws RemoteException {
        super();
        try {
            FileReader fileReader = new FileReader();
            goods = fileReader.goodsListConstructor("Notary\\src\\main\\resources\\GoodsFile1.xml");
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
        Gson gson = new Gson();
        Request pedido = gson.fromJson(jsonRequest, Request.class);
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
        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == pedido.getGoodId() && i.getOwnerId() == pedido.getBuyerId() && i.isOnSale()) {
                    Good newOwner = new Good(pedido.getBuyerId(), i.getGoodId(), i.getName(), !i.isOnSale());
                    temp.set(temp.indexOf(i), newOwner);
                    return "The Good with Good ID " + i.getGoodId() + " Has now Been transfered to the new Owner with Owner ID " + pedido.getBuyerId();
                }
            }
        }
        return "The Good Id, Owner Id or New Owner ID is not present in the server!";
    }

}
