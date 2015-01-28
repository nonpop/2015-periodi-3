package lzw;

import bitstream.BitInputStream;
import bitstream.BitOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class LZWTest {
    private void testCompress(int[] expected, byte[] data) throws IOException {
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        BitOutputStream bouts = new BitOutputStream(outs);
        LZW.compress(new ByteArrayInputStream(data), bouts);
        bouts.close();
        BitInputStream bins = new BitInputStream(new ByteArrayInputStream(outs.toByteArray()));
        for (int i : expected) {
            Integer next = bins.readBits(LZW.codeSize);
            assertNotNull(next);
            assertEquals(i, (int)next);
        }
    }
    
    @Test
    public void testCompress() throws IOException {
        testCompress(new int[]{0,1,256,258}, new byte[]{0,1,0,1,0,1,0});
        testCompress(new int[]{}, new byte[]{});
        testCompress(new int[]{1}, new byte[]{1});
        testCompress(new int[]{1,1}, new byte[]{1,1});
        testCompress(new int[]{1,256}, new byte[]{1,1,1});
        testCompress(new int[]{1,256,257,258}, new byte[]{1,1,1,1,1,1,1,1,1,1});
        testCompress(new int[]{0,1,2,3,258,4,3,5,4,257,3}, new byte[]{0,1,2,3,2,3,4,3,5,4,1,2,3});
        testCompress(new int[]{0,1,2,3,4,5,6,7,8,9}, new byte[]{0,1,2,3,4,5,6,7,8,9});


//        data = new ArrayList<>(Arrays.asList(0,1,2,3,2,3,4,3,5,4,1,2,3));
//        expResult = new ArrayList<>(Arrays.asList(0,1,2,3,258,4,3,5,4,257,3));
//        result = LZW.compress(data);
//        assertEquals(expResult, result);
    }


    public void testDecompress(byte[] data) throws IOException {
        InputStream ins = new ByteArrayInputStream(data);
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        BitOutputStream bouts = new BitOutputStream(outs);
        LZW.compress(ins, bouts);
        bouts.close();
        ins = new ByteArrayInputStream(outs.toByteArray());
        outs = new ByteArrayOutputStream();
        LZW.decompress(new BitInputStream(ins), outs);
        assertArrayEquals(data, outs.toByteArray());
    }
    
    private byte[] randomData(int size) {
        byte[] res = new byte[size];
        Random r = new Random(42);
        for (int i = 0; i < size; ++i) {
            res[i] = (byte)(r.nextInt(256) - 128);
        }
        return res;
    }
    
    @Test
    public void testDecompress() throws IOException {
        testDecompress(new byte[]{});
        testDecompress(new byte[]{1});
        testDecompress(new byte[]{1,1});
        testDecompress(new byte[]{1,1,1});
        testDecompress(new byte[]{1,1,1,1,1,1,1,1,1,1});
        testDecompress(new byte[]{1,2,1,1,2});
        testDecompress(new byte[]{0,1,0,1,0,1,0});
        testDecompress(new byte[]{0,1,2,3,2,3,4,3,5,4,1,2,3});
        testDecompress(new byte[]{0,1,2,3,4,5,6,7,8,9});
        testDecompress(randomData(100));
    }
}
