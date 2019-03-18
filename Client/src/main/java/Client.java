import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements iClient {

    private static iProxy proxy = null;
    private static int UserID;

    Client() throws RemoteException {
        super();
    }


    public static void main(String[] args) {
        try {
            //Prompt User For Input of Port To Register
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            iClient Clientproxy = new Client();

            System.out.println("Please Introduce The Port You Want to Register:");
            System.out.print("PORT Number: ");
            String port = reader.readLine();

            while (!tryParseInt(port)) {
                System.out.println("Introduce a valida Port Number:");
                System.out.print("Port Number: ");
                port = reader.readLine();
            }
            int portNumber = Integer.parseInt(port);

            LocateRegistry.createRegistry(portNumber);

            Naming.rebind("rmi://localhost:" + port + "/" + UserID, Clientproxy);

            //End Of Client Registration in RMI

            proxy = (iProxy) Naming.lookup("rmi://localhost:8086/Notary");

            System.out.println("Please Introduce User ID: ");
            String ID = reader.readLine();

            if (tryParseInt(ID) && checkUserIdExistence(Integer.parseInt(ID))) {
                UserID = Integer.parseInt(ID);
                printMenu();
                String input = reader.readLine();
                while (tryParseInt(input) && !input.equals("exit")) {

                    switch (input) {
                        case "1":
                            proxy.sell(UserID, promptForGoodId());
                            break;
                        case "2":
                            invokeSeller();
                            break;
                        case "3":
                            proxy.getStateOfGood(promptForGoodId());
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
                throw new Exception("The Introduced value is not convertible to an Integer type variable or user ID does not exist in the server. Exiting ...");
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

    private static String invokeSeller() {
        int sellerId, goodId, portNumber;
        System.out.println("Please Introduce Seller ID:");
        System.out.print("Seller ID: ");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String temp = reader.readLine();
            while (!tryParseInt(temp)) {
                System.out.println("The introduced ID is not a valid Number, please introduce ONLY numbers");
                System.out.print("Seller ID: ");
                temp = reader.readLine();
            }
            sellerId = Integer.parseInt(temp);

            System.out.println("Please Introduce GoodId:");
            System.out.print("Good ID: ");
            temp = reader.readLine();
            while (!tryParseInt(temp)) {
                System.out.println("The Introduced ID is not a valid Number, please introduce ONLY numbers");
                System.out.print("Good ID: ");
                temp = reader.readLine();
            }
            goodId = Integer.parseInt(temp);

            System.out.println("Please Introduce GoodId:");
            System.out.print("Good ID: ");
            temp = reader.readLine();
            while (!tryParseInt(temp)) {
                System.out.println("The Introduced Port Number is not a valid Number, please introduce ONLY numbers");
                System.out.print("Port Number: ");
                temp = reader.readLine();
            }
            portNumber = Integer.parseInt(temp);

            iClient clientProxy = (iClient) Naming.lookup("rmi://localhost:" + portNumber + "/" + sellerId);

            return clientProxy.Buy(sellerId, UserID, goodId);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int promptForGoodId() {
        try {
            System.out.println("Please Introduce the Good ID you intend to sell:");
            System.out.print("Good ID: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            while (!tryParseInt(input)) {
                System.out.println("The Introduced ID is invalid, please type only the number of the Good ID you want to sell");
                System.out.print("Good ID: ");
                input = reader.readLine();
            }

            return Integer.parseInt(input);

        } catch (Exception e) {
            System.out.println("Something went wrong during prompting for Good ID");
            return -1;
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
        System.out.print("Please Introduce The Desired Option Number: \n 1. Sell an Item. \n 2. Buy an Item. \n 3. Get Item State. \n Option Number: ");
    }



    public String Buy(int ownerId, int newOwnerId, int goodId) {
        try {
            iProxy proxy = (iProxy) Naming.lookup("rmi://localhost:8086/Notary");
            return proxy.transferGood(ownerId, newOwnerId, goodId);
        } catch (Exception e) {
            System.out.println("Something Went Wrong During the Transfer");
            return "The Good Transfer Has Failed. Please Try Again.";
        }
    }

}
