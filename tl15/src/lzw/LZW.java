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
        int inputSize = 0;
        // hits and misses when the dictionary is full
        int hits = 0;
        int misses = 0;
        int resetCount = 0;
        int b;
        while ((b = ins.read()) != -1) {
            inputSize += 8;
            if (!dict.hasNextChar(b)) {
                outs.writeBits(codeSize, dict.getCurrentCode());
                dict.add(b);
                if (dict.isFull()) {
                    ++misses;
                    double total = hits + misses;
                    if (total > 1000) {
                        if (100 * misses / total > resetDict) {
                            outs.writeBits(codeSize, lastCode + 1); // write dictionary reset code
                            dict.reset();
                            dict.advance(b);
                            hits = 0;
                            misses = 0;
                            ++resetCount;
                        }
                    }
                }
            } else {
                dict.advance(b);
                if (dict.isFull()) {
                    ++hits;
                }
            }
        }
        if (dict.isTraversing()) {
            outs.writeBits(codeSize, dict.getCurrentCode());
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
        return 1531;    // TODO: This should bepend on codeSize
    }

    /**
     * Decompress ins into outs. The output stream is not flushed.
     *
     * @param ins
     * @param outs
     * @throws IOException
     */
    public void decompress(BitInputStream ins, OutputStream outs) throws IOException {
        Map<Integer, List<Integer>> dict = new Map<>(hashTableSize());
        Map<List<Integer>, Boolean> values = new Map<>(hashTableSize());
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
                lastOutput.clear();
                nextCode = 256;
                continue;
            }
            List<Integer> toDict;
            if (code < 256) {
                toDict = lastOutput;
                toDict.add(code);
                lastOutput = new List<>();
                lastOutput.add(code);
                outs.write(code);
            } else {
                List<Integer> decoded = dict.get(code);
                if (decoded == null) {
                    // code is not in the dictionary; this is the exception case
                    // in the LZW decompression algorithm
                    lastOutput.add(lastOutput.get(0));
                    decoded = lastOutput;
                    toDict = new List<>(decoded);
                } else {
                    toDict = new List<>(lastOutput);
                    toDict.add(decoded.get(0));
                    lastOutput = new List<>(decoded);
                }
                for (int i : decoded) {
                    outs.write(i);
                }
            }
            if (toDict.size() > 1 && !values.containsKey(toDict)) {
                if (nextCode <= lastCode) {
                    dict.put(nextCode++, toDict);
                    values.put(toDict, true);
                }
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
        if (bins.readBits(32) != headerMagik) {
            throw new IllegalArgumentException("Bad file.");
        }
        int fileCodeSize = bins.readBits(5);
        LZW lzw = new LZW(fileCodeSize, 30);
        lzw.decompress(bins, outs);
    }
}
