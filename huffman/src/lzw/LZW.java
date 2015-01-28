package lzw;

import bitstream.BitInputStream;
import bitstream.BitOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class LZW {
    public final static int codeSize = 16;
    
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
            if (inputSize % 10240 == 0) {
                System.out.println(inputSize / 1024 + "K compressed into " + outs.getBitCount() / 1024 + "K");
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
        for (int i = 0; i < lastCode + 1; ++i) {
            if (i < 256) {
                dict.add(new ArrayList<>(Arrays.asList(i)));
            } else {
                dict.add(null);
            }
        }
        ArrayList<Integer> last = new ArrayList<>();
        int nextCode = 256;

        int inputSize = 0;
        int outputSize = 0;
        while (true) {
            if (nextCode == 256) {
                for (int i = 256; i < dict.size(); ++i) {
                    dict.set(i, null);
                }
            }
            Integer code = ins.readBits(codeSize);
            if (code == null) {
                break;
            }
            inputSize += codeSize;
            if (dict.get(code) != null && (code != 256 || nextCode > 256)) {
                ArrayList<Integer> cur = new ArrayList<>(dict.get(code));
                for (int i : cur) {
                    outs.write(i);
                }
                outputSize += cur.size();
                last.add(cur.get(0));
                if (!dict.contains(last)) {
                    dict.set(nextCode++, new ArrayList<>(last));
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
                dict.set(nextCode++, new ArrayList<>(cur));
                if (nextCode > lastCode) {
                    nextCode = 256;
                }
                last = cur;
            }
            if ((inputSize / 8) % 10240 == 0) {
                System.out.println((inputSize / 8) / 1024 + "K decompressed into " + outputSize / 1024 + "K");
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
