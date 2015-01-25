package lzw;

import java.util.ArrayList;
import java.util.List;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Implements an LZW dictionary with fixed-size code words. The code word size
 * is given to the constructor. When the dictionary is full and a new string
 * is added, the item with the oldest last-referenced time is replaced.
 * 
 */
public class LZWDictionary {
    private final int lastCode;
    private final LZWDictionaryEntry root = new LZWDictionaryEntry(-1, -1);
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
            root.children.add(new LZWDictionaryEntry(i, i));
        }
    }

    public void addString(ArrayList<Integer> string) {
        LZWDictionaryEntry dict = root;
        int i = 0;
        while (i < string.size()) {
            int next = string.get(i++);
            int j = 0;
            while (j < dict.children.size()) {
                if (dict.children.get(j).data == next) {
                    dict = dict.children.get(j);
                    break;
                }
                ++j;
            }
            if (j == dict.children.size() && i == string.size()) {
                dict.children.add(new LZWDictionaryEntry(next, generateNextCode()));
                return;
            }
        }
    }

    private int generateNextCode() {
        if (nextCode <= lastCode) {
            return nextCode++;
        } else {
            throw new NotImplementedException();
        }
    }

    public int getCode(List<Integer> string) {
        LZWDictionaryEntry dict = root;
        int i = 0;
        while (i < string.size()) {
            int next = string.get(i++);
            int j = 0;
            while (j < dict.children.size()) {
                if (dict.children.get(j).data == next) {
                    dict = dict.children.get(j);
                    if (i == string.size()) {
                        return dict.code;
                    }
                }
                ++j;
            }
        }
        return -1;
    }
}
