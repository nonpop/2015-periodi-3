package tl15.utils;

import tl15.utils.BitInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author nonpop
 */
public class BitInputStreamTest {
    @Test
    public void testZeroBytes() throws IOException {
        byte[] data = new byte[]{};
        InputStream ins = new ByteArrayInputStream(data);
        BitInputStream bins = new BitInputStream(ins);
        assertEquals((Integer)0, bins.readBits(0));
        assertEquals((Integer)0, bins.readBits(0));
        assertEquals(null, bins.readBits(1));
    }

    @Test
    public void testOneByte() throws Exception {
        byte[] data = new byte[]{0};
        InputStream ins = new ByteArrayInputStream(data);
        BitInputStream bins = new BitInputStream(ins);
        assertEquals((Integer)0, bins.readBits(8));
        assertEquals(null, bins.readBits(1));

        data = new byte[]{0b01001101};
        ins = new ByteArrayInputStream(data);
        bins = new BitInputStream(ins);
        assertEquals((Integer)0, bins.readBits(1));
        assertEquals((Integer)1, bins.readBits(1));
        assertEquals((Integer)0, bins.readBits(1));
        assertEquals((Integer)0, bins.readBits(1));
        assertEquals((Integer)1, bins.readBits(1));
        assertEquals((Integer)1, bins.readBits(1));
        assertEquals((Integer)0, bins.readBits(1));
        assertEquals((Integer)1, bins.readBits(1));
        assertEquals(null, bins.readBits(1));

        data = new byte[]{0b01001101};
        ins = new ByteArrayInputStream(data);
        bins = new BitInputStream(ins);
        assertEquals((Integer)0b010, bins.readBits(3));
        assertEquals((Integer)0b0110, bins.readBits(4));
        assertEquals(null, bins.readBits(2));

        data = new byte[]{-1};
        ins = new ByteArrayInputStream(data);
        bins = new BitInputStream(ins);
        assertEquals((Integer)1, bins.readBits(1));
        assertEquals((Integer)0x7f, bins.readBits(7));
    }

    @Test
    public void testTwoBytes() throws Exception {
        byte[] data = new byte[]{0, 0};
        InputStream ins = new ByteArrayInputStream(data);
        BitInputStream bins = new BitInputStream(ins);
        assertEquals((Integer)0, bins.readBits(16));
        assertEquals(null, bins.readBits(1));

        data = new byte[]{0b01101001, 0b00011010};
        ins = new ByteArrayInputStream(data);
        bins = new BitInputStream(ins);
        assertEquals((Integer)0b01101, bins.readBits(5));
        assertEquals((Integer)0b00100, bins.readBits(5));
        assertEquals((Integer)0b01101, bins.readBits(5));
        assertEquals((Integer)0b0, bins.readBits(1));
        assertEquals(null, bins.readBits(1));

        data = new byte[]{0b01101001, 0b00011010};
        ins = new ByteArrayInputStream(data);
        bins = new BitInputStream(ins);
        assertEquals((Integer)0b0110100100011010, bins.readBits(16));
    }

    @Test
    public void testBigByte() throws Exception {
        byte[] data = new byte[]{ (byte)0xff };
        InputStream ins = new ByteArrayInputStream(data);
        BitInputStream bins = new BitInputStream(ins);
        assertEquals((Integer)255, bins.readBits(8));
    }
}
