package com.nonpop.huffman;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

    private static int[] helloData() {
        return new int[]{72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100};
    }

    private static int[] helloFreqs() {
        int[] f = new int[256];
        f[72] = 1;
        f[101] = 1;
        f[108] = 3;
        f[111] = 2;
        f[44] = 1;
        f[32] = 1;
        f[87] = 1;
        f[114] = 1;
        f[100] = 1;
        return f;
    }

    @org.junit.Test
    public void testCalculateFrequencies() {
        int[] data = new int[0];
        int[] expResult = new int[256];
        int[] result = Huffman.calculateFrequencies(data);
        assertArrayEquals(expResult, result);

        data = helloData();
        expResult = helloFreqs();
        result = Huffman.calculateFrequencies(data);
        assertArrayEquals(expResult, result);
    }

    private static void checkHuffmanNode(HuffmanTreeNode node, int[] freqs) {
        if (node == null) {
            return;
        }

        assertTrue(node.sum > 0);

        if (node.left == null && node.right == null) {
            assertEquals(freqs[node.data], node.sum);
            return;
        }

        assertNotNull(node.left);
        assertNotNull(node.right);

        assertEquals(node.left.sum + node.right.sum, node.sum);

        checkHuffmanNode(node.left, freqs);
        checkHuffmanNode(node.right, freqs);
    }

    private static void checkHuffmanParents(HuffmanTreeNode node, int sum, HuffmanTreeNode root) {
        assertTrue(node.sum > 0);
        if (node.parent == null) {
            assertSame(root, node);
            assertEquals(sum, node.sum);
            return;
        }

        checkHuffmanParents(node.parent, sum, root);
    }

    private static void checkHuffmanTree(HuffmanTree tree, int[] freqs) {
        checkHuffmanNode(tree.root, freqs);

        int sum = 0;
        for (int f : freqs) {
            sum += f;
        }

        for (HuffmanTreeNode leaf : tree.leaves) {
            if (leaf == null) {
                continue;
            }
            assertEquals(freqs[leaf.data], leaf.sum);
            checkHuffmanParents(leaf, sum, tree.root);
        }
    }

    @org.junit.Test
    public void testBuildTree() {
        int[] freqs = new int[256];
        checkHuffmanTree(Huffman.buildTree(freqs), freqs);

        freqs = helloFreqs();
        checkHuffmanTree(Huffman.buildTree(freqs), freqs);
    }

    private static void checkCode(ArrayList<Boolean> code, HuffmanTreeNode root, int expected) {
        assertNotNull(root);
        if (root.left == null) {
            assertEquals(0, code.size());
            assertEquals(expected, root.data);
            return;
        }

        boolean nextBit = code.get(0);
        code.remove(0);
        if (nextBit) {
            checkCode(code, root.right, expected);
        } else {
            checkCode(code, root.left, expected);
        }
    }

    private static void checkCodes(int[] freqs, HuffmanTree tree) {
        for (int i = 0; i < 256; ++i) {
            if (freqs[i] > 0) {
                checkCode(Huffman.findCode(tree.leaves[i]), tree.root, i);
            }
        }
    }
    
    @org.junit.Test
    public void testFindCode() {
        int[] freqs = new int[256];
        HuffmanTree tree = Huffman.buildTree(freqs);
        checkCodes(freqs, tree);

        freqs = helloFreqs();
        tree = Huffman.buildTree(freqs);
        checkCodes(freqs, tree);
    }

    // really no better way?
    private static List<Integer> intArrayToList(int[] a) {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i : a) {
            res.add(i);
        }
        return res;
    }
    
    @org.junit.Test
    public void testCompressAndDecompress() {
        int[] data = new int[0];
        int[] freqs = new int[256];
        assertEquals(intArrayToList(data), Huffman.decompress(Huffman.compress(data, freqs), freqs));

        data = new int[]{1};
        freqs = new int[256];
        freqs[1] = 1;
        assertEquals(intArrayToList(data), Huffman.decompress(Huffman.compress(data, freqs), freqs));

        data = helloData();
        freqs = helloFreqs();
        assertEquals(intArrayToList(data), Huffman.decompress(Huffman.compress(data, freqs), freqs));
    }
}
