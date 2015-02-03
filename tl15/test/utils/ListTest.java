package utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class ListTest {
    @Test
    public void test() {
        List v = new List(1);
        assertEquals(0, v.size());
        v.add(true);
        assertEquals(1, v.size());
        assertEquals(true, v.get(0));
        v.add(false);
        assertEquals(2, v.size());
        assertEquals(true, v.get(0));
        assertEquals(false, v.get(1));
        v.reverse();
        assertEquals(2, v.size());
        assertEquals(false, v.get(0));
        assertEquals(true, v.get(1));
        v.add(true);
        assertEquals(3, v.size());
        assertEquals(false, v.get(0));
        assertEquals(true, v.get(1));
        assertEquals(true, v.get(2));
        v.reverse();
        assertEquals(3, v.size());
        assertEquals(true, v.get(0));
        assertEquals(true, v.get(1));
        assertEquals(false, v.get(2));
        v.removeFirst();
        assertEquals(2, v.size());
        assertEquals(true, v.get(0));
        assertEquals(false, v.get(1));
        v.removeFirst();
        assertEquals(1, v.size());
        assertEquals(false, v.get(0));
        v.removeFirst();
        assertEquals(0, v.size());
    }
}
