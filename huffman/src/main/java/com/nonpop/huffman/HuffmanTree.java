package com.nonpop.huffman;

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

    @Override
    public int compareTo(HuffmanTree t) {
        return this.sum - t.sum;
    }
}
