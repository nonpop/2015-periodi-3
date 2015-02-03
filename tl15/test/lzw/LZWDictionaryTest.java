package lzw;

import org.junit.Test;
import static org.junit.Assert.*;
import utils.List;

public class LZWDictionaryTest {
    @Test
    public void test() {
        LZWDictionary dict = new LZWDictionary(12);
        List<Integer> string = new List<>();
        string.add(0);
        for (int i = 0; i < 4096 - 256; ++i) {
            string.add(i % 256);
            assertFalse(dict.isFull());
            dict.addString(string);
        }
        assertTrue(dict.isFull());
        assertEquals(4095, dict.getCode(string));
        string.clear();
        string.add(0);
        assertEquals(0, dict.getCode(string));
        string.add(0);
        assertEquals(256, dict.getCode(string));

        dict.reset();
        assertFalse(dict.isFull());
        string.clear();
        string.add(0);
        assertEquals(0, dict.getCode(string));
        string.add(0);
        assertEquals(-1, dict.getCode(string));
        dict.addString(string);
        assertEquals(256, dict.getCode(string));
    }
}
