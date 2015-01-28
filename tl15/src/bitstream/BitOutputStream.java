package bitstream;

import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream extends OutputStream {
    private OutputStream outs;

    /**
     * Current byte in the buffer.
     */
    private int bufferByte = 0;

    /** Current bit in the current byte in the buffer. */
    private int bufferBit = 0;

    /** Size of the buffer */
    private static final int bufferSize = 102400;
    
    /**
     * The buffer. <code>bufferPos</code> is a 0-based index into the *bits* of this.
     */
    private byte[] buffer = new byte[bufferSize];


    /**
     * Keeps track of how many bits have been written to the stream.
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
     * @param n How many bits to write. Can be between 0..32.
     * @param bits The bits to write are the last n bits of <code>bits</code>.
     * @throws java.io.IOException
     */
    public void writeBits(int n, int bits) throws IOException {
        for (int i = n - 1; i >= 0; --i) {
            if (bufferBit == 8) {
                ++bufferByte;
                bufferBit = 0;
            }
            if (bufferByte == bufferSize) {
                outs.write(buffer);
                buffer = new byte[bufferSize];
                bufferByte = 0;
                bufferBit = 0;
            }
            int bit = bits & (1 << i);
            if (bit != 0) {     // note: using >0 fails here because signed >:[
                buffer[bufferByte] |= (0x80 >> bufferBit);
            }
            ++bufferBit;
        }
        bitCount += n;
    }

    /**
     * Write bits to the stream.
     * @param bits The bits to write.
     * @throws java.io.IOException
     */
    public void writeBits(BitVector bits) throws IOException {
        for (int i = 0; i < bits.size(); ++i) {
            writeBits(1, bits.get(i)? 1 : 0);
        }
    }

    /**
     * Closes the stream making sure any remaining bits are written. If there
     * are n=1..7 bits remaining, then the last 8-n bits written are zeroes.
     * @throws java.io.IOException
     */
    @Override
    public void close() throws IOException {
        if (outs != null) {
            outs.write(buffer, 0, bufferByte + ((bufferBit > 0)? 1 : 0));
            outs.close();
            outs = null;
        }
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
