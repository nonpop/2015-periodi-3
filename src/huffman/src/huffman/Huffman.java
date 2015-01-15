package huffman;

import java.util.ArrayList;
import java.util.List;

public class Huffman<T> {
    public static BitOutput compress(int[] data) {
        HuffmanTree[] leaves = new HuffmanTree[256];
        HuffmanTree tree = HuffmanTree.buildTree(data, leaves);
        BitOutput out = new BitOutput();
        for (int b : data) {
            Pair<Integer, Integer> p = HuffmanTree.findCode(leaves[b]);
            for (int i = 0; i < p.snd; ++i) {
                out.putBit((p.fst & 0x80 >> i) > 0);
            }
        }
        return out;
    }

    public static List<Integer> decompress(BitOutput bits, int[] data) {
        ArrayList<Integer> res = new ArrayList<>();
        HuffmanTree[] leaves = new HuffmanTree[256];
        HuffmanTree tree = HuffmanTree.buildTree(data, leaves);
        // data not used after this point

        int pos = 0;
        while (pos < bits.getBitCount()) {
            HuffmanTree node = tree;
            while (true) {
                boolean bit = bits.getBit(pos++);
                if (!bit && node.left != null) {
                    node = node.left;
                } else if (bit && node.right != null) {
                    node = node.right;
                } else {
                    res.add(node.data);
                    break;
                }
            }
        }
        return res;
    }

    public static int[] toIntArray(String s) {
        int[] res = new int[s.length()];
        for (int i = 0; i < s.length(); ++i) {
            res[i] = s.charAt(i);
        }
        return res;
    }

    public static void main(String[] args) {
        int[] data = toIntArray("Hello, World");
        BitOutput bits = compress(data);
        System.out.println(bits);
        List<Integer> out = decompress(bits, data);
        StringBuilder s = new StringBuilder(out.size());
        for (int i : out) {
            s.append((char) i);
        }
        System.out.println(s);
    }
    
}
