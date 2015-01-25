package lzw;

public class LZWDictionaryEntry {
    public final int data;
    public final int code;
    public final LZWDictionaryEntry[] children = new LZWDictionaryEntry[256];

    public LZWDictionaryEntry(int data, int code) {
        this.data = data;
        this.code = code;
    }
}
