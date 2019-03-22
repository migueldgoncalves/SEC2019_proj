import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iClientThread extends Remote {

    String doSomething1(int time, String name) throws RemoteException;

    String doSomething2(int time, String name) throws RemoteException;

}
