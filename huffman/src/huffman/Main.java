package huffman;

import static huffman.Huffman.compressStream;
import static huffman.Huffman.decompressStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void compressFile(String inp, String outp) throws IOException {
        FileInputStream ins = null;
        FileOutputStream outs = null;
        try {
            ins = new FileInputStream(inp);
            outs = new FileOutputStream(outp);
            compressStream(ins, outs);
        } finally {
            if (ins != null) {
                ins.close();
                ins = null;
            }
            if (outs != null) {
                outs.close();
                outs = null;
            }
        }
    }

    public static void decompressFile(String inp, String outp) throws IOException {
        FileInputStream ins = null;
        FileOutputStream outs = null;
        try {
            ins = new FileInputStream(inp);
            outs = new FileOutputStream(outp);
            decompressStream(ins, outs);
        } finally {
            if (ins != null) {
                ins.close();
                ins = null;
            }
            if (outs != null) {
                outs.close();
                outs = null;
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
                compressFile(args[1], args[2]);
            } else if (args[0].equals("-d")) {
                decompressFile(args[1], args[2]);
            } else {
                usage();
            }
        }
    }
}
