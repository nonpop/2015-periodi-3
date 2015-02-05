package lzw;

import bitstream.BitInputStream;
import bitstream.BitOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import utils.List;
import utils.Set;
import static utils.Math.twoTo;

/**
 * An implementation of LZW-(de)compression.
 */
public class LZW {

    public final int maxCodeSize;
    public final int lastCode;
    public final int resetDict;

    public LZW(int codeSize, int resetDict) {
        assert(codeSize >= 9 && codeSize <= 31);
        this.maxCodeSize = codeSize;
        lastCode = twoTo(codeSize) - 3;
        this.resetDict = resetDict;
    }

    /**
     * Compress ins stream into outs. The output stream is not flushed.
     * @param ins
     * @param outs
     * @throws IOException
     */
    public void compress(InputStream ins, BitOutputStream outs) throws IOException {
        LZWDictionary dict = new LZWDictionary();
        int inputSize = 0;
        // hits and misses when the dictionary is full
        int hits = 0;
        int misses = 0;
        int resetCount = 0;
        int currentCodeSize = 9;
        int nextGrow = twoTo(currentCodeSize) - 2;
        int b;
        while ((b = ins.read()) != -1) {
            inputSize += 8;
            if (!dict.hasNextChar(b)) {
                int code = dict.getCurrentCode();
                while (code >= nextGrow) {
                    outs.writeBits(currentCodeSize, twoTo(currentCodeSize) - 2);
                    ++currentCodeSize;
                    nextGrow = twoTo(currentCodeSize) - 2;
                }
                outs.writeBits(currentCodeSize, code);
                if (dict.getNextCode() <= lastCode) {
                    dict.add(b);
                }
                dict.restartTraverse();
                dict.advance(b);
                if (dict.getNextCode() > lastCode) {
                    // dictionary is full
                    ++misses;
                    double total = hits + misses;
                    if (total > 1000) {
                        if (100 * misses / total > resetDict) {
                            outs.writeBits(currentCodeSize, twoTo(currentCodeSize) - 1);
                            dict.reset();
                            currentCodeSize = 9;
                            nextGrow = twoTo(currentCodeSize) - 2;
                            dict.advance(b);
                            hits = 0;
                            misses = 0;
                            ++resetCount;
                        }
                    }
                }
            } else {
                dict.advance(b);
                if (dict.getNextCode() > lastCode) {
                    ++misses;
                }
            }
        }
        if (dict.isTraversing()) {
            outs.writeBits(currentCodeSize, dict.getCurrentCode());
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
        // some prime close to 2^n+2^(n+1) should be best
//        return 769;
//        return 1531;    // TODO: This should bepend on codeSize
//        return 3067;
//        return 6143;
        return 12289;
    }
    
    /**
     * Decompress ins into outs. The output stream is not flushed.
     *
     * @param ins
     * @param outs
     * @throws IOException
     */
    public void decompress(BitInputStream ins, OutputStream outs) throws IOException {
        List<List<Integer>> dict = new List<>(lastCode + 1);
        dict.setSize(lastCode + 1);
        Set<List<Integer>> values = new Set<>(hashTableSize());
        int nextCode = 256;
        int curCodeSize = 9;

        List<Integer> lastOutput = new List<>();
        while (true) {
            Integer code = ins.readBits(curCodeSize);
            if (code == null) {
                break;
            }
            if (code == twoTo(curCodeSize) - 2) {
                ++curCodeSize;
                continue;
            }
            if (code == twoTo(curCodeSize) - 1) {
                dict.clear();
                dict.setSize(lastCode + 1);
                values.clear();
                lastOutput.clear();
                nextCode = 256;
                curCodeSize = 9;
//                System.out.println("values load factor = " + values.loadFactor());
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
            if (toDict.size() > 1 && !values.contains(toDict)) {
                if (nextCode <= lastCode) {
                    dict.set(nextCode++, toDict);
                    values.put(toDict);
                }
            }
        }
//        System.out.println("values load factor = " + values.loadFactor());
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
        System.out.println("Using max code size " + fileCodeSize);
        LZW lzw = new LZW(fileCodeSize, 30);
        lzw.decompress(bins, outs);
    }
}
