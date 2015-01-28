package bitstream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BitOutputStreamTest {

    @Test
    public void testZeroBytes() throws IOException {
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        BitOutputStream bouts = new BitOutputStream(outs);
        bouts.close();
        assertEquals(0, outs.toByteArray().length);
    }

    @Test
    public void testOneByte() throws Exception {
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        BitOutputStream bouts = new BitOutputStream(outs);
        bouts.writeBits(1, 1);
        bouts.close();
        assertEquals(1, outs.toByteArray().length);
        assertEquals(0x80, (outs.toByteArray()[0] & 0xff));

        outs = new ByteArrayOutputStream();
        bouts = new BitOutputStream(outs);
        bouts.writeBits(1, 0);
        bouts.writeBits(1, 1);
        bouts.writeBits(1, 0);
        bouts.writeBits(1, 0);
        bouts.writeBits(1, 1);
        bouts.writeBits(1, 1);
        bouts.writeBits(1, 0);
        bouts.writeBits(1, 1);
        bouts.close();
        assertEquals(1, outs.toByteArray().length);
        assertEquals(0b01001101, outs.toByteArray()[0]);

        outs = new ByteArrayOutputStream();
        bouts = new BitOutputStream(outs);
        bouts.writeBits(3, 0b010);
        bouts.writeBits(5, 0b01101);
        bouts.close();
        assertEquals(1, outs.toByteArray().length);
        assertEquals(0b01001101, outs.toByteArray()[0]);

        outs = new ByteArrayOutputStream();
        bouts = new BitOutputStream(outs);
        bouts.writeBits(3, 0b010);
        bouts.writeBits(4, 0b0110);
        bouts.close();
        assertEquals(1, outs.toByteArray().length);
        assertEquals(0b01001100, outs.toByteArray()[0]);

        outs = new ByteArrayOutputStream();
        bouts = new BitOutputStream(outs);
        bouts.writeBits(8, -1);
        bouts.close();
        assertEquals(1, outs.toByteArray().length);
        assertEquals(-1, outs.toByteArray()[0]);
    }

    @Test
    public void testTwoBytes() throws Exception {
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        BitOutputStream bouts = new BitOutputStream(outs);
        bouts.writeBits(16, 0);
        bouts.close();
        assertEquals(2, outs.toByteArray().length);
        assertEquals(0, outs.toByteArray()[0]);
        assertEquals(0, outs.toByteArray()[1]);

        outs = new ByteArrayOutputStream();
        bouts = new BitOutputStream(outs);
        bouts.writeBits(16, 0b0110100100011010);
        bouts.close();
        assertEquals(2, outs.toByteArray().length);
        assertEquals(0b01101001, outs.toByteArray()[0]);
        assertEquals(0b00011010, outs.toByteArray()[1]);
    }

    @Test
    public void testRandom() throws IOException {
        Random r = new Random(42);
        int dataSize = 10000;
        byte[] data = new byte[dataSize];
        for (int i = 0; i < dataSize; ++i) {
            data[i] = (byte) (r.nextInt(256) - 128);
        }

        InputStream ins = new ByteArrayInputStream(data);
        BitInputStream bins = new BitInputStream(ins);
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        BitOutputStream bouts = new BitOutputStream(outs);
        int howManyRead = 0;
        while (howManyRead < dataSize * 8) {
            int n = r.nextInt(33);
            while (n + howManyRead > dataSize * 8) {
                n = r.nextInt(33);
            }
            int bits = bins.readBits(n);
            bouts.writeBits(n, bits);
            howManyRead += n;
        }
        bouts.close();
        assertArrayEquals(data, outs.toByteArray());
    }
}
