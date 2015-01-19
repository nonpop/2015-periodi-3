package huffman;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

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

    private static ArrayList<Integer> helloData() {
        return new ArrayList<>(Arrays.asList(72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100));
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

    private static byte[] asByteArray(ArrayList<Integer> data) {
        byte[] result = new byte[data.size()];
        for (int i = 0; i < data.size(); ++i) {
            result[i] = (byte)(data.get(i) & 0xff);
        }
        return result;
    }
    
    private static InputStream helloStream() {
        return new ByteArrayInputStream(asByteArray(helloData()));
    }

    @Test
    public void testCalculateFrequencies() {
        ArrayList<Integer> data = new ArrayList<>();
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

    @Test
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

    @Test
    public void testFindCode() {
        int[] freqs = new int[256];
        HuffmanTree tree = Huffman.buildTree(freqs);
        checkCodes(freqs, tree);

        freqs = helloFreqs();
        tree = Huffman.buildTree(freqs);
        checkCodes(freqs, tree);
    }

    @Test
    public void testCompressAndDecompress() {
        ArrayList<Integer> data = new ArrayList<>();
        int[] freqs = new int[256];
        assertEquals(data, Huffman.decompress(Huffman.compress(data, freqs), freqs));

        data = new ArrayList<>(Arrays.asList(1));
        freqs = new int[256];
        freqs[1] = 1;
        assertEquals(data, Huffman.decompress(Huffman.compress(data, freqs), freqs));

        data = new ArrayList<>(Arrays.asList(1, 1));
        freqs = new int[256];
        freqs[1] = 2;
        assertEquals(data, Huffman.decompress(Huffman.compress(data, freqs), freqs));

        data = helloData();
        freqs = helloFreqs();
        assertEquals(data, Huffman.decompress(Huffman.compress(data, freqs), freqs));
    }

    @Test
    public void testCompressAndDecompressStream() throws Exception {
        InputStream ins = helloStream();
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        Huffman.compressStream(ins, outs);
        ins = new ByteArrayInputStream(outs.toByteArray());
        outs = new ByteArrayOutputStream();
        Huffman.decompressStream(ins, outs);
        byte[] data = asByteArray(helloData());
        assertArrayEquals(data, outs.toByteArray());
    }
}
