package lzw;

import bitstream.BitInputStream;
import bitstream.BitOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import utils.List;
import utils.Map;
import static utils.Math.twoTo;

/**
 * An implementation of LZW-(de)compression.
 */
public class LZW {

    public final int codeSize;
    public final int lastCode;
    public final int resetDict;

    public LZW(int codeSize, int resetDict) {
        assert(codeSize >= 9 && codeSize <= 31);
        this.codeSize = codeSize;
        lastCode = twoTo(codeSize) - 2;
        this.resetDict = resetDict;
    }

    /**
     * Compress ins stream into outs. The output stream is not flushed.
     * @param ins
     * @param outs
     * @throws IOException
     */
    public void compress(InputStream ins, BitOutputStream outs) throws IOException {
        LZWDictionary dict = new LZWDictionary(codeSize);
        List<Integer> longest = new List<>();
        int b;
        int inputSize = 0;
        // hits and misses when the dictionary is full
        int hits = 0;
        int misses = 0;
        int resetCount = 0;
        while ((b = ins.read()) != -1) {
            inputSize += 8;
            longest.add(b);
            if (dict.getCode(longest) == -1) {
                longest.removeLast();    // temporarily remove the last element
                // so we don't have to create a sublist
                outs.writeBits(codeSize, dict.getCode(longest));
                longest.add(b);
                if (dict.isFull()) {
                    ++misses;
                }
                dict.addString(longest);
                longest.clear();
                longest.add(b);
                if (dict.isFull()) {
                    double total = hits + misses;
                    if (total > 1000) {
                        if (100 * misses / total > resetDict) {
                            outs.writeBits(codeSize, lastCode + 1); // write dictionary reset code
                            dict.reset();
                            hits = 0;
                            misses = 0;
                            ++resetCount;
                        }
                    }
                }
            } else {
                if (dict.isFull()) {
                    ++hits;
                }
            }
        }
        if (!longest.isEmpty()) {
            outs.writeBits(codeSize, dict.getCode(longest));
        }

        System.out.println("Compressed/original (no headers): " + (100.0 * outs.getBitCount() / inputSize) + " %");
        if (resetDict < 100) {
            System.out.println("Dictionary was reset " + resetCount + " times");
        }
    }

    /**
     * Calculate a suitable size for the hash table in decompress();
     *
     * @return A very good number.
     */
    private int hashTableSize() {
        return 98299;  // a random prime close to the middle of 2^16 and 2^17
    }

    /**
     * Decompress ins into outs. The output stream is not flushed.
     *
     * @param ins
     * @param outs
     * @throws IOException
     */
    public void decompress(BitInputStream ins, BitOutputStream outs) throws IOException {
        Map<Integer, List<Integer>> dict = new Map<>(hashTableSize());
        Map<List<Integer>, Boolean> values = new Map<>(hashTableSize());
        for (int i = 0; i < 256; ++i) {
            List<Integer> l = new List<>();
            l.add(i);
            dict.put(i, l);
            values.put(l, true);
        }
        int nextCode = 256;

        List<Integer> lastOutput = new List<>();
        while (true) {
            Integer code = ins.readBits(codeSize);
            if (code == null) {
                break;
            }
            if (code == lastCode + 1) {
                dict.clear();
                values.clear();
                nextCode = 256;
                for (int i = 0; i < 256; ++i) {
                    List<Integer> l = new List<>();
                    l.add(i);
                    dict.put(i, l);
                    values.put(l, true);
                }
                lastOutput.clear();
                continue;
            }
            List<Integer> decoded = dict.get(code);
            List<Integer> toDict;
            if (decoded == null) {
                // code is not in the dictionary; this is the exception case
                // in the LZW decompression algorithm
                lastOutput.add(lastOutput.get(0));
                decoded = lastOutput;
                toDict = decoded;
            } else {
                toDict = new List<>(lastOutput);
                toDict.add(decoded.get(0));
                lastOutput = new List<>(decoded);
            }
            if (!values.containsKey(toDict)) {
                if (nextCode <= lastCode) {
                    dict.put(nextCode++, toDict);
                    values.put(toDict, true);
                }
            }
            for (int i : decoded) {
                outs.write(i);
            }
        }
    }

    private static final int headerMagik = ('T' << 24) | ('L' << 16) | (1 << 8) | 6;

    /**
     * Compress ins into outs using the given code size. A header is written to
     * the output stream.
     *
     * @param ins
     * @param outs
     * @param codeSize
     * @param resetDict
     * @throws IOException
     */
    public static void compressFile(InputStream ins, OutputStream outs, int codeSize, int resetDict) throws IOException {
        LZW lzw = new LZW(codeSize, resetDict);
        BitOutputStream bouts = new BitOutputStream(outs);

        // the header
        bouts.writeBits(32, headerMagik);
        bouts.writeBits(5, codeSize);

        lzw.compress(ins, bouts);
        bouts.flush();
    }

    /**
     * Decompress ins into outs. The input stream must contain a header.
     *
     * @param ins
     * @param outs
     * @throws IOException
     */
    public static void decompressFile(InputStream ins, OutputStream outs) throws IOException {
        BitInputStream bins = new BitInputStream(ins);
        BitOutputStream bouts = new BitOutputStream(outs);
        if (bins.readBits(32) != headerMagik) {
            throw new IllegalArgumentException("Bad file.");
        }
        int fileCodeSize = bins.readBits(5);
        LZW lzw = new LZW(fileCodeSize, 30);

        lzw.decompress(bins, bouts);
        bouts.flush();
    }
}
