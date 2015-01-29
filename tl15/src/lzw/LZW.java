package lzw;

import bitstream.BitInputStream;
import bitstream.BitOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * An implementation of LZW-(de)compression.
 */
public class LZW {
    public final static int codeSize = 12;
    
    public static void compress(InputStream ins, BitOutputStream outs) throws IOException {
        LZWDictionary dict = new LZWDictionary(codeSize);
        ArrayList<Integer> string = new ArrayList<>();
        int b;
        int inputSize = 0;
        while ((b = ins.read()) != -1) {
            if (dict.isFull()) {
                dict.reset();
            }
            ++inputSize;
            string.add(b);
            if (dict.getCode(string) == -1) {
                outs.writeBits(codeSize, dict.getCode(string.subList(0, string.size() - 1)));
                dict.addString(string);
                string.clear();
                string.add(b);
            }
//            if (inputSize % 10240 == 0) {
//                System.out.println(inputSize / 1024 + "K compressed into " + (outs.getBitCount() / 8) / 1024 + "K");
//            }
        }
        if (!string.isEmpty()) {
            outs.writeBits(codeSize, dict.getCode(string));
        }
        
        System.out.println("Compressed/original (no headers): " + (100.0 * outs.getBitCount() / (inputSize * 8)) + " %");
    }

    public static void decompress(BitInputStream ins, OutputStream outs) throws IOException {
        int lastCode = (int)Math.pow(2, codeSize) - 1;
        HashMap<Integer, ArrayList<Integer>> dict = new HashMap<>();
        HashSet<ArrayList<Integer>> values = new HashSet<>();
        ArrayList<Integer> last = new ArrayList<>();
        int nextCode = 256;

        int inputSize = 0;
        int outputSize = 0;
        while (true) {
            if (nextCode == 256) {
                dict.clear();
                values.clear();
            }
            Integer code = ins.readBits(codeSize);
            if (code == null) {
                break;
            }
            inputSize += codeSize;
            if (code < 256 || (dict.get(code) != null && (code != 256 || nextCode > 256))) {
                ArrayList<Integer> cur;
                if (code < 256) {
                    cur = new ArrayList<>(Arrays.asList(code));
                } else {
                    cur = new ArrayList<>(dict.get(code));
                }
                for (int i : cur) {
                    outs.write(i);
                }
                outputSize += cur.size();
                last.add(cur.get(0));
                if (last.size() > 1 && !values.contains(last)) {
                    ArrayList<Integer> copy = new ArrayList<>(last);
                    dict.put(nextCode++, copy);
                    values.add(copy);
                    if (nextCode > lastCode) {
                        nextCode = 256;
                    }
                }
                last = cur;
            } else {
                ArrayList<Integer> cur = new ArrayList<>(last);//TODO:remove?
                cur.add(cur.get(0));
                for (int i : cur) {
                    outs.write(i);
                }
                outputSize += cur.size();
                ArrayList<Integer> copy = new ArrayList<>(cur);
                dict.put(nextCode++, copy);
                values.add(copy);
                if (nextCode > lastCode) {
                    nextCode = 256;
                }
                last = cur;
            }
//            if ((inputSize / 8) % 10240 == 0) {
//                System.out.println((inputSize / 8) / 1024 + "K decompressed into " + outputSize / 1024 + "K");
//            }
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
