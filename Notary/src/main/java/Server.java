import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server extends UnicastRemoteObject implements iProxy {

    private ArrayList<ArrayList<Good>> goods = new ArrayList<ArrayList<Good>>();

    Server() throws RemoteException {
        super();
        FileReader fileReader = new FileReader();
        goods = fileReader.goodsListConstructor("Common\\src\\main\\resources\\GoodsFile1.xml");

        if (goods.size() == 0) {
            System.out.println("WARNING: No good were loaded into the Notary!");
        } else {

        }
    }

    public String sell() throws RemoteException{
        return "The Server Sold the Good";
    }

    //return OwnerId and State
    public String getStateOfGood(String goodId) throws RemoteException {
        return "The server returned the state of the good";
    }

    public String transferGood() throws RemoteException{
        return "The server transfered the good";
    }

    public boolean checkUserId(int ID) throws RemoteException {
        return true;
    }

}
