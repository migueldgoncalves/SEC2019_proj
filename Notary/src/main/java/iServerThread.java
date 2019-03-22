import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iServerThread extends Remote {

    String wait(int time, String name) throws RemoteException;

    String wait2(int time, String name) throws RemoteException;

}
