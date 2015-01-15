package huffman;

import java.util.PriorityQueue;

public class HuffmanTree implements Comparable<HuffmanTree> {

    public HuffmanTree parent = null;
    public final int sum;
    public final HuffmanTree left, right;
    public final int data;

    public HuffmanTree(int sum, HuffmanTree left, HuffmanTree right, int data) {
        this.sum = sum;
        this.left = left;
        this.right = right;
        this.data = data;
    }

    // data should be an array of unsigned bytes
    private static int[] calculateFrequencies(int[] data) {
        int[] freqs = new int[256];
        for (int b : data) {
            ++freqs[b];
        }
        return freqs;
    }

    public static HuffmanTree buildTree(int[] data, HuffmanTree[] leaves /* assumed to be of size 256 */) {
        int[] freqs = calculateFrequencies(data);
        PriorityQueue<HuffmanTree> q = new PriorityQueue<>();
        for (int b = 0; b < 256; ++b) {
            int freq = freqs[b];
            if (freq > 0) {
                HuffmanTree t = new HuffmanTree(freq, null, null, b);
                leaves[b] = t;
                q.add(t);
            }
        }
        while (q.size() >= 2) {
            HuffmanTree a = q.poll();
            HuffmanTree b = q.poll();
            HuffmanTree t = new HuffmanTree(a.sum + b.sum, a, b, (byte)0);
            a.parent = t;
            b.parent = t;
            q.add(t);
        } 
        if (q.size() == 0) {
            return null;
        } else {
            return q.poll();
        }
    }

    public static Pair<Integer, Integer> findCode(HuffmanTree leaf) {
        if (leaf == null) {
            return new Pair<>(0, 0);
        }
        int code = 0;
        int pos = 0;
        while (true) {
            HuffmanTree parent = leaf.parent;
            if (parent == null) {
                break;
            }
            if (leaf == parent.right) {
                code += 1 << pos;
            }
            ++pos;
            leaf = parent;
        }
        code <<= 8 - pos;
        return new Pair<>(code, pos);
        
    }

    @Override
    public int compareTo(HuffmanTree t) {
        return this.sum - t.sum;
    }
}
