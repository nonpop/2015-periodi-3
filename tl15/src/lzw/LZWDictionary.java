package lzw;


/**
 * Implements an LZW dictionary with fixed-size code words.
 */
public class LZWDictionary {
    private final LZWDictionaryEntry root = new LZWDictionaryEntry(-1);
    private int nextCode = 256;
    private LZWDictionaryEntry currentEntry = root;

    /**
     *
     */
    public LZWDictionary() {
        for (int i = 0; i < 256; ++i) {
            root.children[i] = new LZWDictionaryEntry(i);
        }
    }

    public int getNextCode() {
        return nextCode;
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
     * Add code.
     * @param character 
     */
    public void add(Integer character) {
        currentEntry.children[character] = new LZWDictionaryEntry(nextCode++);
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
}
