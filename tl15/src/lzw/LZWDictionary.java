package lzw;

import utils.List;
import static utils.Math.twoTo;


/**
 * Implements an LZW dictionary with fixed-size code words. The code word size
 * is given to the constructor. When the dictionary is full the addString()
 * method will just return.
 */
public class LZWDictionary {
    private final int lastCode;
    private final LZWDictionaryEntry root = new LZWDictionaryEntry(-1);
    private int nextCode = 256;
    private LZWDictionaryEntry currentEntry = root;

    /**
     *
     * @param codeSize The code word size in bits. Must be 9..31.
     */
    public LZWDictionary(int codeSize) {
        lastCode = twoTo(codeSize) - 2;     // 2^(codeSize-1) is the 
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
     */
    public void add(Integer character) {
        if (isFull()) {
            return;
        }
        currentEntry.children[character] = new LZWDictionaryEntry(nextCode++);
        currentEntry = root.children[character];
    }

    public void advance(Integer character) {
        currentEntry = currentEntry.children[character];
    }

    public boolean isTraversing() {
        return currentEntry != root;
    }
}
