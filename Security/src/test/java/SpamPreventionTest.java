import org.apache.commons.codec.binary.Hex;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SpamPreventionTest {

    @Before
    public void setUp() {
    }

    @Test
    public void xhashcashGeneratorTest() {
        try {
            String[] spamPrevention = SpamPrevention.xhashcashGenerator(12345678, 8086);
            System.out.println("Header: " + spamPrevention[0]);
            System.out.println("Hash: " + spamPrevention[1]);
            Assert.assertTrue(spamPrevention[0].length() > 31); //Length of "X-Hashcash: 1:20:12345678:8086:"
            Assert.assertTrue(spamPrevention[1].length() == 40); //160 bits

            String header = spamPrevention[0];
            Assert.assertTrue(header.startsWith(SpamPrevention.baseHeaderGenerator(12345678, 8086)));

            String headerDigest = spamPrevention[1];
            Assert.assertTrue(headerDigest.startsWith(SpamPrevention.zerosSubstringGenerator(SpamPrevention.ZERO_BITS / SpamPrevention.BITS_PER_HEXA)));

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            String hash = Hex.encodeHexString(digest.digest(spamPrevention[0].getBytes(StandardCharsets.UTF_8)));
            Assert.assertEquals(headerDigest, hash);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void xhashcashValidatorSuccessfulTest() {
        try {
            String[] spamPrevention = new String[2];

            // Correct case
            spamPrevention[0] = "X-Hashcash: 1:20:12345678:8086:-530047689";
            spamPrevention[1] = "00000399a53319d191fd33727c9e3ba3f9a8290c";
            Assert.assertTrue(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));

            // Not enough zeros
            spamPrevention[0] = "X-Hashcash: 1:16:12345678:8086:1212368772";
            spamPrevention[1] = "0000c6f8abe071b16a54785be1d660e1a99bfbb4";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));

            // More than enough zeros
            spamPrevention[0] = "X-Hashcash: 1:24:12345678:8086:211717796";
            spamPrevention[1] = "000000deed2fc43d1013df602df2339bf59e0710";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));

            // Hash not matching header
            spamPrevention[0] = "X-Hashcash: 1:20:12345678:8086:-530047689";
            spamPrevention[1] = "000000deed2fc43d1013df602df2339bf59e0710";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));
            spamPrevention[1] = "     ";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));
            spamPrevention[1] = "";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));
            spamPrevention[1] = null;
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));
            spamPrevention[1] = "00000399a53319d191fd33727c9e3ba3f9a8290c";
            spamPrevention[0] = "     ";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));
            spamPrevention[0] = "";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));
            spamPrevention[0] = null;
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));
            spamPrevention[1] = null;
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));

            // Wrong nounce and server port
            spamPrevention[0] = "X-Hashcash: 1:20:12345678:8086:-530047689";
            spamPrevention[1] = "00000399a53319d191fd33727c9e3ba3f9a8290c";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345679, 8086));
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8087));

            //Incorrect spamPrevention array
            spamPrevention = new String[1];
            spamPrevention[0] = "X-Hashcash: 1:20:12345678:8086:-530047689";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));
            spamPrevention[0] = "00000399a53319d191fd33727c9e3ba3f9a8290c";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));
            spamPrevention = new String[3];
            spamPrevention[0] = "X-Hashcash: 1:20:12345678:8086:-530047689";
            spamPrevention[1] = "00000399a53319d191fd33727c9e3ba3f9a8290c";
            Assert.assertFalse(SpamPrevention.xhashcashValidator(spamPrevention, 12345678, 8086));
            Assert.assertFalse(SpamPrevention.xhashcashValidator(new String[3], 12345678, 8086));
            Assert.assertFalse(SpamPrevention.xhashcashValidator(new String[2], 12345678, 8086));
            Assert.assertFalse(SpamPrevention.xhashcashValidator(new String[1], 12345678, 8086));
            Assert.assertFalse(SpamPrevention.xhashcashValidator(null, 12345678, 8086));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void zeroSubstringGeneratorTest() {
        try {
            Assert.assertEquals("", SpamPrevention.zerosSubstringGenerator(-4));
            Assert.assertEquals("", SpamPrevention.zerosSubstringGenerator(-1));
            Assert.assertEquals("", SpamPrevention.zerosSubstringGenerator(0));
            Assert.assertEquals("", SpamPrevention.zerosSubstringGenerator(1));
            Assert.assertEquals("", SpamPrevention.zerosSubstringGenerator(2));
            Assert.assertEquals("", SpamPrevention.zerosSubstringGenerator(3));
            Assert.assertEquals("0", SpamPrevention.zerosSubstringGenerator(4));
            Assert.assertEquals("00", SpamPrevention.zerosSubstringGenerator(8));
            Assert.assertEquals("000", SpamPrevention.zerosSubstringGenerator(12));
            Assert.assertEquals("0000", SpamPrevention.zerosSubstringGenerator(16));
            Assert.assertEquals("00000", SpamPrevention.zerosSubstringGenerator(20));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void baseHeaderGeneratorTest() {
        try {
            Assert.assertEquals("X-Hashcash: 1:20:12345678:8086:", SpamPrevention.baseHeaderGenerator(12345678, 8086));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @After
    public void tearDown() {
    }
}
