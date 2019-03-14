import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iProxy extends Remote {

    String sell(int ownerId, int goodId) throws RemoteException;

    String getStateOfGood(int goodId) throws RemoteException;

    String transferGood(int ownerId, int newOwnerId, int goodId) throws RemoteException;

    boolean checkUserId(int ID) throws RemoteException;
}
