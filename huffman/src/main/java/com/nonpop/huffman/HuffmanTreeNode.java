package com.nonpop.huffman;

public class HuffmanTreeNode implements Comparable<HuffmanTreeNode> {
    public HuffmanTreeNode parent = null;
    public final int sum;
    public final HuffmanTreeNode left, right;
    public final int data;

    public HuffmanTreeNode(int sum, HuffmanTreeNode left, HuffmanTreeNode right, int data) {
        this.sum = sum;
        this.left = left;
        this.right = right;
        this.data = data;
    }

    @Override
    public int compareTo(HuffmanTreeNode t) {
        return this.sum - t.sum;
    }
}
