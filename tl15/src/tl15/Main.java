package tl15;

import utils.Options;
import huffman.Huffman;
import utils.ResettableFileInputStream;
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
        opts.addOption("lzw.codeSize", "ls", "code_size", 12, "The code size for LZW compression. Must be between 9..31");
        opts.addOption("lzw.resetDict", "lr", "treshold", 20, "Reset the LZW compression dictionary when its poorness is > treshold %. Set to 100 to disable.");
        // TODO: -h and -v

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
            return null;
        }
        if (opts.getOptionString("inputFile") == null) {
            System.out.println("no input file given");
            return null;
        }
        if (opts.getOptionString("outputFile") == null) {
            System.out.println("no output file given");
            return null;
        }
        int cs = opts.getOptionInteger("lzw.codeSize");
        if (cs < 9 || cs > 31) {
            System.out.println("lzw.codeSize out of range: " + cs);
            return null;
        }
        int poorness = opts.getOptionInteger("lzw.resetDict");
        if (poorness < 0 || poorness > 100) {
            System.out.println("lzw.resetDict out of range: " + poorness);
            return null;
        }

        return opts;
    }

    public static void main(String[] args) throws IOException {
        args = new String[]{"-i", "test.orig", "-o", "test.lc", "-ls", "16", "-lr", "20"};
        //args = new String[]{"-i", "test.lc", "-o", "test.ld", "-d"};
        //args = new String[]{"-i", "test.orig", "-o", "test.hc", "-a", "huffman"};
        //args = new String[]{"-i", "test.hc", "-o", "test.hd", "-d", "-a", "huffman"};
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
                if (!opts.getFlagState("decompress")) {
                    LZW.compressFile(ins, outs, 
                            opts.getOptionInteger("lzw.codeSize"),
                            opts.getOptionInteger("lzw.resetDict"));
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
