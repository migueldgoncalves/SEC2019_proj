import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;

public class Server extends UnicastRemoteObject implements iProxy {

    private Dictionary<Integer, ArrayList<Good>> goods;

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

    public String sell(int ownerId, int goodId) throws RemoteException {
        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == goodId && i.getOwnerId() == ownerId) {
                    if (!i.isOnSale())
                        i.setOnSale(true);
                    return (i.isOnSale() ? "The Item is Already On Sale" : "The Item is Now on Sale");
                }
            }
        }
        return "The Requested Item To Be Put on Sell Is Not Available In The System";
    }

    //return OwnerId and State of The Good
    public String getStateOfGood(int goodId) throws RemoteException {
        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == goodId) {
                    return "<" + i.getOwnerId() + ", " + (i.isOnSale() ? "On-Sale" : "Not-On-Sale" + ">");
                }
            }
        }
        return "The GoodId" + goodId + "Is Not Present In The Server!";
    }

    public String transferGood(int ownerId, int newOwnerId, int goodId) throws RemoteException {
        for (Enumeration e = goods.elements(); e.hasMoreElements(); ) {
            ArrayList<Good> temp = (ArrayList<Good>) e.nextElement();
            for (Good i : temp) {
                if (i.getGoodId() == goodId && i.getOwnerId() == ownerId && i.isOnSale()) {
                    Good newOwner = new Good(newOwnerId, i.getGoodId(), i.getName(), !i.isOnSale());
                    temp.set(temp.indexOf(i), newOwner);
                    return "The Good with Good ID " + i.getGoodId() + " Has now Been transfered to the new Owner with Owner ID " + newOwnerId;
                }
            }
        }
        return "The Good Id, Owner Id or New Owner ID is not present in the server!";
    }

    public boolean checkUserId(int ID) throws RemoteException {
        return true;
    }

}
