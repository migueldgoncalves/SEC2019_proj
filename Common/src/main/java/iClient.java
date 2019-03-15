import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iClient extends Remote {

    String Buy(int ownerId, int newOwnerId, int goodId) throws RemoteException;

}
