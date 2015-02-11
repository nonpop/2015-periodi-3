package tl15.huffman;

import tl15.huffman.HuffmanHeap;
import tl15.huffman.HuffmanTreeNode;
import org.junit.Test;
import static org.junit.Assert.*;

public class HuffmanHeapTest {
    @Test
    public void test() {
        HuffmanHeap q = new HuffmanHeap(10);
        assertEquals(0, q.size());
        HuffmanTreeNode t1 = new HuffmanTreeNode(1, null, null, 0);
        q.push(t1);
        assertEquals(1, q.size());
        assertEquals(t1, q.peek());
        assertEquals(t1, q.pop());
        assertEquals(0, q.size());

        HuffmanTreeNode t2 = new HuffmanTreeNode(2, null, null, 0);
        q.push(t2);
        q.push(t1);
        assertEquals(2, q.size());
        assertEquals(t1, q.peek());
        assertEquals(t1, q.pop());
        assertEquals(t2, q.pop());
        assertEquals(0, q.size());

        for (int i = 0; i < 10; ++i) {
            HuffmanTreeNode t = new HuffmanTreeNode(i*3 % 10, null, null, 0);
            q.push(t);
        }
        assertEquals(10, q.size());
        for (int i = 0; i < 10; ++i) {
            assertEquals(i, q.pop().sum);
        }
    }
}
