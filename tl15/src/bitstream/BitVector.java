package bitstream;

/** A stripped-down ArrayList of booleans */
public class BitVector {
    private boolean[] bits;
    private int size = 0;

//    public BitVector() {
//        bits = new boolean[1];      // don't put 0 here; otherwise grow() won't work
//    }

    /**
     * 
     * @param initialSize The initial size of the vector in bits. Must be > 0!
     */
    public BitVector(int initialSize) {
        bits = new boolean[initialSize];
    }

    /**
     * 
     * @return The number of bits in the vector.
     */
    public int size() {
        return size;
    }

    /**
     * Get a bit from the vector.
     * @param i The bit to get.
     * @return The bit.
     */
    public boolean get(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException();
        }
        return bits[i];
    }

    /**
     * Add a bit to the end of the vector.
     * @param bit The bit to put.
     */
    public void add(boolean bit) {
        if (size < bits.length) {
            bits[size++] = bit;
        } else {
            grow();
            add(bit);
        }
    }

    /**
     * Double the capacity of the vector.
     */
    private void grow() {
        boolean[] newBits = new boolean[bits.length * 2];
        for (int i = 0; i < bits.length; ++i) {
            newBits[i] = bits[i];
        }
        bits = newBits;
    }

    /**
     * Reverse the order of the bits in the vector.
     */
    public void reverse() {
        for (int i = 0; i < size / 2; ++i) {
            boolean tmp = bits[i];
            bits[i] = bits[(size - 1) - i];
            bits[(size - 1) - i] = tmp;
        }
    }

    /** 
     * Remove first bit of the vector.
     */
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
