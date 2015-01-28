package huffman;

/** 
 * A fixed-size minheap containing HuffmanTreeNodes.
 * This is pretty much just copied from "Tietorakenteet,
 * kevät 2011" by Matti Luukkainen.
 */
public class HuffmanHeap {
    private final HuffmanTreeNode[] heap;

    /** Current size of the heap. */
    private int size;

    /**
     * @param limit The size limit of the heap. Adding more than limit nodes
     *              will cause an explosion.
     */
    public HuffmanHeap(int limit) {
        heap = new HuffmanTreeNode[limit];
        this.size = 0;
    }

    /**
     * Get the parent node of child.
     * 
     * @param child The index of the child node. The result is meaningful iff
     *              child is in the interval (0,size).
     *              
     * @return The index of the parent node.
     */
    private int parent(int child) {
        return (child - 1) / 2;
    }

    /**
     * Get the left child of a node.
     * 
     * @param parent The index of the parent node. Must be in the interval [0,size).
     * @return The index of the left child node. The caller must check that the value
     *         is valid (i.e. in the interval [0,size)).
     */
    private int left(int parent) {
        return parent * 2 + 1;
    }

    /**
     * Get the right child of a node.
     * @see #left(int)
     */
    private int right(int parent) {
        return parent * 2 + 2;
    }

    /**
     * Swap two elements in the heap.
     * 
     * @param a Index of the first element. Must be in the interval [0, size).
     * @param b Index of the second element. Must be in the interval [0, size).
     */
    private void swap(int a, int b) {
        HuffmanTreeNode tmp = heap[a];
        heap[a] = heap[b];
        heap[b] = tmp;
    }

    /**
     * Fix the heap, assuming the subtrees of node are good.
     * 
     * @param node The node to start the fixing from. Must be in [0,size).
     */
    private void heapify(int node) {
        while (true) {
            int left = left(node);
            int right = right(node);
            if (right < size) {
                int smallest;
                if (heap[left].sum < heap[right].sum) {
                    smallest = left;
                } else {
                    smallest = right;
                }
                swap(node, smallest);
                node = smallest;
            } else {
                if (left == size - 1 && heap[node].sum > heap[left].sum) {
                    swap(node, left);
                }
                break;
            }
        }
    }

    /**
     * Return the least element of the heap, i.e. the HuffmanTreeNode with the least sum.
     * 
     * @return Guess.
     */
    public HuffmanTreeNode peek() {
        return heap[0];
    }

    /**
     * Return and remove the least element of the heap.
     * @see #peek()
     * @return The least element of the heap.
     */
    public HuffmanTreeNode pop() {
        HuffmanTreeNode min = heap[0];
        heap[0] = heap[--size];
        heapify(0);
        return min;
    }

    /**
     * Add an element to the heap.
     * 
     * @param x The element to add.
     * @see #HuffmanHeap(int)
     */
    public void push(HuffmanTreeNode x) {
        int i = size++;
        while (i > 0 && heap[parent(i)].sum > x.sum) {
            heap[i] = heap[parent(i)];
            i = parent(i);
        }
        heap[i] = x;
    }
    
    /**
     * @return The number of elements in the heap.
     */
    public int size() {
        return size;
    }
}
