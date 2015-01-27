package lzw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class LZWTest {
    @Test
    public void testCompress() {
        ArrayList<Integer> data = new ArrayList<>(Arrays.asList(0,1,0,1,0,1,0));
        ArrayList<Integer> expResult = new ArrayList<>(Arrays.asList(0,1,256,258));
        ArrayList<Integer> result = LZW.compress(data);
        assertEquals(expResult, result);

        data = new ArrayList<>();
        expResult = new ArrayList<>();
        result = LZW.compress(data);
        assertEquals(expResult, result);

        data = new ArrayList<>(1);
        expResult = new ArrayList<>(1);
        result = LZW.compress(data);
        assertEquals(expResult, result);

        data = new ArrayList<>(Arrays.asList(1,1));
        expResult = new ArrayList<>(Arrays.asList(1,1));
        result = LZW.compress(data);
        assertEquals(expResult, result);

        data = new ArrayList<>(Arrays.asList(1,1,1));
        expResult = new ArrayList<>(Arrays.asList(1,256));
        result = LZW.compress(data);
        assertEquals(expResult, result);

        data = new ArrayList<>(Arrays.asList(1,1,1,1,1,1,1,1,1,1));
        expResult = new ArrayList<>(Arrays.asList(1,256,257,258));
        result = LZW.compress(data);
        assertEquals(expResult, result);

        data = new ArrayList<>(Arrays.asList(0,1,2,3,2,3,4,3,5,4,1,2,3));
        expResult = new ArrayList<>(Arrays.asList(0,1,2,3,258,4,3,5,4,257,3));
        result = LZW.compress(data);
        assertEquals(expResult, result);

        data = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9));
        expResult = data;
        result = LZW.compress(data);
        assertEquals(expResult, result);
    }

    @Test
    public void testDecompress() {
        ArrayList<Integer> data = new ArrayList<>();
        assertEquals(data, LZW.decompress(LZW.compress(data)));

        data = new ArrayList<>(1);
        assertEquals(data, LZW.decompress(LZW.compress(data)));

        data = new ArrayList<>(Arrays.asList(1,1));
        assertEquals(data, LZW.decompress(LZW.compress(data)));

        data = new ArrayList<>(Arrays.asList(1,2,1,1,2));
        assertEquals(data, LZW.decompress(LZW.compress(data)));

        data = new ArrayList<>(Arrays.asList(1,1,1));
        assertEquals(data, LZW.decompress(LZW.compress(data)));

        data = new ArrayList<>(Arrays.asList(1,1,1,1,1,1,1,1,1,1));
        assertEquals(data, LZW.decompress(LZW.compress(data)));

        data = new ArrayList<>(Arrays.asList(0,1,0,1,0,1,0));
        assertEquals(data, LZW.decompress(LZW.compress(data)));

        data = new ArrayList<>(Arrays.asList(0,1,2,3,2,3,4,3,5,4,1,2,3));
        assertEquals(data, LZW.decompress(LZW.compress(data)));

        data = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9));
        assertEquals(data, LZW.decompress(LZW.compress(data)));

        Random r = new Random(42);
        data.clear();
        for (int i = 0; i < 100; ++i) {
            data.add(r.nextInt(256));
        }
        assertEquals(data, LZW.decompress(LZW.compress(data)));

        data.clear();
        for (int i = 0; i < 1000; ++i) {
            data.add(r.nextInt(256));
        }
        assertEquals(data, LZW.decompress(LZW.compress(data)));
    }
}
