package lzw;

public class LZWDictionaryEntry {
    private int code;
    public LZWDictionaryEntry[] children = new LZWDictionaryEntry[256];

    public LZWDictionaryEntry(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void clear() {
        code = -1;
        for (LZWDictionaryEntry child : children) {
            if (child != null) {
                child.clear();
            }
        }
    }

    public boolean isValid() {
        return code >= 0;
    }
}
