package tl15.main;

import tl15.utils.Options;
import tl15.huffman.Huffman;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import tl15.lzw.LZW;

/**
 * The user interface.
 */
public class Main {
    private static Options opts;

    /**
     * Initialize the option structure.
     * @param args
     */
    public static void initOptions(String[] args) {
        opts = new Options("java -jar tl15");
        opts.addOption("algorithm", "a", "algorithm_name", "lzw", "Choose the algorithm to use. Available algorithms: huffman, lzw");
        opts.addOption("inputFile", "i", "input_file", null, "The file to compress/decompress");   // TODO: allow -/empty for stdin
        opts.addOption("outputFile", "o", "output_file", null, "The file to write the compressed/decompressed data to");   // TODO: allow -/empty for stdout
        opts.addFlag("decompress", "d", "Decompress (default is to compress)");
        opts.addOption("lzw.codeSize", "ls", "code_size", 12, "The maximum code size for LZW compression. Must be between 9..31");
        opts.addFlag("help", "h", "Show this usage information");

        if (!opts.parse(args) || opts.getFlagState("help")) {
            opts.usage();
            opts = null;
            return;
        }

        System.out.println("\nOptions:");
        opts.dump();
        System.out.println();

        // TODO: fix that --v
        String alg = opts.getOptionString("algorithm");
        if (!alg.equals("huffman") && !alg.equals("lzw")) {
            System.out.println("Unknown algorithm: " + alg);
            opts = null;
            return;
        }
        if (opts.getOptionString("inputFile") == null) {
            System.out.println("no input file given");
            opts = null;
            return;
        }
        if (opts.getOptionString("outputFile") == null) {
            System.out.println("no output file given");
            opts = null;
            return;
        }
        int cs = opts.getOptionInteger("lzw.codeSize");
        if (cs < 9 || cs > 31) {
            System.out.println("lzw.codeSize out of range: " + cs);
            opts = null;
            return;
        }
    }

    public static void main(String[] args) throws IOException {
//        args = new String[]{"-i", "test.orig", "-o", "test.lc", "-ls", "16"};
//        args = new String[]{"-i", "test.lc", "-o", "test.ld", "-d"};
//        args = new String[]{"-i", "test.orig", "-o", "test.hc", "-a", "huffman"};
//        args = new String[]{"-i", "test.hc", "-o", "test.hd", "-d", "-a", "huffman"};
        initOptions(args);
        if (opts == null) {
            return;
        }
        String inp = opts.getOptionString("inputFile");
        String outp = opts.getOptionString("outputFile");

        try (InputStream ins = new BufferedInputStream(new FileInputStream(inp));
             OutputStream outs = new BufferedOutputStream(new FileOutputStream(outp)))
        {
            long start = System.nanoTime();
            if (opts.getOptionString("algorithm").equals("lzw")) {
                if (!opts.getFlagState("decompress")) {
                    LZW.init(opts.getOptionInteger("lzw.codeSize"));
                    LZW.compressFile(ins, outs);
                } else {
                    LZW.decompressFile(ins, outs);
                }
            } else {
                if (!opts.getFlagState("decompress")) {
                    Huffman.compressFile(ins, outs);
                } else {
                    Huffman.decompressFile(ins, outs);
                }
            }
            long end = System.nanoTime();
            System.out.println("Took " + (end - start) / 1000000 + "ms");
        }
    }
}
