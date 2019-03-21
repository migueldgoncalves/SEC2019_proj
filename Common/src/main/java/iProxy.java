import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iProxy extends Remote {

    String sell(String request) throws RemoteException;

    String getStateOfGood(String request) throws RemoteException;

    String transferGood(String request) throws RemoteException;

    String wait(int time) throws RemoteException;

}
