package lzw;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements an LZW dictionary with fixed-size code words. The code word size
 * is given to the constructor. When the dictionary is full and a new string
 * is added, the item with the oldest last-referenced time is replaced.
 * 
 */
public class LZWDictionary {
    private final int lastCode;
    private final LZWDictionaryEntry root = new LZWDictionaryEntry(-1, true);
    private int nextCode = 256;

    /**
     * 
     * @param codeSize The code word size in bits. Must be 9..31. We require
     * >= 9 because otherwise no compression can be achieved, and <= 31 because
     * otherwise the codes will be interpreted as negative numbers, which won't
     * work.
     */
    public LZWDictionary(int codeSize) {
        lastCode = (int)Math.pow(2, codeSize) - 1;
        for (int i = 0; i < 256; ++i) {
            root.children[i] = new LZWDictionaryEntry(i, true);
        }
    }

    public void addString(ArrayList<Integer> string) {
        LZWDictionaryEntry dict = root;
        int i = 0;
        while (true) {
            int next = string.get(i++);
            if (dict.children[next] != null && dict.children[next].isValid()) {
                dict = dict.children[next];
            } else {
                dict.children[next] = new LZWDictionaryEntry(generateNextCode(), false);
                return;
            }
        }
    }

    private LZWDictionaryEntry findOldest(LZWDictionaryEntry dict, LZWDictionaryEntry oldest) {
        for (LZWDictionaryEntry child : dict.children) {
            if (child != null) {
                if (oldest.getAccessTime() < 0 || child.getAccessTime() < oldest.getAccessTime()) {
                    oldest = child;
                }
                oldest = findOldest(child, oldest);
            }
        }
        return oldest;
    }
    
    private int generateNextCode() {
        if (nextCode <= lastCode) {
            return nextCode++;
        } else {
            LZWDictionaryEntry oldestEntry = findOldest(root, root);
            if (oldestEntry.getAccessTime() < 0) {
                throw new IllegalStateException("You used codeSize <= 8, didn't you?");
            }
            int code = oldestEntry.getCode();
            oldestEntry.clear();
            return code;
        }
    }

    public int getCode(List<Integer> string) {
        LZWDictionaryEntry dict = root;
        for (int i = 0; i < string.size(); ++i) {
            int next = string.get(i);
            if (dict.children[next] == null) {
                return -1;
            }
            dict = dict.children[next];
        }
        return dict.getCode();
    }
}
