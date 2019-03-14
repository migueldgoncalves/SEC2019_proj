import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.ConnectException;
import java.rmi.Naming;

public class Client {

    private static iProxy proxy = null;

    public static void main(String[] args) {
        try {
            proxy = (iProxy) Naming.lookup("rmi://localhost:8086/Notary");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Please Introduce User ID: ");
            String ID = reader.readLine();

            if (tryParseInt(ID) && checkUserIdExistence(Integer.parseInt(ID))) {
                printMenu();
                String input = reader.readLine();
                while (tryParseInt(input) && !input.equals("exit")) {

                    switch (input) {
                        case "1":
                            break;
                        case "2":
                            //Os clientes têm de se registar num porto conhecido de modo a podermos invocar um comando Buy a partir de outro cliente
                            break;
                        case "3":
                            //To receive good ID
                            //proxy.getStateOfGood(input);
                            break;
                        default:
                            System.out.println("The Introduced Input is not a valid number, please try again or type 'exit' to exit program.");
                            break;
                    }

                    printMenu();
                    input = reader.readLine();
                }
                reader.close();
                System.exit(1);
            } else {
                throw new Exception("The Introduced value is not convertable to an Integer type variable or user ID does not exist in the server. Exiting ...");
            }

        }catch (ConnectException e){
            System.out.println("Could not connect to server. The server may be offline or unavailable due to network reasons.");
            System.exit(-1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            System.out.println("The introduced Input could not be converted to an integer. Exiting...");
            return false;
        }
    }

    private static boolean checkUserIdExistence(int ID) {
        try {
            return proxy.checkUserId(ID);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return false;
    }

    private static void printMenu() {
        System.out.print("Please Introduce The Desired Option Number: \n 1. Sell an Item. \n 2. Buy an Item. \n 3. Get Item State.");
    }

}
