package lzw;

import bitstream.BitInputStream;
import bitstream.BitOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LZW {
    public final static int codeSize = 9;
    
    public static void compress(InputStream ins, BitOutputStream outs) throws IOException {
        LZWDictionary dict = new LZWDictionary(codeSize);
        ArrayList<Integer> string = new ArrayList<>();
        int b;
        int inputSize = 0;
        while ((b = ins.read()) != -1) {
            ++inputSize;
            string.add(b);
            if (dict.getCode(string) == -1) {
                outs.writeBits(codeSize, dict.getCode(string.subList(0, string.size() - 1)));
                dict.addString(string);
                string.clear();
                string.add(b);
            }
        }
        if (!string.isEmpty()) {
            outs.writeBits(codeSize, dict.getCode(string));
        }
        
        System.out.println("Compressed/original (no headers): " + (100.0 * outs.getBitCount() / (inputSize * 8)) + " %");
    }

    public static void decompress(BitInputStream ins, OutputStream outs) throws IOException {
        int lastCode = (int)Math.pow(2, codeSize) - 1;
        ArrayList<ArrayList<Integer>> dict = new ArrayList<>(lastCode + 1);
        for (int i = 0; i < 256; ++i) {
            dict.add(i, new ArrayList<>(Arrays.asList(i)));
        }
        ArrayList<Integer> last = new ArrayList<>();
        int nextCode = 256;

        while (true) {
            Integer code = ins.readBits(codeSize);
            if (code == null) {
                break;
            }
            if (dict.get(code) != null && (code != 256 || nextCode > 256)) {
                ArrayList<Integer> cur = new ArrayList<>(dict.get(code));
                for (int i : cur) {
                    outs.write(i);
                }
                last.add(cur.get(0));
                if (!dict.contains(last)) {
                    dict.add(nextCode++, last);
                    if (nextCode > lastCode) {
                        nextCode = 256;
                    }
                }
                last = cur;
            } else {
                ArrayList<Integer> cur = new ArrayList<>(last);
                cur.add(cur.get(0));
                for (int i : cur) {
                    outs.write(i);
                }
                dict.put(nextCode++, cur);
                if (nextCode > lastCode) {
                    nextCode = 256;
                }
                last = cur;
            }
        }
    }

    public static void compressFile(InputStream ins, OutputStream outs) throws IOException {
        BitOutputStream bouts = new BitOutputStream(outs);
        compress(ins, bouts);
        bouts.close();
    }

    public static void decompressFile(InputStream ins, OutputStream outs) throws IOException {
        decompress(new BitInputStream(ins), outs);
    }
}
