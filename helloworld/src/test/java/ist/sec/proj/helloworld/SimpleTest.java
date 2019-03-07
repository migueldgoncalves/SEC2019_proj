package ist.sec.proj.helloworld;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleTest {

    @Before
    public void setUp() {
        System.out.println("Setting up");
    }

    @Test
    public void simpleSumTest() {
        assertEquals(5, 10/2);
    }

    @After
    public void tearDown() {
        System.out.println("Tearing down");
    }
}
