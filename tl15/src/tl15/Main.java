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
    public static void processFile(String inp, String outp, boolean compress, boolean lzw) throws IOException {
        try (InputStream ins = new ResettableFileInputStream(inp);
             OutputStream outs = new FileOutputStream(outp))
        {
            if (compress) {
                if (lzw) {
                    LZW.compressFile(ins, outs);
                } else {
                    Huffman.compressFile(ins, outs);
                }
            } else {
                if (lzw) {
                    LZW.decompressFile(ins, outs);
                } else {
                    Huffman.decompressFile(ins, outs);
                }
            }
        }
    }

    public static void usage() {
        System.out.println("Usage: java -jar tl15 -[hl][cd] infile outfile");
        System.out.println("h selects Huffman coding, l selects LZW coding");
        System.out.println("c compresses, d decompresses");
    }
    
    private static Options initOptions() {
        Options opts = new Options("java -jar tl15");
        opts.addOption("algorithm", "a", "algorithm_name", "lzw", "Choose the algorithm to use. Available algorithms: huffman, lzw");
        opts.addOption("inputFile", "i", "input_file", null, "The file to compress/decompress");   // TODO: allow -/empty for stdin
        opts.addOption("outputFile", "o", "output_file", null, "The file to write the compressed/decompressed data to");   // TODO: allow -/empty for stdout
        opts.addFlag("lzw.reset", "lr", "Reset the LZW dictionary when it becomes full instead of just dropping new entries");
        opts.addOption("lzw.codeSize", "ls", "code_size", "12", "The code size for LZW. Must be between 9..31");

        return opts;
    }
    
    public static void main(String[] args) throws IOException {
        //args = new String[]{ "-hc", "test.orig", "test.hc" };
        //args = new String[]{ "-hd", "test.hc", "test.hd" };
        //args = new String[]{ "-lc", "test.orig", "test.lc" };
        //args = new String[]{ "-ld", "test.lc", "test.ld" };
        Options opts = initOptions();
        opts.usage();
/*        if (args.length != 3) {
            usage();
        } else {
            switch (args[0]) {
                case "-hc":
                    processFile(args[1], args[2], true, false);
                    break;
                case "-hd":
                    processFile(args[1], args[2], false, false);
                    break;
                case "-lc":
                    processFile(args[1], args[2], true, true);
                    break;
                case "-ld":
                    processFile(args[1], args[2], false, true);
                    break;
                default:
                    usage();
                    break;
            }
        }*/
    }
}
