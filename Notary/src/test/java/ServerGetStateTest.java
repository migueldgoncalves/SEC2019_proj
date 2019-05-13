import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Objects;

public class ServerGetStateTest {

    private Server servidor;

    private Request state = null;

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
    public void methodGetStateOfGoodSuccessful() {
        try {
            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            sellGoodRequestGenerator(1, 1);

            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<1, On-Sale>", Objects.requireNonNull(state).getAnswer());

            transferGoodRequestGenerator(1, 1, 2);

            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<2, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodInvalidGood() {
        try {
            state = getGoodStateRequestGenerator(-1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The GoodId -1 Is Not Present In The Server!", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(0, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The GoodId 0 Is Not Present In The Server!", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(10, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("The GoodId 10 Is Not Present In The Server!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodInvalidUser() {
        try {
            state = getGoodStateRequestGenerator(1, -1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Invoke Method Get State Of Good in Server!", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(1, 0, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Invoke Method Get State Of Good in Server!", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(1, 2, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Invoke Method Get State Of Good in Server!", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(1, 10, 0, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("Invalid Authorization to Invoke Method Get State Of Good in Server!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodWithSellerId() {
        try {
            state = getGoodStateRequestGenerator(1, 1, -1, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(1, 1, 1, 0, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodWithBuyerId() {
        try {
            state = getGoodStateRequestGenerator(1, 1, 0, -1, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(1, 1, 0, 1, 0, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodWithBuyerNonce() {
        try {
            state = getGoodStateRequestGenerator(1, 1, 0, 0, -1, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(1, 1, 0, 0, 1, 0, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodWithBuyerKey() {
        try {
            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, -1, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 1, new Date().getTime(), 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodInvalidNonce() {
        try {
            long nounce = new Date().getTime();
            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, nounce, 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            nounce++;
            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, nounce, 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, nounce, 1, 0, null);
            Assert.assertEquals("This message has already been processed!", Objects.requireNonNull(state).getAnswer());

            nounce--;
            nounce--;
            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, nounce, 1, 0, null);
            Assert.assertEquals("This message has already been processed!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodInvalidUserKey() {
        try {
            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 2, 0, null);
            Assert.assertEquals("Invalid Authorization to Invoke Method Get State Of Good in Server!", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 3, 0, null);
            Assert.assertEquals("Invalid Authorization to Invoke Method Get State Of Good in Server!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodWithNotaryId() {
        try {
            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, -1, null);
            Assert.assertEquals("As a Notary, you cannot invoke this method!", Objects.requireNonNull(state).getAnswer());

            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 1, null);
            Assert.assertEquals("As a Notary, you cannot invoke this method!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodWithAnswer() {
        try {
            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, "answer");
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void methodGetStateOfGoodReplayAttack() {
        try {
            long nounce = new Date().getTime();
            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, nounce, 1, 0, null);
            Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(state).getAnswer());

            // Same request
            state = getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, nounce, 1, 0, null);
            Assert.assertEquals("This message has already been processed!", Objects.requireNonNull(state).getAnswer());

            ensureServerIsOkAfterAttack(false);
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
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User1.key"), gson.toJson(pedido)));
            Request temp = gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("<1, Not-On-Sale>", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            pedido.setNounce(new Date().getTime());
            pedido.setSignature(null);
            temp = gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
            Assert.assertEquals("Invalid Authorization To Invoke Method Sell on Server!", temp.getAnswer());

            ensureServerIsOkAfterAttack(false);
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

    private Request getGoodStateRequestGenerator(int goodId, int userId, int sellerId, int buyerId, long buyerNonce, long buyerKeyId, long nounce, long keyId, int notaryId, String answer) {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setGoodId(goodId);
            pedido.setUserId(userId);
            pedido.setSellerId(sellerId);
            pedido.setBuyerId(buyerId);
            pedido.setBuyerNounce(buyerNonce);
            if(buyerKeyId >= 1 && buyerKeyId <= 9)
                pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User" + buyerKeyId + ".key"), gson.toJson(pedido)));
            else
                pedido.setBuyerSignature(null);
            pedido.setNounce(nounce);
            pedido.setNotaryId(notaryId);
            pedido.setAnswer(answer);
            if(keyId >= 1 && keyId <= 9)
                pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User" + keyId + ".key"), gson.toJson(pedido)));
            else
                pedido.setSignature(null);

            return gson.fromJson(servidor.getStateOfGood(gson.toJson(pedido)), Request.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Request sellGoodRequestGenerator(int userId, int goodId) {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();

            pedido.setUserId(userId);
            pedido.setGoodId(goodId);
            pedido.setNounce(new Date().getTime());
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User" + userId + ".key"), gson.toJson(pedido)));

            return gson.fromJson(servidor.sell(gson.toJson(pedido)), Request.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Request transferGoodRequestGenerator(int goodId, int sellerId, int buyerId) {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            long buyerNonce = new Date().getTime();

            pedido.setGoodId(goodId);
            pedido.setUserId(buyerId);
            pedido.setSellerId(sellerId);
            pedido.setBuyerId(buyerId);
            pedido.setNounce(buyerNonce);
            pedido.setBuyerSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User" + buyerId + ".key"), gson.toJson(pedido)));
            pedido.setUserId(sellerId);
            pedido.setNounce(new Date().getTime());
            pedido.setBuyerNounce(buyerNonce);
            pedido.setSignature(SignatureGenerator.generateSignature(RSAKeyLoader.getPriv(System.getProperty("user.dir").replace("\\Notary", "") + "\\Client\\src\\main\\resources\\User" + sellerId + ".key"), gson.toJson(pedido)));

            return gson.fromJson(servidor.transferGood(gson.toJson(pedido)), Request.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void ensureServerIsOkAfterAttack(boolean goodOnSale) {
        try {
            if(!goodOnSale) {
                Assert.assertEquals("<1, Not-On-Sale>", Objects.requireNonNull(getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null)).getAnswer());
            } else {
                Assert.assertEquals("<1, On-Sale>", Objects.requireNonNull(getGoodStateRequestGenerator(1, 1, 0, 0, 0, 0, new Date().getTime(), 1, 0, null)).getAnswer());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
