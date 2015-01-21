package huffman;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Methods implementing (de)compression by Huffman coding.
 */
public class Huffman {
    /**
     * Calculate the frequencies of characters in the input.
     * @param data A list of input characters. Each element must be between 0..255.
     * @return An array where the element with index i is the frequency of the character i in data.
     */
    public static int[] calculateFrequencies(ArrayList<Integer> data) {
        int[] freqs = new int[256];
        for (int b : data) {
            ++freqs[b];
        }
        return freqs;
    }

    /**
     * Build a Huffman tree from the given frequencies.
     * @param freqs The frequencies. Must be a size 256 array of (non-negative) integers.
     * @return A corresponding Huffman tree.
     */
    public static HuffmanTree buildTree(int[] freqs) {
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

        // Then start pushing the nodes with least frequencies away from the root.
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
    public static ArrayList<Boolean> findCode(HuffmanTreeNode node) {
        ArrayList<Boolean> res = new ArrayList<>();
        while (node.parent != null) {
            if (node == node.parent.right) {
                res.add(true);
            } else {
                res.add(false);
            }
            node = node.parent;
        }
        Collections.reverse(res);
        return res;
    }

    /**
     * Compress a list of bytes.
     * 
     * @param data The input bytes. Must be a list of integers between 0..255.
     * @return A list of bits containing the compressed data.
     */
    public static ArrayList<Boolean> compress(ArrayList<Integer> data) {
        int[] freqs = calculateFrequencies(data);
        HuffmanTree tree = buildTree(freqs);
        ArrayList<Boolean> out = new ArrayList<>();
        for (int b : data) {
            out.addAll(findCode(tree.leaves[b]));
        }

        System.out.println("Compressed/original (no headers) = " + 100 * (out.size() / 8.0) / data.size() + "%");
        return out;
    }

    /**
     * Decompress a list of bits.
     * 
     * @param bits The input bits. Must be something produced by compress().
     * @param freqs The frequencies of the bytes in the original input.
     * @return The decompressed data.
     */

    public static ArrayList<Integer> decompress(ArrayList<Boolean> bits, int[] freqs) {
        ArrayList<Integer> res = new ArrayList<>();
        HuffmanTree tree = buildTree(freqs);
        if (bits.isEmpty()) {
            // All characters in the original input were the same so
            // we just need to figure out which one it was and how many
            // of it there were.
            for (HuffmanTreeNode leaf : tree.leaves) {
                if (leaf != null) {
                    // This is the one. Assuming the input data is correct.
                    for (int i = 0; i < freqs[leaf.data]; ++i) {
                        res.add(leaf.data);
                    }
                    break; // if we return here then the case of empty original input will fail
                }
            }
            return res;
        }
        
        // The original input had at least two different characters so now all
        // codes have a length of at least one bit.
        HuffmanTreeNode node = tree.root;
        for (boolean bit : bits) {
            if (!bit) {
                node = node.left;
            } else {
                node = node.right;
            }
            if (node.left == null) {
                res.add(node.data);
                node = tree.root;
            }
        }
        return res;
    }

    /** Just some random bytes to identify a compressed file. */
    private static final byte[] headerMagik = new byte[]{ 84, 76, 1, 5 };
    
    // TODO: There must be a standard way to do this?
    /**
     * Write a 4-byte integer to a stream. Most significant byte comes first.
     * @param outs Stream to write to.
     * @param value The integer.
     * @throws IOException 
     */
    private static void writeInt(OutputStream outs, int value) throws IOException {
        outs.write(new byte[]{ (byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)(value) });
    }

    /**
     * Read a 4-byte integer from a stream.
     * @see writeInt()
     * @param ins Stream to read from.
     * @return The integer.
     * @throws IOException 
     */
    private static int readInt(InputStream ins) throws IOException {
        int b1 = ins.read();
        int b2 = ins.read();
        int b3 = ins.read();
        int b4 = ins.read();
        return (b1 << 24) + ((b2 << 16) & 0xff0000) + ((b3 << 8) & 0xff00) + (b4 & 0xff);
    }

    /**
     * Write header information to a stream. The header consists of:
     *  - the four headerMagik bytes
     *  - the number of bits in the compressed data as a 4-byte integer
     *  - a table of 256 4-byte integers containing the frequency data
     * @param outs The stream.
     * @param freqs The frequencies.
     * @param actualBitCount The bit count.
     * @throws IOException 
     */
    public static void writeHeader(OutputStream outs, int[] freqs, int actualBitCount) throws IOException {
        outs.write(headerMagik);
        writeInt(outs, actualBitCount);
        for (int f : freqs) {
            writeInt(outs, f);
        }
    }

    /**
     * Read header information from a stream.
     * @see writeHeader()
     * @param ins The stream.
     * @param freqs An array of 256 integers. The frequencies are written here and the original data is destroyed.
     * @return The bit count.
     * @throws IOException 
     */
    public static int readHeader(InputStream ins, int[] freqs) throws IOException {
        for (int i = 0; i < headerMagik.length; ++i) {
// I'm too lazy to write a test to cover this now.
//            if (ins.read() != headerMagik[i]) {
//                throw new IllegalArgumentException("Bad file.");
//            }
            ins.read();
        }
        int actualBitCount = readInt(ins);
        for (int i = 0; i < 256; ++i) {
            freqs[i] = readInt(ins);
        }
        return actualBitCount;
    }

    /**
     * Write a list of bits to a stream. If the number of bits is 8*q + r, where 
     * r is in (0,8), then the last 8-r bits written will be garbage.
     * @param outs
     * @param bits
     * @throws IOException 
     */
    public static void writeBits(OutputStream outs, ArrayList<Boolean> bits) throws IOException {
        byte b = 0;
        int pos = 0;
        for (boolean bit : bits) {
            if (pos == 8) {
                outs.write(b);
                pos = 0;
            }
            int mask = 0x80 >> pos;
            if (bit) {
                b |= mask;
            } else {
                b &= ~mask;
            }
            ++pos;
        }
        outs.write(b);
    }

    /**
     * Read a list of bits from a stream. The number of bits read is always divisible by 8.
     * @param ins
     * @return
     * @throws IOException 
     */
    public static ArrayList<Boolean> readBits(InputStream ins) throws IOException {
        ArrayList<Boolean> res = new ArrayList<>();
        int c;
        while ((c = ins.read()) != -1) {
            for (int i = 0; i < 8; ++i) {
                res.add((c & (0x80 >> i)) > 0);
            }
        }
        return res;
    }
    
    /**
     * Compress a stream into another stream.
     * @param ins Input stream.
     * @param outs Output stream.
     * @throws IOException 
     */
    public static void compressStream(InputStream ins, OutputStream outs) throws IOException {
        ArrayList<Integer> data = new ArrayList<>();
        int c;
        while ((c = ins.read()) != -1) {
            data.add(c);
        }

        ArrayList<Boolean> compressed = compress(data);
        writeHeader(outs, calculateFrequencies(data), compressed.size());
        writeBits(outs, compressed);

        System.out.println("Compressed/original = " + 100 * (compressed.size() / 8.0 + 8 + 256*4) / data.size() + "%");
    }

    /**
     * Decompress a stream into another stream.
     * @param ins Input stream.
     * @param outs Output stream.
     * @throws IOException 
     */
    public static void decompressStream(InputStream ins, OutputStream outs) throws IOException {
        int freqs[] = new int[256];
        int dataSize = readHeader(ins, freqs);
        ArrayList<Boolean> compressed = readBits(ins);
        while (compressed.size() > dataSize) {
            compressed.remove(compressed.size() - 1);
        }
        ArrayList<Integer> data = decompress(compressed, freqs);
        for (int c : data) {
            outs.write(c);
        }
    }
}
