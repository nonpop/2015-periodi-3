package huffman;

// a fixed-size minheap containing HuffmanTreeNodes
public class HuffmanHeap {
    private final int limit;
    private final HuffmanTreeNode[] heap;
    private int size;

    public HuffmanHeap(int limit) {
        this.limit = limit;
        heap = new HuffmanTreeNode[limit];
        this.size = 0;
    }

    private int parent(int child) {
        return (child - 1) / 2;
    }

    private int left(int parent) {
        return parent * 2 + 1;
    }

    private int right(int parent) {
        return parent * 2 + 2;
    }

    private void swap(int a, int b) {
        HuffmanTreeNode tmp = heap[a];
        heap[a] = heap[b];
        heap[b] = tmp;
    }

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

    public HuffmanTreeNode peek() {
        return heap[0];
    }

    public HuffmanTreeNode pop() {
        HuffmanTreeNode min = heap[0];
        heap[0] = heap[--size];
        heapify(0);
        return min;
    }

    public void push(HuffmanTreeNode x) {
        int i = size++;
        while (i > 0 && heap[parent(i)].sum > x.sum) {
            heap[i] = heap[parent(i)];
            i = parent(i);
        }
        heap[i] = x;
    }
    
    public int size() {
        return size;
    }
}
