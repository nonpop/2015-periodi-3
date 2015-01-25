package lzw;

import java.util.ArrayList;

public class LZW {
    public static ArrayList<Integer> compress(ArrayList<Integer> data) {
        ArrayList<Integer> compressed = new ArrayList<>();
        LZWDictionary dict = new LZWDictionary(16);
        ArrayList<Integer> string = new ArrayList<>();
        for (int i = 0; i < data.size(); ++i) {
            string.add(data.get(i));
            if (dict.getCode(string) == -1) {
                compressed.add(dict.getCode(string.subList(0, string.size() - 1)));
                dict.addString(string);
                int last = string.get(string.size() - 1);
                string.clear();
                string.add(last);
            }
        }
        if (!string.isEmpty()) {
            compressed.add(dict.getCode(string));
        }
        return compressed;
    }
}
