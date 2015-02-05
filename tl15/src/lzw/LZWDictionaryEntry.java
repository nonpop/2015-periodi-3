package lzw;

/**
 * An entry in LZWDictionary.
 */
public class LZWDictionaryEntry {
    private int code;
    public final LZWDictionaryEntry parent;
    public LZWDictionaryEntry[] children = new LZWDictionaryEntry[256];

    public LZWDictionaryEntry(LZWDictionaryEntry parent, int code) {
        this.parent = parent;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public boolean isValid() {
        return code >= 0;
    }

    public void invalidate() {
        code = -1;
    }
}
