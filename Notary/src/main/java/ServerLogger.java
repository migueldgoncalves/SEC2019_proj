import RequestClasses.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

class ServerLogger {

    private static ServerLogger single_instance = null;

    private ServerLogger() {}

    static ServerLogger getInstance()
    {
        if (single_instance == null)
            single_instance = new ServerLogger();

        return single_instance;
    }

/*    public enum OPCODE {
        TRANSFERGOOD, SELLGOOD, GETSTATEOFGOOD, PREPARE_SELL
    }*/

//    /**
//     * Method That updates the logs of the Server
//     * @param operation The operation executed
//     * @param pedido The Request Object Sent By The Client
//     * @param result The result of the executed operation that had as argument the Request object
//     */
//    synchronized void updateServerLog(OPCODE operation, Request pedido, String result) {
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
//            switch (operation) {
//                case GETSTATEOFGOOD:
//                    writer.write("Operation: Get State of Good\n");
//                    writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
//                    writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
//                    writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
//                    writer.write("Requested AnswerClasses.Good ID: " + pedido.getGoodId() + "\n");
//                    writer.write("Nounce: " + pedido.getNounce() + "\n");
//                    writer.write("Operation Result: " + result + "\n");
//                    writer.write("---------------------------------------------------------------------------------------------------------------\n");
//                    writer.close();
//                    break;
//                case SELLGOOD:
//                    writer.write("Operation: Sell Good\n");
//                    writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
//                    writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
//                    writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
//                    writer.write("Requested AnswerClasses.Good ID: " + pedido.getGoodId() + "\n");
//                    writer.write("Nounce: " + pedido.getNounce() + "\n");
//                    writer.write("Operation Result: " + result + "\n");
//                    writer.write("---------------------------------------------------------------------------------------------------------------\n");
//                    writer.close();
//                    break;
//                case TRANSFERGOOD:
//                    writer.write("Operation: Transfer Good\n");
//                    writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
//                    writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
//                    writer.write("Seller ID: " + pedido.getSellerId() + "\n");
//                    writer.write("Seller Signature: " + Arrays.toString(pedido.getSignature()) + "\n");
//                    writer.write("Buyer ID: " + pedido.getBuyerId() + "\n");
//                    writer.write("Buyer Signature: " + Arrays.toString(pedido.getBuyerSignature()) + "\n");
//                    writer.write("AnswerClasses.Good ID: " + pedido.getGoodId() + "\n");
//                    writer.write("Nounce: " + pedido.getNounce() + "\n");
//                    writer.write("Operation Result: " + result + "\n");
//                    writer.write("---------------------------------------------------------------------------------------------------------------\n");
//                    writer.close();
//                    break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Something Went Wrong During Server Log Update");
//        }
//    }
//
//    /**
//     * Method That updates the logs of the Server
//     * @param pedido The RequestClasses.PrepareSellRequest Object Sent By The Client
//     * @param result The JsonString of The Modified AnswerClasses.Good Returned By The Server or The String of Error
//     */
//    synchronized void updateServerLogPrepareSell(PrepareSellRequest pedido, String result) {
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
//            writer.write("Operation: Prepare Sell\n");
//            writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
//            writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
//            writer.write("AnswerClasses.Good ID: " + pedido.getGoodId() + "\n");
//            writer.write("Nounce: " + pedido.getNounce() + "\n");
//            writer.write("Read ID: " + pedido.getReadId() + "\n");
//            writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
//            writer.write("Operation Result: " + result + "\n");
//            writer.write("---------------------------------------------------------------------------------------------------------------\n");
//            writer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Something Went Wrong During Server Log Update");
//        }
//    }
//
//    synchronized void updateServerLogPrepareTransfer(PrepareTransferRequest pedido, String result) {
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
//            writer.write("Operation: Prepare Sell\n");
//            writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
//            writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
//            writer.write("Good ID: " + pedido.getBuyerRequest().getGoodId() + "\n");
//            writer.write("Nounce: " + pedido.getNounce() + "\n");
//            writer.write("Read ID: " + pedido.getReadId() + "\n");
//            writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
//            writer.write("Operation Result: " + result + "\n");
//            writer.write("---------------------------------------------------------------------------------------------------------------\n");
//            writer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Something Went Wrong During Server Log Update");
//        }
//    }
//
//    /**
//     * Method That updates the logs of the Server
//     * @param pedido The RequestClasses.SellRequest Object Sent By The Client
//     * @param result The JsonString of The Modified AnswerClasses.Good Returned By The Server or The String of Error
//     */
//    synchronized void updateServerLogSell(SellRequest pedido, String result) {
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
//            writer.write("Operation: Prepare Sell\n");
//            writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
//            writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
//            writer.write("Good ID: " + pedido.getRequests().get(0).getGood().getGoodId() + "\n");
//            writer.write("Nounce: " + pedido.getNounce() + "\n");
//            writer.write("Write Time Stamp: " + pedido.getRequests().get(0).getGood().getWriteTimeStampOfGood());
//            writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
//            writer.write("Operation Result: " + result + "\n");
//            writer.write("---------------------------------------------------------------------------------------------------------------\n");
//            writer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Something Went Wrong During Server Log Update");
//        }
//    }
//
//    /**
//     * Method That updates the logs of the Server
//     * @param pedido The RequestClasses.SellRequest Object Sent By The Client
//     * @param result The JsonString of The Modified AnswerClasses.Good Returned By The Server or The String of Error
//     */
//    synchronized void updateServerLogTransferGood(TransferGoodRequest pedido, String result) {
//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
//            writer.write("Operation: Prepare Sell\n");
//            writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
//            writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
//            writer.write("Good ID: " + pedido.getBuyerAnswer().getNotaryAnswers().get(0).getGood().getGoodId() + "\n");
//            writer.write("Nounce: " + pedido.getNounce() + "\n");
//            writer.write("Write Time Stamp: " + pedido.getWriteTimeStamp());
//            writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
//            writer.write("Operation Result: " + result + "\n");
//            writer.write("---------------------------------------------------------------------------------------------------------------\n");
//            writer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Something Went Wrong During Server Log Update");
//        }
//    }

    synchronized void LogTransferGood(TransferGoodRequest pedido, String result) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
            writer.write("Operation: Transfer Good\n");
            writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
            writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
            writer.write("Nounce: " + pedido.getNounce() + "\n");
            writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
            writer.write("Operation Result: " + result + "\n");
            writer.write("---------------------------------------------------------------------------------------------------------------\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something Went Wrong During Server Log Update");
        }
    }

    synchronized void LogPrepareTransferGood(PrepareTransferRequest pedido, String result) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
            writer.write("Operation: Prepare Transfer Good\n");
            writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
            writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
            writer.write("Nounce: " + pedido.getNounce() + "\n");
            writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
            writer.write("Operation Result: " + result + "\n");
            writer.write("---------------------------------------------------------------------------------------------------------------\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something Went Wrong During Server Log Update");
        }
    }

    synchronized void LogSell(SellRequest pedido, String result) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
            writer.write("Operation: Sell Good\n");
            writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
            writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
            writer.write("Nounce: " + pedido.getNounce() + "\n");
            writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
            writer.write("Operation Result: " + result + "\n");
            writer.write("---------------------------------------------------------------------------------------------------------------\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something Went Wrong During Server Log Update");
        }
    }

    synchronized void LogPrepareSell(PrepareSellRequest pedido, String result) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
            writer.write("Operation: Prepare Sell\n");
            writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
            writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
            writer.write("Good ID: " + pedido.getGoodId() + "\n");
            writer.write("Nounce: " + pedido.getNounce() + "\n");
            writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
            writer.write("Operation Result: " + result + "\n");
            writer.write("---------------------------------------------------------------------------------------------------------------\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something Went Wrong During Server Log Update");
        }
    }

    synchronized void LogGetStateOfGood(GetStateRequest pedido, String result) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("ServerLog.txt", true));
            writer.write("Operation: Get State Of Good\n");
            writer.write("Time of Operation Completion: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
            writer.write("User ID of Method Caller: " + pedido.getUserId() + "\n");
            writer.write("Good ID: " + pedido.getGoodId() + "\n");
            writer.write("Nounce: " + pedido.getNounce() + "\n");
            writer.write("User Signature of Method Caller: " + Arrays.toString(pedido.getSignature()) + "\n");
            writer.write("Operation Result: " + result + "\n");
            writer.write("---------------------------------------------------------------------------------------------------------------\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something Went Wrong During Server Log Update");
        }
    }


}
