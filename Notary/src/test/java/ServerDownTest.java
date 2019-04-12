import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.PrintWriter;

public class ServerDownTest {

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
