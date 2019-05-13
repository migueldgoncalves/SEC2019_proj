import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface iClient extends Remote {

    ArrayList<String> Buy(String request) throws RemoteException;

}
