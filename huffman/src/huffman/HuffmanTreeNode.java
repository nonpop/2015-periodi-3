package huffman;

/**
 * One node in a Huffman tree.
 */
public class HuffmanTreeNode implements Comparable<HuffmanTreeNode> {

    public HuffmanTreeNode parent = null;
    public final HuffmanTreeNode left, right;

    /**
     * For leaf nodes this is the character whose frequency is in question.
     * For other nodes it is unused.
     */
    public final int data;

    /**
     * For leaf nodes this is the frequency of the character. For other nodes
     * this is the cumulative sum of the sums of the child nodes.
     */
    public final int sum;

    public HuffmanTreeNode(int sum, HuffmanTreeNode left, HuffmanTreeNode right, int data) {
        this.sum = sum;
        this.left = left;
        this.right = right;
        this.data = data;
    }

    /**
     * Compare two nodes by comparing their sum fields.
     */
    @Override
    public int compareTo(HuffmanTreeNode t) {
        return this.sum - t.sum;
    }
}
