import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;

public class ServerTransferTest {

    private Server servidor;

    @Before
    public void setUp() {
        try {
            servidor = new Server("src\\main\\resources\\GoodsFile1.xml");

            //Cleans server backup file
            PrintWriter writer = new PrintWriter(new File(System.getProperty("user.dir") + "\\Backups\\ServerState.old"));
            writer.println("");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Something Went Wrong In The System");
        }
    }

    @Test
    public void methodTransferGoodSuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", state.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());

            pedido.setUserId(1);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setSignature(null);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidUser() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(-1);
            pedido.setGoodId(1);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Transfer Good!", temp.getAnswer());

            pedido.setUserId(0);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Transfer Good!", temp.getAnswer());

            pedido.setUserId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Transfer Good!", temp.getAnswer());

            pedido.setUserId(10);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Transfer Good!", temp.getAnswer());

            // Ensures server is ok after attack

            pedido.setUserId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", state.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());

            pedido.setSignature(null);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidGood() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(-1);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setGoodId(0);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setGoodId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setGoodId(10);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            // Ensures server is ok after attack

            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", state.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());

            pedido.setSignature(null);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidSellerGoodNotAtSale() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setSellerId(2);
            pedido.setBuyerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setUserId(2);
            pedido.setSellerId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Transfer Good!", temp.getAnswer());

            pedido.setUserId(2);
            pedido.setSellerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Transfer Good!", temp.getAnswer());

            pedido.setUserId(1);
            pedido.setSellerId(-1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setSellerId(0);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setSellerId(10);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            // Ensures server is ok after attack

            pedido.setSellerId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", state.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());

            pedido.setSignature(null);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidSellerGoodAtSale() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());

            pedido.setSellerId(2);
            pedido.setBuyerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setUserId(2);
            pedido.setSellerId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Transfer Good!", temp.getAnswer());

            pedido.setUserId(2);
            pedido.setSellerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization to Transfer Good!", temp.getAnswer());

            pedido.setUserId(1);
            pedido.setSellerId(-1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setSellerId(0);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setSellerId(10);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            // Ensures server is ok after attack

            pedido.setSellerId(1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, On-Sale>", state.getAnswer());

            pedido.setSignature(null);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidBuyerGoodNotAtSale() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setSellerId(1);
            pedido.setBuyerId(-1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setBuyerId(0);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setBuyerId(10);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            // Ensures server is ok after attack

            pedido.setBuyerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", state.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());

            pedido.setSignature(null);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodInvalidBuyerGoodAtSale() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setSellerId(1);
            pedido.setBuyerId(-1);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setBuyerId(0);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setBuyerId(10);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            // Ensures server is ok after attack

            pedido.setBuyerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", state.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());

            pedido.setSignature(null);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodTransferGoodReplayAttack() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            // Same request
            temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("This message has already been processed!", temp.getAnswer());

            // Ensure server is ok after the attack

            pedido.setBuyerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", state.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());

            pedido.setSignature(null);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodTamperingAttack() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(1);
            pedido.setGoodId(1);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good Id, Owner Id or New Owner ID is not present in the server!", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            // Ensure server is ok after the attack

            pedido.setBuyerId(2);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request state = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", state.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            state = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Item is Now on Sale", state.getAnswer());

            pedido.setSignature(null);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User2.key"), gson.toJson(pedido)));
            temp = gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Assert.assertEquals("<2, Not-On-Sale>", gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @After
    public void tearDown() {
        try {
            //Cleans server backup file
            PrintWriter writer = new PrintWriter(new File(System.getProperty("user.dir") + "\\Backups\\ServerState.old"));
            writer.println("");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
