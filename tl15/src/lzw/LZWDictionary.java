package lzw;

import static utils.Math.max;
import static utils.Math.min;
import static utils.Math.twoTo;


/**
 * Implements an LZW dictionary with fixed-size code words. The code word size
 * is given to the constructor. When the dictionary is full the addString()
 * method will just return.
 */
public class LZWDictionary {
    private final int maxCodeSize;
    private final int lastCode;
    private final LZWDictionaryEntry root = new LZWDictionaryEntry(-1);
    private int nextCode = 256;
    private LZWDictionaryEntry currentEntry = root;

    /**
     *
     * @param maxCodeSize The code word size in bits. Must be 9..31.
     */
    public LZWDictionary(int maxCodeSize) {
        this.maxCodeSize = maxCodeSize;
        lastCode = twoTo(maxCodeSize) - 2;     // 2^(codeSize-1) is the 
                                            // dictionary reset code
        for (int i = 0; i < 256; ++i) {
            root.children[i] = new LZWDictionaryEntry(i);
        }
    }

    public boolean isFull() {
        return nextCode > lastCode;
    }

    public void reset() {
        for (int i = 0; i < 256; ++i) {
//            root.children[i].invalidate();        // for some reason this is much slower than creating a new object
            root.children[i] = new LZWDictionaryEntry(i);
        }
        nextCode = 256;
        currentEntry = root;
    }

    public boolean hasNextChar(Integer character) {
        return currentEntry.children[character] != null && currentEntry.children[character].isValid();
    }

    public Integer getCurrentCode() {
        return currentEntry.getCode();
    }

    /**
     * Add code and restart traversing.
     * @param character 
     * @return True if the next addition will grow code size.
     */
    public boolean add(Integer character) {
        if (isFull()) {
            currentEntry = root.children[character];
            return false;
        }
        currentEntry.children[character] = new LZWDictionaryEntry(nextCode++);
        currentEntry = root.children[character];
        if (nextCode == twoTo(currentCodeSize()) - 2) {
            nextCode += 2;
        }

        return nextCode == twoTo(currentCodeSize()) - 3 && currentCodeSize() < maxCodeSize;
    }

    private int currentCodeSize() {
        int res = 8;
        int c = nextCode >> 8;
        while (c > 0) {
            c >>= 1;
            ++res;
        }
        return max(9, res);
    }

    public void advance(Integer character) {
        currentEntry = currentEntry.children[character];
    }

    public boolean isTraversing() {
        return currentEntry != root;
    }
}
