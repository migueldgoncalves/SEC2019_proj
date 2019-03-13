import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;

public class Client {

    private static iProxy proxy = null;

    public static void main(String[] args) {
        try {
            proxy = (iProxy) Naming.lookup("rmi://localhost:8086/Notary");

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            //User Input to be sent to Server
            System.out.println("Please Introduce User ID: ");
            String ID = reader.readLine();
            //If input is integer and is present in server
            if (tryParseInt(ID) && checkUserIdExistence(Integer.parseInt(ID))) {
                printMenu();
                String input = reader.readLine();
                while (tryParseInt(input) && !input.equals("exit")) {

                    switch (input) {
                        case "1":
                            proxy.sell();
                            break;
                        case "2":
                            //Os clientes têm de se registar num porto conhecido de modo a podermos invocar um comando Buy a partir de outro cliente
                            break;
                        case "3":
                            proxy.getStateOfGood();
                            break;
                        default:
                            System.out.println("The Introduced Input is not a valid number, please try again or type 'exit' to exit program.");
                            break;
                    }
                    input = reader.readLine();
                }
                System.exit(1);
            } else {
                throw new Exception("The Introduced value is not convertable to an Integer type variable or user ID does not exist in the server. Exiting ...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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
