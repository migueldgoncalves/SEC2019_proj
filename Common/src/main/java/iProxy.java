import java.rmi.*;

public interface iProxy extends Remote {

    String sell() throws RemoteException;

    String getStateOfGood() throws RemoteException;

    String transferGood() throws RemoteException;

}
