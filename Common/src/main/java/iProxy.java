import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iProxy extends Remote {

    String sell() throws RemoteException;

    String getStateOfGood(String goodId) throws RemoteException;

    String transferGood() throws RemoteException;

    boolean checkUserId(int ID) throws RemoteException;
}
