import java.rmi.Naming;

public class Client {

    public static void main(String[] args) {
        try {

            iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:8086/Notary");

            System.out.println(proxy.getStateOfGood());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
