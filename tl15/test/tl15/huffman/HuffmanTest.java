package tl15.huffman;

import tl15.huffman.HuffmanTreeNode;
import tl15.huffman.Huffman;
import tl15.huffman.HuffmanTree;
import tl15.utils.BitInputStream;
import tl15.utils.BitOutputStream;
import tl15.utils.List;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import static org.junit.Assert.*;
import org.junit.Test;
import static tl15.utils.DataSources.randomData;
import static tl15.utils.DataSources.weighedExponentialRandomData;
import static tl15.utils.DataSources.weighedLinearRandomData;

public class HuffmanTest {
    private static final int bigSize = 10000;
    private static final byte[] helloData = new byte[]{72, 101, 108, 108, 111, 44, 32, 87, 111, 114, 108, 100};

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

//    private static byte[] asByteArray(List<Integer> data) {
//        byte[] result = new byte[data.size()];
//        for (int i = 0; i < data.size(); ++i) {
//            result[i] = (byte)(data.get(i) & 0xff);
//        }
//        return result;
//    }
    
    @Test
    public void testCalculateFrequencies() throws IOException {
        byte[] data = new byte[]{};
        int[] expResult = new int[256];
        int[] result = Huffman.calculateFrequencies(new ByteArrayInputStream(data));
        assertArrayEquals(expResult, result);

        data = helloData;
        expResult = helloFreqs();
        result = Huffman.calculateFrequencies(new ByteArrayInputStream(data));
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

    private static void checkCode(List<Boolean> code, HuffmanTreeNode root, int expected) {
        assertNotNull(root);
        if (root.left == null) {
            assertEquals(0, code.size());
            assertEquals(expected, root.data);
            return;
        }

        boolean nextBit = code.get(0);
        code.removeFirst();
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

    private void testCompressDecompress(byte[] data, int[] freqs) throws IOException {
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        BitOutputStream bouts = new BitOutputStream(outs);
        Huffman.compress(new ByteArrayInputStream(data), freqs, bouts);
        bouts.flush();
        ByteArrayInputStream ins = new ByteArrayInputStream(outs.toByteArray());
        outs = new ByteArrayOutputStream();
        Huffman.decompress(new BitInputStream(ins), freqs, outs);
        assertArrayEquals(data, outs.toByteArray());
    }
    @Test
    public void testCompressAndDecompress() throws IOException {
        int[] freqs = new int[256];
        testCompressDecompress(new byte[0], freqs);
        freqs[1] = 1;
        testCompressDecompress(new byte[]{1}, freqs);
        freqs[1] = 2;
        testCompressDecompress(new byte[]{1, 1}, freqs);
        testCompressDecompress(helloData, helloFreqs());

        System.out.print("Random: ");
        byte[] data = randomData(bigSize, false);
        freqs = Huffman.calculateFrequencies(new ByteArrayInputStream(data));
        testCompressDecompress(data, freqs);

        System.out.print("Random weighed linear: ");
        data = weighedLinearRandomData(bigSize);
        freqs = Huffman.calculateFrequencies(new ByteArrayInputStream(data));
        testCompressDecompress(data, freqs);

        System.out.print("Random weighed exponential: ");
        data = weighedExponentialRandomData(bigSize);
        freqs = Huffman.calculateFrequencies(new ByteArrayInputStream(data));
        testCompressDecompress(data, freqs);
    }

    @Test
    public void testCompressAndDecompressFile() throws Exception {
        InputStream ins = new ByteArrayInputStream(helloData);
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        Huffman.compressFile(ins, outs);
        ins = new ByteArrayInputStream(outs.toByteArray());
        outs = new ByteArrayOutputStream();
        Huffman.decompressFile(ins, outs);
        assertArrayEquals(helloData, outs.toByteArray());
    }
}
