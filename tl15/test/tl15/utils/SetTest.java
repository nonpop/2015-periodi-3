package tl15.utils;

import tl15.utils.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class SetTest {
    @Test
    public void test() {
        Set s = new Set(3);
        s.put(3);
        s.put(2);
        s.put(3);
        s.put(-10);
        assertTrue(s.contains(2));
        assertTrue(s.contains(3));
        assertTrue(s.contains(-10));
        s.clear();
        assertFalse(s.contains(2));
        assertFalse(s.contains(3));
        assertFalse(s.contains(-10));
    }
}
