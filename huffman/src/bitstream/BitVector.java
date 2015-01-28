package bitstream;

public class BitVector {
    private boolean[] bits;
    private int size = 0;

    public BitVector() {
        bits = new boolean[1];      // don't put 0 here; otherwise grow() won't work
    }

    public BitVector(int initialSize) {
        bits = new boolean[initialSize];
    }

    public int size() {
        return size;
    }

    public boolean get(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException();
        }
        return bits[i];
    }

    public void add(boolean bit) {
        if (size < bits.length) {
            bits[size++] = bit;
        } else {
            grow();
            add(bit);
        }
    }

    private void grow() {
        boolean[] newBits = new boolean[bits.length * 2];
        for (int i = 0; i < bits.length; ++i) {
            newBits[i] = bits[i];
        }
        bits = newBits;
    }

    public void reverse() {
        for (int i = 0; i < size / 2; ++i) {
            boolean tmp = bits[i];
            bits[i] = bits[(size - 1) - i];
            bits[(size - 1) - i] = tmp;
        }
    }

    public void removeFirst() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        for (int i = 1; i < size; ++i) {
            bits[i - 1] = bits[i];
        }
        --size;
    }
}
