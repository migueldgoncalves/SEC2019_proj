import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.*;

public class Server extends UnicastRemoteObject implements iProxy {

    Server() throws RemoteException {
        super();
    }

    public String sell() throws RemoteException{
        return "";
    }

    public String getStateOfGood() throws RemoteException{
        return "";
    }

    public String transferGood() throws RemoteException{
        return "";
    }

}
