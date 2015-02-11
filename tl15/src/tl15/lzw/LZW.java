package tl15.lzw;

import tl15.utils.BitInputStream;
import tl15.utils.BitOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tl15.main.Main;
import tl15.utils.List;
import tl15.utils.Set;
import static tl15.utils.Math.twoTo;

/**
 * An implementation of LZW-(de)compression.
 */
public class LZW {
    private static int maxCodeSize() {
        return Main.opts.getOptionInteger("lzw.codeSize");
    }

    private static int lastCode() {
        return twoTo(maxCodeSize()) - 3;
    }

    private static int growCode(int codeSize) {
        return twoTo(codeSize) - 2;
    }

    private static int resetCode(int codeSize) {
        return twoTo(codeSize) - 1;
    }
    
    /**
     * Compress ins stream into outs. The output stream is not flushed.
     * @param ins
     * @param outs
     * @throws IOException
     */
    public static void compress(InputStream ins, BitOutputStream outs) throws IOException {
        LZWDictionary dict = new LZWDictionary();
        int resetCount = 0;
        int inputSize = 0;
        int currentCodeSize = 9;
        int b;
        while ((b = ins.read()) != -1) {
            inputSize += 8;
            if (!dict.hasNextChar(b)) {
                int code = dict.getCurrentCode();
                while (code >= growCode(currentCodeSize)) {
                    outs.writeBits(currentCodeSize, growCode(currentCodeSize));
                    ++currentCodeSize;
                }
                outs.writeBits(currentCodeSize, code);
                if (dict.getNextCode() <= lastCode()) {
                    dict.add(b);
                }
                dict.restartTraverse();
                dict.advance(b);
                if (dict.getNextCode() > lastCode()) {
                    outs.writeBits(currentCodeSize, resetCode(currentCodeSize));
                    dict.reset();
                    currentCodeSize = 9;
                    dict.advance(b);
                    ++resetCount;
                }
            } else {
                dict.advance(b);
            }
        }
        if (dict.isTraversing()) {
            outs.writeBits(currentCodeSize, dict.getCurrentCode());
        }

        System.out.println("Compressed/original (no headers): " + (100.0 * outs.getBitCount() / inputSize) + " %");
        System.out.println("Dictionary was reset " + resetCount + " times");
    }

    /**
     * Calculate a suitable size for the hash table in decompress();
     *
     * @return A very good number.
     */
    private static int hashTableSize() {
        // some prime close to 2^n+2^(n+1) should be best
//        return 769;
//        return 1531;    // TODO: This should depend on codeSize
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
    public static void decompress(BitInputStream ins, OutputStream outs) throws IOException {
        List<List<Integer>> dict = new List<>(lastCode() + 1, true);
        Set<List<Integer>> values = new Set<>(hashTableSize());
        int nextCode = 256;
        int curCodeSize = 9;

        List<Integer> lastOutput = new List<>();
        while (true) {
            Integer code = ins.readBits(curCodeSize);
            if (code == null) {
                break;
            }
            if (code == growCode(curCodeSize)) {
                ++curCodeSize;
                continue;
            }
            if (code == resetCode(curCodeSize)) {
                dict = new List<>(lastCode() + 1, true);
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
                if (nextCode <= lastCode()) {
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
     * @throws IOException
     */
    public static void compressFile(InputStream ins, OutputStream outs) throws IOException {
        BitOutputStream bouts = new BitOutputStream(outs);

        // the header
        bouts.writeBits(32, headerMagik);
        bouts.writeBits(5, Main.opts.getOptionInteger("lzw.codeSize"));

        LZW.compress(ins, bouts);
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
        LZW.decompress(bins, outs);
    }
}
