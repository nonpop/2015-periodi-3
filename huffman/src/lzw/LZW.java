package lzw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    public static ArrayList<Integer> decompress(ArrayList<Integer> data) {
        ArrayList<Integer> decompressed = new ArrayList<>();
        HashMap<Integer, ArrayList<Integer>> dict = new HashMap<>();
        for (int i = 0; i < 256; ++i) {
            dict.put(i, new ArrayList<>(Arrays.asList(i)));
        }
        ArrayList<Integer> last = new ArrayList<>();
        int nextCode = 256;
        for (int i = 0; i < data.size(); ++i) {
            int code = data.get(i);
            if (dict.containsKey(code)) {
                ArrayList<Integer> cur = new ArrayList<>(dict.get(code));
                decompressed.addAll(cur);
                last.add(cur.get(0));
                if (!dict.containsValue(last)) {
                    dict.put(nextCode++, last);
                }
                last = cur;
            } else {
                ArrayList<Integer> cur = new ArrayList<>(last);
                cur.add(cur.get(0));
                decompressed.addAll(cur);
                dict.put(nextCode++, cur);
                last = cur;
            }
        }
        return decompressed;
    }
}
