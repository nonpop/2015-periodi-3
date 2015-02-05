package lzw;

import utils.List;


/**
 * Implements an LZW dictionary with fixed-size code words.
 */
public class LZWDictionary {
    private final LZWDictionaryEntry root = new LZWDictionaryEntry(null, -1);
    private int nextCode = 256;
    private LZWDictionaryEntry currentEntry = root;

    /**
     *
     */
    public LZWDictionary() {
        for (int i = 0; i < 256; ++i) {
            root.children[i] = new LZWDictionaryEntry(root, i);
        }
    }

    public int getNextCode() {
        return nextCode;
    }

    public void reset() {
        for (int i = 0; i < 256; ++i) {
//            root.children[i].invalidate();        // for some reason this is much slower than creating a new object
            root.children[i] = new LZWDictionaryEntry(root, i);
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
     * Add code.
     * @param character 
     */
    public void add(Integer character) {
        currentEntry.children[character] = new LZWDictionaryEntry(currentEntry, nextCode++);
    }

    public void restartTraverse() {
        currentEntry = root;
    }

    public void advance(Integer character) {
        currentEntry = currentEntry.children[character];
    }

    public boolean isTraversing() {
        return currentEntry != root;
    }

    /**
     * @return 
     */
    public List<Integer> getString() {
        List<Integer> res = new List<>();
        LZWDictionaryEntry node = currentEntry;
        while (node != null) {
            res.add(node.getCode());
            node = node.parent;
        }
        res.reverse();
        return res;
    }
}
