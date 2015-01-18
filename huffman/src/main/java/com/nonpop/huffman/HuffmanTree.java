package com.nonpop.huffman;

public class HuffmanTree {
    public final HuffmanTreeNode root;
    public final HuffmanTreeNode[] leaves;

    public HuffmanTree(HuffmanTreeNode root, HuffmanTreeNode[] leaves) {
        this.root = root;
        this.leaves = leaves;
    }
}
