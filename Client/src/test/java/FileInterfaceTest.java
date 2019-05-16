import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Objects;

public class FileInterfaceTest {

    @Before
    public void setUp() {
        for(int i=1; i<=9; i++) {
            FileInterface.writeTimestamps(i, 0, 0);
        }
    }

    @Test
    public void fileReadSuccessful() {
        for(int i=1; i<=9; i++) {
            Assert.assertEquals(0, Objects.requireNonNull(FileInterface.readTimestamps(i))[0]);
            Assert.assertEquals(0, Objects.requireNonNull(FileInterface.readTimestamps(i))[1]);
        }
    }

    @Test
    public void fileReadInvalidClientId() {
        Assert.assertNull(FileInterface.readTimestamps(-1));
        Assert.assertNull(FileInterface.readTimestamps(0));
        Assert.assertNull(FileInterface.readTimestamps(10));
    }

    @Test
    public void fileWriteSuccessful() {
        for(int i=1; i<=9; i++) {
            FileInterface.writeTimestamps(i, i+1, i+2);
        }
        for(int i=1; i<=9; i++) {
            Assert.assertEquals(i+1, Objects.requireNonNull(FileInterface.readTimestamps(i))[0]);
            Assert.assertEquals(i+2, Objects.requireNonNull(FileInterface.readTimestamps(i))[1]);
        }
    }

    @Test
    public void fileWriteInvalidClientId() {
        FileInterface.writeTimestamps(-1, 1, 2);
        FileInterface.writeTimestamps(0, 1, 2);
        FileInterface.writeTimestamps(10, 1, 2);
        for(int i=1; i<=9; i++) {
            Assert.assertEquals(0, Objects.requireNonNull(FileInterface.readTimestamps(i))[0]);
            Assert.assertEquals(0, Objects.requireNonNull(FileInterface.readTimestamps(i))[1]);
        }
    }

    @After
    public void tearDown() {
        for(int i=1; i<=9; i++) {
            FileInterface.writeTimestamps(i, 0, 0);
        }
    }
}
