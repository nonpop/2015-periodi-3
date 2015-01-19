package huffman;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Huffman {
    // data should be an array of unsigned bytes
    public static int[] calculateFrequencies(ArrayList<Integer> data) {
        int[] freqs = new int[256];
        for (int b : data) {
            ++freqs[b];
        }
        return freqs;
    }

    public static HuffmanTree buildTree(int[] freqs) {
        HuffmanTreeNode[] leaves = new HuffmanTreeNode[256];
        PriorityQueue<HuffmanTreeNode> q = new PriorityQueue<>();
        for (int b = 0; b < 256; ++b) {
            if (freqs[b] > 0) {
                HuffmanTreeNode t = new HuffmanTreeNode(freqs[b], null, null, b);
                leaves[b] = t;
                q.add(t);
            }
        }
        while (q.size() >= 2) {
            HuffmanTreeNode a = q.poll();
            HuffmanTreeNode b = q.poll();
            HuffmanTreeNode t = new HuffmanTreeNode(a.sum + b.sum, a, b, (byte)0);
            a.parent = t;
            b.parent = t;
            q.add(t);
        } 
        HuffmanTreeNode root = (q.size() > 0)? q.poll() : null;
        return new HuffmanTree(root, leaves);
    }

    public static ArrayList<Boolean> findCode(HuffmanTreeNode node) {
        ArrayList<Boolean> res = new ArrayList<>();
        while (node != null && node.parent != null) {
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

    public static ArrayList<Boolean> compress(ArrayList<Integer> data, int[] freqs) {
        HuffmanTree tree = buildTree(freqs);
        ArrayList<Boolean> out = new ArrayList<>();
        for (int b : data) {
            out.addAll(findCode(tree.leaves[b]));
        }
        return out;
    }

    public static ArrayList<Integer> decompress(ArrayList<Boolean> bits, int[] freqs) {
        ArrayList<Integer> res = new ArrayList<>();
        HuffmanTree tree = buildTree(freqs);
        if (bits.isEmpty()) {
            // there was at most one byte in the source data
            for (HuffmanTreeNode leaf : tree.leaves) {
                if (leaf != null) {
                    for (int i = 0; i < freqs[leaf.data]; ++i) {
                        res.add(leaf.data);
                    }
                    break;
                }
            }
            return res;
        }
        HuffmanTreeNode node = tree.root;
        for (boolean bit : bits) {
            if (!bit && node.left != null) {
                node = node.left;
            } else if (bit && node.right != null) {
                node = node.right;
            }
            if (node.left == null) {
                res.add(node.data);
                node = tree.root;
            }
        }
        return res;
    }

    private static final byte[] headerMagik = new byte[]{ 84, 76, 1, 5 };
    
    // I'm sure there are standard ways to do this
    private static void writeInt(OutputStream outs, int value) throws IOException {
        outs.write(new byte[]{ (byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)(value) });
    }

    private static int readInt(InputStream ins) throws IOException {
        int b1 = ins.read();
        int b2 = ins.read();
        int b3 = ins.read();
        int b4 = ins.read();
        return (b1 << 24) + ((b2 << 16) & 0xff0000) + ((b3 << 8) & 0xff00) + (b4 & 0xff);
    }

    public static void writeHeader(OutputStream outs, int[] freqs, int actualBitCount) throws IOException {
        outs.write(headerMagik);
        writeInt(outs, actualBitCount);
        for (int f : freqs) {
            writeInt(outs, f);
        }
    }

    public static int readHeader(InputStream ins, int[] freqs) throws IOException {
        for (int i = 0; i < headerMagik.length; ++i) {
            if (ins.read() != headerMagik[i]) {
                throw new IllegalArgumentException("Bad file.");
            }
        }
        int actualBitCount = readInt(ins);
        for (int i = 0; i < 256; ++i) {
            freqs[i] = readInt(ins);
        }
        return actualBitCount;
    }

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
    
    public static void compressStream(InputStream ins, OutputStream outs) throws FileNotFoundException, IOException {
        ArrayList<Integer> data = new ArrayList<>();
        int c;
        while ((c = ins.read()) != -1) {
            data.add(c);
        }

        int[] freqs = calculateFrequencies(data);
        ArrayList<Boolean> compressed = compress(data, freqs);
        writeHeader(outs, freqs, compressed.size());
        writeBits(outs, compressed);

        System.out.println("Compressed/original = " + 100 * (compressed.size() / 8.0 + 8 + 4*256) / data.size() + "%");
    }

    public static void decompressStream(InputStream ins, OutputStream outs) throws FileNotFoundException, IOException {
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
