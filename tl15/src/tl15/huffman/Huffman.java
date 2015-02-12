package tl15.huffman;

import tl15.utils.BitInputStream;
import tl15.utils.BitOutputStream;
import tl15.utils.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Methods implementing (de)compression by Huffman coding.
 */
public class Huffman {
    /**
     * Calculate the frequencies of characters in the input.
     * @param ins The input stream. All remaining data is consumed.
     * @return An array where the element with index <code>i</code> is the 
     *         frequency of the character <code>i</code> in the data.
     * @throws java.io.IOException
     */
    private static int[] calculateFrequencies(InputStream ins) throws IOException {
        int[] freqs = new int[256];
        int b;
        while ((b = ins.read()) != -1) {
            ++freqs[b];
        }
        return freqs;
    }

    /**
     * Build a Huffman tree from the given frequencies.
     * @param freqs The frequencies. Must be a size 256 array of (non-negative) integers.
     * @return A corresponding Huffman tree.
     */
    private static HuffmanTree buildTree(int[] freqs) {
        HuffmanTreeNode[] leaves = new HuffmanTreeNode[256];
        HuffmanHeap q = new HuffmanHeap(256);

        // First create the leaf nodes from the given frequencies.
        for (int b = 0; b < 256; ++b) {
            if (freqs[b] > 0) {
                HuffmanTreeNode t = new HuffmanTreeNode(freqs[b], null, null, b);
                leaves[b] = t;
                q.push(t);
            }
        }

        // Then start pushing the nodes with least frequencies away from the (imaginary) root.
        while (q.size() >= 2) {
            HuffmanTreeNode a = q.pop();
            HuffmanTreeNode b = q.pop();
            HuffmanTreeNode t = new HuffmanTreeNode(a.sum + b.sum, a, b, (byte)0);
            a.parent = t;
            b.parent = t;
            q.push(t);
        } 

        HuffmanTreeNode root = (q.size() > 0)? q.pop() : null;
        return new HuffmanTree(root, leaves);
    }

    /**
     * Find the Huffman code of a given input character.
     * @param node A leaf node representing the character.
     * @return The code for the character.
     */
    private static List<Boolean> findCode(HuffmanTreeNode node) {
        List<Boolean> res = new List<>(8, false);
        while (node.parent != null) {
            if (node == node.parent.right) {
                res.add(true);
            } else {
                res.add(false);
            }
            node = node.parent;
        }
        res.reverse();
        return res;
    }

    /**
     * Compress a list of bytes.
     * 
     * @param ins The input data. All remaining data is consumed.
     * @param freqs The character frequencies for <code>data</code>.
     * @param outs The bit stream to write the compressed data to.
     * @throws java.io.IOException
     */
    public static void compress(InputStream ins, int[] freqs, BitOutputStream outs) throws IOException {
        HuffmanTree tree = buildTree(freqs);
        int inBytes = 0;
        int b;
        while ((b = ins.read()) != -1) {
            outs.writeBits(findCode(tree.leaves[b]));
            ++inBytes;
        }

        System.out.println("Compressed/original (no headers) = " + 100 * (outs.getBitCount() / 8.0) / inBytes + "%");
    }

    /**
     * Decompress a list of bits.
     * 
     * @param bits The input bits. Must be something produced by compress().
     * @param freqs The frequencies of the bytes in the original input.
     * @param outs The stream to append the decompressed data to.
     * @throws java.io.IOException
     */

    public static void decompress(BitInputStream bits, int[] freqs, OutputStream outs) throws IOException {
        HuffmanTree tree = buildTree(freqs);
        int inputChars = 0;
        int chr = 0, count = 0;
        for (int i = 0; i < freqs.length; ++i) {
            if (freqs[i] > 0) {
                chr = i;
                count = freqs[i];
                ++inputChars;
                if (inputChars >= 2) {
                    break;
                }
            }
        }
        if (inputChars <= 1) {
            // All characters in the original input were the same.
            if (inputChars == 1) {
                for (int i = 0; i < count; ++i) {
                    outs.write(chr);
                }
            }
            return;
        }
        
        // The original input had at least two different characters so now all
        // codes have a length of at least one bit.
        int bytesToWrite = sizeFromFreqs(freqs);
        HuffmanTreeNode node = tree.root;
        while (bytesToWrite > 0) {
            int bit = bits.readBits(1);
//            if (bit == null) {
//                break;
//            }
            if (bit == 0) {
                node = node.left;
            } else {
                node = node.right;
            }
            if (node.left == null) {
                outs.write(node.data);
                --bytesToWrite;
                node = tree.root;
            }
        }
    }

    /** Just some random bytes to identify a compressed file. */
    private static final int headerMagik = ('T' << 24) | ('L' << 16) | (1 << 8) | 5;
    
    /**
     * Write header information to a stream. The header consists of:
     *  - the four headerMagik bytes
     *  - a byte 'n' telling how many entries the following table has
     *  - a table of 'n' pairs (b,i), where b is a byte and i 4-byte integer 
     *    expressing the frequency of byte b
     * @param outs The stream.
     * @param freqs The frequencies.
     * @throws IOException 
     */
    private static void writeHeader(BitOutputStream outs, int[] freqs) throws IOException {
        outs.writeBits(32, headerMagik);
        for (int freq : freqs) {
            outs.writeBits(32, freq);
        }
    }

    /**
     * Read header information from a stream.
     * @see #writeHeader(OutputStream, int[], int)
     * @param ins The stream.
     * @return The frequencies.
     * @throws IOException 
     */
    private static int[] readHeader(BitInputStream ins) throws IOException {
        if (ins.readBits(32) != headerMagik) {
            throw new IllegalArgumentException("Bad file.");
        }
        int[] freqs = new int[256];
        for (int i = 0; i < 256; ++i) {
            freqs[i] = ins.readBits(32);
        }
        return freqs;
    }

    /**
     * Calculate the number of characters from the frequency table.
     * @param freqs The frequency table.
     * @return The sum of all frequencies.
     */
    private static int sizeFromFreqs(int[] freqs) {
        int size = 0;
        for (int f : freqs) {
            size += f;
        }
        return size;
    }

    /**
     * Compress a file into another file.
     * @param ins Input stream. Must be resettable.
     * @param outs Output stream. Will contain a header.
     * @throws IOException 
     */
    public static void compressFile(InputStream ins, OutputStream outs) throws IOException {
        if (!ins.markSupported()) {
            throw new IllegalArgumentException("input stream must support mark");
        }
        ins.mark(Integer.MAX_VALUE);
        int[] freqs = calculateFrequencies(ins);
        ins.reset();

        // the compressed data is written to compressedBytes through compressedBits
        ByteArrayOutputStream compressedBytes = new ByteArrayOutputStream();
        BitOutputStream compressedBits = new BitOutputStream(compressedBytes);
        compress(ins, freqs, compressedBits);
        compressedBits.flush();

        BitOutputStream bouts = new BitOutputStream(outs);
        writeHeader(bouts, freqs);
        bouts.write(compressedBytes.toByteArray());
        bouts.flush();

        System.out.println("Compressed/original = " + 100 * (bouts.getBitCount() / 8.0) / sizeFromFreqs(freqs) + " %");
    }

    /**
     * Decompress a file into another file.
     * @param ins Input stream. Must contain a header.
     * @param outs Output stream.
     * @throws IOException 
     */
    public static void decompressFile(InputStream ins, OutputStream outs) throws IOException {
        BitInputStream compressed = new BitInputStream(ins);
        int freqs[] = readHeader(compressed);
        decompress(compressed, freqs, outs);
    }
}
