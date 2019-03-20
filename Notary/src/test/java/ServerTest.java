import com.google.gson.Gson;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {

    private Server servidor;

    @Before
    public void setUp() {
        try {
            servidor = new Server("src\\main\\resources\\GoodsFile1.xml");
        } catch (Exception e) {
            System.out.println("Something Went Wrong In The System");
        }
    }

    @Test
    public void methodSellSuccess() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(1);
            String temp = servidor.sell(gson.toJson(pedido));
            Assert.assertEquals("The Item is Now on Sale", temp);
            temp = servidor.sell(gson.toJson(pedido));
            Assert.assertEquals("The Item was Already On Sale", temp);
        } catch (Exception e) {
            System.out.println("Something Went Wrong In The System");
        }
    }

    @Test
    public void methodSellUnsuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(0);
            pedido.setGoodId(0);
            String temp = servidor.sell(gson.toJson(pedido));
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", temp);
        } catch (Exception e) {
            System.out.println("Something Went Wrong In The System");
        }
    }

    @Test
    public void methodGetStateOfGoodSuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setGoodId(1);
            String temp = servidor.getStateOfGood(gson.toJson(pedido));
            Assert.assertEquals("<1, Not-On-Sale>", temp);
            pedido.setUserId(1);
            servidor.sell(gson.toJson(pedido));
            temp = servidor.getStateOfGood(gson.toJson(pedido));
            Assert.assertEquals("<1, On-Sale>", temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void methodGetStateOfGoodUnsuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setGoodId(10);
            String temp = servidor.getStateOfGood(gson.toJson(pedido));
            Assert.assertEquals("The GoodId 10 Is Not Present In The Server!", temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void methodTransferGoodSuccessful() {
        try {
            Gson gson = new Gson();
            Request pedido = new Request();
            pedido.setUserId(1);
            pedido.setGoodId(1);
            String state = servidor.getStateOfGood(gson.toJson(pedido));
            Assert.assertEquals("<1, Not-On-Sale>", state);
            state = servidor.sell(gson.toJson(pedido));
            Assert.assertEquals("The Item is Now on Sale", state);
            pedido.setSellerId(1);
            pedido.setBuyerId(2);
            String temp = servidor.transferGood(gson.toJson(pedido));
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp);
            Assert.assertEquals("<2, Not-On-Sale>", servidor.getStateOfGood(gson.toJson(pedido)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() {

    }
}
