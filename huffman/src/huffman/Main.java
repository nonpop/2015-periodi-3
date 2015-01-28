package huffman;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lzw.LZW;

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
        System.out.println("To compress 'infile' to 'outfile' using Huffman coding: java Huffman -hc infile outfile");
        System.out.println("To decompress 'infile' to 'outfile' using Huffman coding: java Huffman -hd infile outfile");
        System.out.println("To compress 'infile' to 'outfile' using LZW coding: java Huffman -lc infile outfile");
        System.out.println("To decompress 'infile' to 'outfile' using LZW coding: java Huffman -ld infile outfile");
    }
    
    public static void main(String[] args) throws IOException {
//        InputStream ins = new FileInputStream("test.orig");
//        byte[] data = new byte[1024];
//        for (int i = 0; i < 1024; ++i) {
//            data[i] = (byte) ins.read();
//        }
        //args = new String[]{ "-hc", "test.orig", "test.hc" };
        //args = new String[]{ "-hd", "test.hc", "test.hd" };
        args = new String[]{ "-lc", "test.orig", "test.lc" };
        //args = new String[]{ "-ld", "test.lc", "test.ld" };
        if (args.length != 3) {
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
        }
    }
}
