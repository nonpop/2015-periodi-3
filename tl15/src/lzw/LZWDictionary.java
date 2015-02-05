package lzw;

import utils.List;


/**
 * Implements an LZW dictionary with fixed-size code words.
 */
public class LZWDictionary {
    private final LZWDictionaryEntry root = new LZWDictionaryEntry(null, -1);
    private int nextCode = 256;
    private LZWDictionaryEntry currentEntry = root;

    public LZWDictionary() {
        for (int i = 0; i < 256; ++i) {
            root.children[i] = new LZWDictionaryEntry(root, i);
        }
    }

    /**
     * 
     * @return The code the next string added will get.
     */
    public int getNextCode() {
        return nextCode;
    }

    /**
     * Reset the dictionary to its initial state.
     */
    public void reset() {
        for (int i = 0; i < 256; ++i) {
//            root.children[i].invalidate();        // for some reason this is much slower than creating a new object
            root.children[i] = new LZWDictionaryEntry(root, i);
        }
        nextCode = 256;
        currentEntry = root;
    }

    /**
     * Can we advance to the given character?
     * @param character Character we want to advance to.
     * @return Yes or no.
     */
    public boolean hasNextChar(Integer character) {
        return currentEntry.children[character] != null && currentEntry.children[character].getCode() >= 0;
    }

    /**
     * Get the code for the current string.
     * @return 
     */
    public Integer getCurrentCode() {
        return currentEntry.getCode();
    }

    /**
     * Add the current string + given character to the dictionary and add a code for it.
     * @param character 
     */
    public void add(Integer character) {
        currentEntry.children[character] = new LZWDictionaryEntry(currentEntry, nextCode++);
    }

    /**
     * Restart traversing from root.
     */
    public void restartTraverse() {
        currentEntry = root;
    }

    /**
     * Advance to a child node.
     * @param character 
     */
    public void advance(Integer character) {
        currentEntry = currentEntry.children[character];
    }

    /**
     * @return True if the current string is not empty.
     */
    public boolean isTraversing() {
        return currentEntry != root;
    }

    /**
     * @return The current string.
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
