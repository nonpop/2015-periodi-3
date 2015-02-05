package utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class ListTest {
    @Test
    public void test() {
        List v = new List(1, false);
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
        v.add(1);
        v.add(2);
        v.removeLast();
        assertEquals(1, v.size());
        assertEquals(1, v.get(0));
        v.add(1);
        v.add(2);
        v.clear();
        assertEquals(0, v.size());
        v.clear();
        assertEquals(0, v.size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBadEmpty() {
        List v = new List();
        v.get(0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBadSmall() {
        List v = new List();
        v.add(0);
        v.get(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetBadBig() {
        List v = new List();
        v.add(0);
        v.get(1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetBadEmpty() {
        List v = new List();
        v.set(0, 0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetBadSmall() {
        List v = new List();
        v.add(0);
        v.set(-1, 0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSetBadBig() {
        List v = new List();
        v.add(0);
        v.set(1, 0);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveFirstBad() {
        List v = new List();
        v.removeFirst();
    }
    
    @Test(expected = IllegalStateException.class)
    public void testRemoveLastBad() {
        List v = new List();
        v.removeLast();
    }
}
