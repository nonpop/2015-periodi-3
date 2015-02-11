package tl15.huffman;

/**
 * A Huffman tree. It's really just a pair containing the root node and an
 * array of the leaves.
 */
public class HuffmanTree {
    public final HuffmanTreeNode root;
    public final HuffmanTreeNode[] leaves;

    public HuffmanTree(HuffmanTreeNode root, HuffmanTreeNode[] leaves) {
        this.root = root;
        this.leaves = leaves;
    }
}
