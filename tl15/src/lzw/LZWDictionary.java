package lzw;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements an LZW dictionary with fixed-size code words. The code word size
 * is given to the constructor. When the dictionary is full and a new string is
 * added, the oldest added item is replaced.
 *
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
            // These are not added to the timeline because we don't want to
            // replace them ever. Same thing with root.
            root.children[i] = new LZWDictionaryEntry(i);
        }
    }

    public void addString(ArrayList<Integer> string) {
        LZWDictionaryEntry dict = root;
        for (int i = 0; i < string.size() - 1; ++i) {
            dict = dict.children[string.get(i)];
        }
        LZWDictionaryEntry entry = new LZWDictionaryEntry(nextCode++);
        int last = string.get(string.size() - 1);
        dict.children[last] = entry;
    }

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

    public int getCode(List<Integer> string) {
        LZWDictionaryEntry dict = root;
        for (int next : string) {
            if (dict.children[next] == null || !dict.children[next].isValid()) {
                return -1;
            }
            dict = dict.children[next];
        }
        return dict.getCode();
    }

    public boolean isFull() {
        return nextCode > lastCode;
    }
}
