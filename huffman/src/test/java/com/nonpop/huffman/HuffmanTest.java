package com.nonpop.huffman;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class HuffmanTest {
    
    public HuffmanTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of buildTree method, of class Huffman.
     */
    @org.junit.Test
    public void testBuildTree() {
        System.out.println("buildTree");
        int[] freqs = null;
        Pair expResult = null;
        Pair result = Huffman.buildTree(freqs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findCode method, of class Huffman.
     */
    @org.junit.Test
    public void testFindCode() {
        System.out.println("findCode");
        HuffmanTree leaf = null;
        Pair<Integer, Integer> expResult = null;
        Pair<Integer, Integer> result = Huffman.findCode(leaf);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compress method, of class Huffman.
     */
    @org.junit.Test
    public void testCompress() {
        System.out.println("compress");
        int[] data = null;
        int[] freqs = null;
        BitOutput expResult = null;
        BitOutput result = Huffman.compress(data, freqs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of decompress method, of class Huffman.
     */
    @org.junit.Test
    public void testDecompress() {
        System.out.println("decompress");
        BitOutput bits = null;
        int[] freqs = null;
        List<Integer> expResult = null;
        List<Integer> result = Huffman.decompress(bits, freqs);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toIntArray method, of class Huffman.
     */
    @org.junit.Test
    public void testToIntArray() {
        System.out.println("toIntArray");
        String s = "";
        int[] expResult = null;
        int[] result = Huffman.toIntArray(s);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class Huffman.
     */
    @org.junit.Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        Huffman.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
