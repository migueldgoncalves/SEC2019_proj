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
            String temp = servidor.sell(1, 1);
            Assert.assertEquals("The Item is Now on Sale", temp);
            temp = servidor.sell(1, 1);
            Assert.assertEquals("The Item was Already On Sale", temp);
        } catch (Exception e) {
            System.out.println("Something Went Wrong In The System");
        }
    }

    @Test
    public void methodSellUnsuccessful() {
        try {
            String temp = servidor.sell(0, 0);
            Assert.assertEquals("The Requested Item To Be Put on Sell Is Not Available In The System", temp);
        } catch (Exception e) {
            System.out.println("Something Went Wrong In The System");
        }
    }

    @Test
    public void methodGetStateOfGoodSuccessful() {
        try {
            String temp = servidor.getStateOfGood(1);
            Assert.assertEquals("<1, Not-On-Sale>", temp);
            servidor.sell(1, 1);
            temp = servidor.getStateOfGood(1);
            Assert.assertEquals("<1, On-Sale>", temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void methodGetStateOfGoodUnsuccessful() {
        try {
            String temp = servidor.getStateOfGood(10);
            Assert.assertEquals("The GoodId 10 Is Not Present In The Server!", temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void methodTransferGoodSuccessful() {
        try {
            String state = servidor.getStateOfGood(1);
            Assert.assertEquals("<1, Not-On-Sale>", state);
            state = servidor.sell(1, 1);
            Assert.assertEquals("The Item is Now on Sale", state);
            String temp = servidor.transferGood(1, 2, 1);
            Assert.assertEquals("The Good with Good ID 1 Has now Been transfered to the new Owner with Owner ID 2", temp);
            Assert.assertEquals("<2, Not-On-Sale>", servidor.getStateOfGood(1));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() {

    }
}
