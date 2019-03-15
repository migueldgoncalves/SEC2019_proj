import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {

    private Server servidor;

    @Before
    public void setUp() {
        try {
            servidor = new Server();
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
            Assert.assertEquals("The Item is Already On Sale", temp);
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

    @After
    public void tearDown() {

    }
}
