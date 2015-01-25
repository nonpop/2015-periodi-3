package huffman;

import static huffman.Huffman.compressStream;
import static huffman.Huffman.decompressStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void processFile(String inp, String outp, boolean compress) throws IOException {
        try (FileInputStream ins = new FileInputStream(inp);
             FileOutputStream outs = new FileOutputStream(outp))
        {
            if (compress) {
                compressStream(ins, outs);
            } else {
                decompressStream(ins, outs);
            }
        }
    }

    public static void usage() {
        System.out.println("To compress 'infile' to 'outfile': java Huffman -c infile outfile");
        System.out.println("To decompress 'infile' to 'outfile': java Huffman -d infile outfile");
    }
    
    public static void main(String[] args) throws IOException {
        //args = new String[]{ "-c", "test.orig", "test.compressed" };
        //args = new String[]{ "-d", "test.compressed", "test.decompressed" };
        if (args.length != 3) {
            usage();
        } else {
            if (args[0].equals("-c")) {
                processFile(args[1], args[2], true);
            } else if (args[0].equals("-d")) {
                processFile(args[1], args[2], false);
            } else {
                usage();
            }
        }
    }
}
