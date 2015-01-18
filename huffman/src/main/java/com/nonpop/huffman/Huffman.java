package com.nonpop.huffman;

import java.util.ArrayList;
import java.util.Collections;
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

    public static HuffmanTree buildTree(int[] freqs) {
        HuffmanTreeNode[] leaves = new HuffmanTreeNode[256];
        PriorityQueue<HuffmanTreeNode> q = new PriorityQueue<>();
        for (int b = 0; b < 256; ++b) {
            int freq = freqs[b];
            if (freq > 0) {
                HuffmanTreeNode t = new HuffmanTreeNode(freq, null, null, b);
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

    public static ArrayList<Boolean> compress(int[] data, int[] freqs) {
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
                    res.add(leaf.data);
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

    public static void main(String[] args) {
    }
    
}
