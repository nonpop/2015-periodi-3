package lzw;

public class LZWDictionaryEntry {
    private int code;
    public final LZWDictionaryEntry[] children = new LZWDictionaryEntry[256];
    /**
     * If accessTime < 0 then the entry is never removed.
     */
    private long accessTime;
    private static long currentTime = 0;

    public LZWDictionaryEntry(int code, boolean permanent) {
        this.code = code;
        accessTime = permanent? -1 : 0;
    }

    public int getCode() {
        if (accessTime >= 0) {
            accessTime = currentTime++;
        }
        return code;
    }

    public long getAccessTime() {
        return accessTime;
    }

    public void clear() {
        code = -1;
    }

    public boolean isValid() {
        return code >= 0;
    }
}
