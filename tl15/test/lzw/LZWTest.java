package lzw;

import bitstream.BitInputStream;
import bitstream.BitOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LZWTest {
    private final LZW lzw;
    
    @Parameters
    public static Collection<Object[]> parameters() {
       return Arrays.asList(new Object[][]{{9}, {10}, {11}, {12}, {16}, {31}});
       //return Arrays.asList(new Object[][]{{9}});
    }
    
    public LZWTest(int codeSize) {
        this.lzw = new LZW(codeSize);
    }

    private void testCompress(int[] expected, byte[] data) throws IOException {
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        BitOutputStream bouts = new BitOutputStream(outs);
        lzw.compress(new ByteArrayInputStream(data), bouts);
        bouts.flush();
        BitInputStream bins = new BitInputStream(new ByteArrayInputStream(outs.toByteArray()));
        for (int i : expected) {
            Integer next = bins.readBits(lzw.codeSize);
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
        testCompress(new int[]{1,0,256,258,257,260,258}, new byte[]{1,0,1,0,1,0,1,0,1,0,1,0,1,0,1});
    }


    public void testDecompress(byte[] data) throws IOException {
        InputStream ins = new ByteArrayInputStream(data);
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        BitOutputStream bouts = new BitOutputStream(outs);
        lzw.compress(ins, bouts);
        bouts.flush();
        ins = new ByteArrayInputStream(outs.toByteArray());
        outs = new ByteArrayOutputStream();
        bouts = new BitOutputStream(outs);
        lzw.decompress(new BitInputStream(ins), bouts);
        bouts.flush();
        assertArrayEquals(data, outs.toByteArray());
    }

    public void testDecompressFile(byte[] data) throws IOException {
        ByteArrayInputStream ins = new ByteArrayInputStream(data);
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        LZW.compressFile(ins, outs, lzw.codeSize);
        ins = new ByteArrayInputStream(outs.toByteArray());
        outs = new ByteArrayOutputStream();
        LZW.decompressFile(ins, outs);
        assertArrayEquals(data, outs.toByteArray());
    }
    
    private byte[] randomData(int size, boolean nonnegative) {
        byte[] res = new byte[size];
        Random r = new Random(42);
        for (int i = 0; i < size; ++i) {
            if (nonnegative) {
                res[i] = (byte)(r.nextInt(128));
            } else {
                res[i] = (byte)(r.nextInt(256));
            }
        }
        return res;
    }

    private byte[] alternatingData(int size) {
        byte[] res = new byte[size];
        for (int i = 0; i < size; i += 2) {
            res[i] = 1;
        }
        return res;
    }

    private byte[] consecutiveData(int size) {
        byte[] res = new byte[size];
        for (int i = 0; i < size; ++i) {
            res[i] = (byte)(i & 0xff);
        }
        return res;
    }
    
    @Test
    public void testDecompress() throws IOException {
        testDecompress(new byte[]{});
        testDecompress(new byte[]{1});
        testDecompress(new byte[]{1,1});
        testDecompress(new byte[]{1,1,1});
        testDecompress(new byte[]{1,1,1,1,1});      // 5 is the magic bug number
        testDecompress(new byte[]{1,1,1,1,1,1,1,1,1,1});
        testDecompress(new byte[]{1,2,1,1,2});
        testDecompress(new byte[]{0,1,0,1,0,1,0});
        testDecompress(new byte[]{0,1,2,3,2,3,4,3,5,4,1,2,3});
        testDecompress(new byte[]{0,1,2,3,4,5,6,7,8,9});
        testDecompress(randomData(100000, false));
        testDecompress(randomData(100000, true));
        testDecompress(consecutiveData(100000));
        testDecompress(alternatingData(100000));
    }

    @Test
    public void testDecompressFile() throws IOException {
        if (lzw.codeSize <= 16) {
            testDecompressFile(randomData(2000000, false));
        }
    }

    @Test
    public void weirdBug() throws UnsupportedEncodingException, IOException {
        String data = "#LyX 2.1 created this file. For more info see http://www.lyx.org/\n" +
                        "\\lyxformat 474\n" +
                        "\\begin_document\n" +
                        "\\begin_header\n" +
                        "\\textclass scrbook\n" +
                        "\\begin_preamble\n" +
                        "% For XeTeX %%%%%%%%%%%\n" +
                        "%\\usepackage{fontspec}\n" +
                        "%\\usepackage{xunicode}\n" +
                        "%\\usepackage{xltxtra}\n" +
                        "%%%%%%%%%%%%%%%%%%";
        byte[] bytes = data.getBytes("UTF-8");
        testDecompress(bytes);
    }

    @Test
    public void weirdBug2() throws UnsupportedEncodingException, IOException {
        String data = "%%%%%";
        byte[] bytes = data.getBytes("UTF-8");
        testDecompress(bytes);
    }
}
