package lzw;

import java.util.ArrayList;

public class LZWDictionaryEntry {
    public final int data;
    public final int code;
    public final ArrayList<LZWDictionaryEntry> children = new ArrayList<>();

    public LZWDictionaryEntry(int data, int code) {
        this.data = data;
        this.code = code;
    }
}
