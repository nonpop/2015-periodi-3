package tl15.lzw;

/**
 * An entry in LZWDictionary.
 */
public class LZWDictionaryEntry {
    private final int code;
    public final LZWDictionaryEntry parent;
    public LZWDictionaryEntry[] children = new LZWDictionaryEntry[256];

    public LZWDictionaryEntry(LZWDictionaryEntry parent, int code) {
        this.parent = parent;
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
