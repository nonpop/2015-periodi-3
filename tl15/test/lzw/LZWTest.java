package lzw;

import utils.BitInputStream;
import utils.BitOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static utils.DataSources.alternatingData;
import static utils.DataSources.consecutiveData;
import static utils.DataSources.randomData;
import static utils.DataSources.weighedExponentialRandomData;
import static utils.DataSources.weighedLinearRandomData;
import static utils.Math.twoTo;

@RunWith(Parameterized.class)
public class LZWTest {
    private final LZW lzw;
//    private final int bigSize = 10000;
    private final int bigSize = 100000;
    
    @Parameters
    public static Collection<Object[]> parameters() {
       return Arrays.asList(new Object[][]{{9}, {10}, {11}, {12}, {16}, {20}});
//       return Arrays.asList(new Object[][]{{10}});
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
        int curCodeSize = 9;
        for (int i : expected) {
            Integer next = bins.readBits(curCodeSize);
            if (next == twoTo(curCodeSize) - 2) {
                ++curCodeSize;
            } else if (next == twoTo(curCodeSize) - 1) {
                curCodeSize = 9;
            }
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
        lzw.decompress(new BitInputStream(ins), outs);
        assertArrayEquals(data, outs.toByteArray());
    }

    public void testDecompressFile(byte[] data) throws IOException {
        ByteArrayInputStream ins = new ByteArrayInputStream(data);
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        LZW.compressFile(ins, outs, lzw.maxCodeSize);
        ins = new ByteArrayInputStream(outs.toByteArray());
        outs = new ByteArrayOutputStream();
        LZW.decompressFile(ins, outs);
        assertArrayEquals(data, outs.toByteArray());
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
        System.out.println("Random " + bigSize);
        testDecompress(randomData(bigSize, false));
        System.out.println("Random " + bigSize + ", nonnegative");
        testDecompress(randomData(bigSize, true));
        System.out.println("Consecutive " + bigSize);
        testDecompress(consecutiveData(bigSize));
        System.out.println("Alternating " + bigSize);
        testDecompress(alternatingData(bigSize));
        System.out.println("Weighed linear " + bigSize);
        testDecompress(weighedLinearRandomData(bigSize));
        System.out.println("Weighed exponential " + bigSize);
        testDecompress(weighedExponentialRandomData(bigSize));
    }

    @Test
    public void testDecompressFile() throws IOException {
        testDecompressFile(randomData(bigSize, false));
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

    @Test
    public void weirdBug3() throws UnsupportedEncodingException, IOException {
        FileInputStream ins = new FileInputStream("testdata/pg48138.txt");
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        int b;
        while ((b = ins.read()) != -1) {
            outs.write(b);
        }
        ins.close();
        testDecompressFile(outs.toByteArray());
    }
}
