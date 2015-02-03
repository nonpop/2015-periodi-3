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
    public final int codeSize;
    public final int lastCode;

    public LZW(int codeSize) {
        assert(codeSize >= 9 && codeSize <= 31);
        this.codeSize = codeSize;
        lastCode = (int)Math.pow(2, codeSize) - 1;
    }
    
    public void compress(InputStream ins, BitOutputStream outs) throws IOException {
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
//            if (inputSize % 10240 == 0) {
//                System.out.println(inputSize / 1024 + "K compressed into " + (outs.getBitCount() / 8) / 1024 + "K");
//            }
        }
        if (!string.isEmpty()) {
            outs.writeBits(codeSize, dict.getCode(string));
        }
        
        System.out.println("Compressed/original (no headers): " + (100.0 * outs.getBitCount() / (inputSize * 8)) + " %");
    }

    public void decompress(BitInputStream ins, OutputStream outs) throws IOException {
        HashMap<Integer, ArrayList<Integer>> dict = new HashMap<>();
        HashSet<ArrayList<Integer>> values = new HashSet<>();
        for (int i = 0; i < 256; ++i) {
            ArrayList<Integer> l = new ArrayList<>(Arrays.asList(i));
            dict.put(i, l);
            values.add(l);
        }
        int nextCode = 256;

        ArrayList<Integer> lastOutput = new ArrayList<>();
        while (true) {
            Integer code = ins.readBits(codeSize);
            if (code == null) {
                break;
            }
            ArrayList<Integer> decoded = dict.get(code);
            ArrayList<Integer> toDict;
            if (decoded == null) {
                // code is not in the dictionary; this is the exception case
                // in the LZW decompression algorithm
                lastOutput.add(lastOutput.get(0));
                decoded = lastOutput;
                toDict = decoded;
            } else {
                toDict = new ArrayList<>(lastOutput);
                toDict.add(decoded.get(0));
                lastOutput = new ArrayList<>(decoded);
            }
            if (!values.contains(toDict)) {
                if (nextCode <= lastCode) {
                    dict.put(nextCode++, toDict);
                    values.add(toDict);
                }
            }
            for (int i : decoded) {
                outs.write(i);
            }
        }
    }

    public void compressFile(InputStream ins, OutputStream outs) throws IOException {
        BitOutputStream bouts = new BitOutputStream(outs);
        compress(ins, bouts);
        bouts.flush();
    }

    public void decompressFile(InputStream ins, OutputStream outs) throws IOException {
        decompress(new BitInputStream(ins), outs);
    }
}
