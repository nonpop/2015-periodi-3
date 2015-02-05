package utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A bit output stream.
 */
public class BitOutputStream extends OutputStream {
    private final OutputStream outs;

    private byte curByte = 0;
    private int bytePos = 0;

    /**
     * Keeps track of how many bits have been written to the stream (or buffer).
     */
    private int bitCount = 0;

    /**
     * 
     * @param outs The stream to convert into a bit stream.
     */
    public BitOutputStream(OutputStream outs) {
        this.outs = outs;
    }

    /**
     * Write bits to the stream.
     * @param bitsToWrite How many bits to write. Can be between 0..32.
     * @param theBits The bits to write are the last 'bitsToWrite' bits of <code>theBits</code>.
     * @throws java.io.IOException
     */
    public void writeBits(int bitsToWrite, int theBits) throws IOException {
        bitCount += bitsToWrite;
        while (bitsToWrite > 0) {
            if (bytePos == 8) {
                outs.write(curByte);
                curByte = 0;
                bytePos = 0;
            }
            if ((theBits & (1 << (bitsToWrite - 1))) != 0) {
                curByte |= (0x80 >> bytePos);
            }
            ++bytePos;
            --bitsToWrite;
        }
    }

    /**
     * Write bits to the stream.
     * @param bits The bits to write.
     * @throws java.io.IOException
     */
    public void writeBits(List<Boolean> bits) throws IOException {
        for (int i = 0; i < bits.size(); ++i) {
            writeBits(1, bits.get(i)? 1 : 0);
        }
    }

    /**
     * Write the remaining buffer to the stream. If there are 8p+n, where n=1..7,
     * bits remaining, then the last 8-n bits written are zeroes. Also flushes
     * the underlying stream.
     * @throws java.io.IOException
     */
    @Override
    public void flush() throws IOException {
        if (bytePos > 0) {
            outs.write(curByte);
        }
        bytePos = 0;
        curByte = 0;
        outs.flush();
    }

    /**
     * 
     * @return The number of bits written to the stream. Might not be a multiple of 8.
     */
    public int getBitCount() {
        return bitCount;
    }

    @Override
    public void write(int i) throws IOException {
        writeBits(8, i);
    }
}
