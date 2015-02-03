package lzw;

import utils.List;


/**
 * Implements an LZW dictionary with fixed-size code words. The code word size
 * is given to the constructor. When the dictionary is full the addString()
 * method will just return.
 */
public class LZWDictionary {
    private final int lastCode;
    private final LZWDictionaryEntry root = new LZWDictionaryEntry(-1);
    private int nextCode = 256;

    /**
     *
     * @param codeSize The code word size in bits. Must be 9..31.
     */
    public LZWDictionary(int codeSize) {
        lastCode = (int) Math.pow(2, codeSize) - 1;
        for (int i = 0; i < 256; ++i) {
            root.children[i] = new LZWDictionaryEntry(i);
        }
    }

    /**
     * Add a new string into the dictionary. It is assumed that the prefix
     * (all but the last character) is already in the dictionary but the string
     * itself is not.
     * @param string The string.
     */
    public void addString(List<Integer> string) {
        if (isFull()) {
            return;
        }

        LZWDictionaryEntry dict = root;
        for (int i = 0; i < string.size() - 1; ++i) {
            dict = dict.children[string.get(i)];
        }
        LZWDictionaryEntry entry = new LZWDictionaryEntry(nextCode++);
        int last = string.get(string.size() - 1);
        dict.children[last] = entry;
    }

    /**
     * Reset the dictionary. After this the dictionary will only contain
     * the 1-length strings.
     */
    public void reset() {
        for (LZWDictionaryEntry child : root.children) {
            for (int i = 0; i < 256; ++i) {
                if (child.children[i] != null) {
                    child.children[i].invalidate();
                }
            }
        }
        nextCode = 256;
    }

    /**
     * Get the code associated to the given string.
     * @param string The string.
     * @return The code.
     */
    public int getCode(Iterable<Integer> string) {
        LZWDictionaryEntry dict = root;
        for (int next : string) {
            if (dict.children[next] == null || !dict.children[next].isValid()) {
                return -1;
            }
            dict = dict.children[next];
        }
        return dict.getCode();
    }

    /**
     * Check if the dictionary is full.
     * @return True if the dictionary is full, false otherwise.
     */
    public boolean isFull() {
        return nextCode > lastCode;
    }
}
