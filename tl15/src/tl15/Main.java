package tl15;

import huffman.Huffman;
import huffman.ResettableFileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lzw.LZW;

/**
 * The user interface.
 */
public class Main {
    private static Options initOptions(String[] args) {
        Options opts = new Options("java -jar tl15");
        opts.addOption("algorithm", "a", "algorithm_name", "lzw", "Choose the algorithm to use. Available algorithms: huffman, lzw");
        opts.addOption("inputFile", "i", "input_file", null, "The file to compress/decompress");   // TODO: allow -/empty for stdin
        opts.addOption("outputFile", "o", "output_file", null, "The file to write the compressed/decompressed data to");   // TODO: allow -/empty for stdout
        opts.addFlag("decompress", "d", "Decompress (default is to compress)");
        opts.addOption("lzw.codeSize", "ls", "code_size", 12, "The code size for LZW. Must be between 9..31");

        if (!opts.parse(args)) {
            opts.usage();
            return null;
        }

        System.out.println("\nOptions:");
        opts.dump();
        System.out.println();

        String alg = opts.getOptionString("algorithm");
        if (!alg.equals("huffman") && !alg.equals("lzw")) {
            System.out.println("Unknown algorithm: " + alg);
            opts.usage();
            return null;
        }
        if (opts.getOptionString("inputFile") == null) {
            System.out.println("no input file given");
            opts.usage();
            return null;
        }
        if (opts.getOptionString("outputFile") == null) {
            System.out.println("no output file given");
            opts.usage();
            return null;
        }
        int cs = opts.getOptionInteger("lzw.codeSize");
        if (cs < 9 || cs > 31) {
            System.out.println("lzw.codeSize out of range: " + cs);
            opts.usage();
            return null;
        }

        return opts;
    }

    public static void main(String[] args) throws IOException {
        //args = new String[]{"-i", "test.orig", "-o", "test.lc", "-c"};
        args = new String[]{"-i", "test.lc", "-o", "test.ld", "-d"};
        Options opts = initOptions(args);
        if (opts == null) {
            return;
        }
        String inp = opts.getOptionString("inputFile");
        String outp = opts.getOptionString("outputFile");

        try (InputStream ins = new ResettableFileInputStream(inp);
             OutputStream outs = new FileOutputStream(outp))
        {
            long start = System.nanoTime();
            if (opts.getOptionString("algorithm").equals("lzw")) {
                LZW l = new LZW(opts.getOptionInteger("lzw.codeSize"));
                if (!opts.getFlagState("decompress")) {
                    l.compressFile(ins, outs);
                } else {
                    l.decompressFile(ins, outs);
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
