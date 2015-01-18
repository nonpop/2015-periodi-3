package com.nonpop.huffman;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class Huffman {
    // data should be an array of unsigned bytes
    public static int[] calculateFrequencies(int[] data) {
        int[] freqs = new int[256];
        for (int b : data) {
            ++freqs[b];
        }
        return freqs;
    }

    public static Pair<HuffmanTree, HuffmanTree[]> buildTree(int[] freqs) {
        HuffmanTree[] leaves = new HuffmanTree[256];
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
        HuffmanTree root = (q.size() > 0)? q.poll() : null;
        return new Pair<>(root, leaves);
    }

    public static Pair<Byte, Byte> findCode(HuffmanTree leaf) {
        byte code = 0;
        byte pos = 0;
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

    public static BitOutput compress(int[] data, int[] freqs) {
        Pair<HuffmanTree, HuffmanTree[]> tree = buildTree(freqs);
        BitOutput out = new BitOutput();
        for (int b : data) {
            Pair<Byte, Byte> p = findCode(tree.snd[b]);
            for (int i = 0; i < p.snd; ++i) {
                out.putBit((p.fst & 0x80 >> i) > 0);
            }
        }
        return out;
    }

    public static List<Integer> decompress(BitOutput bits, int[] freqs) {
        ArrayList<Integer> res = new ArrayList<>();
        Pair<HuffmanTree, HuffmanTree[]> tree = buildTree(freqs);
        int pos = 0;
        while (pos < bits.getBitCount()) {
            HuffmanTree node = tree.fst;
            while (true) {
                boolean bit = bits.getBit(pos);
                if (!bit && node.left != null) {
                    node = node.left;
                    ++pos;
                } else if (bit && node.right != null) {
                    node = node.right;
                    ++pos;
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
        int[] freqs = calculateFrequencies(data);
        BitOutput bits = compress(data, freqs);
        System.out.println(bits);
        List<Integer> out = decompress(bits, freqs);
        StringBuilder s = new StringBuilder(out.size());
        for (int i : out) {
            s.append((char) i);
        }
        System.out.println(s);
    }
    
}
