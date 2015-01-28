package lzw;

/**
 * An entry in LZWDictionary.
 */
public class LZWDictionaryEntry {
    private int code;
    public LZWDictionaryEntry[] children = new LZWDictionaryEntry[256];

    public LZWDictionaryEntry(int code) {
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
