import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iClient extends Remote {

    String Buy(String request) throws RemoteException;

}
