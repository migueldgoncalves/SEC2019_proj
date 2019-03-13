import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Server extends UnicastRemoteObject implements iProxy {

    Server() throws RemoteException {
        super();
    }

    public String sell() throws RemoteException{
        return "The Server Sold the Good";
    }

    public String getStateOfGood() throws RemoteException{
        return "The server returned the state of the good";
    }

    public String transferGood() throws RemoteException{
        return "The server transfered the good";
    }

    public boolean checkUserId(int ID) throws RemoteException {
        return true;
    }

}
