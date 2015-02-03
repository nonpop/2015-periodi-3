package bitstream;

import java.io.IOException;
import java.io.InputStream;

/**
 * A bit input stream.
 */
public class BitInputStream extends InputStream {
    private final InputStream ins;

    /**
     * Position in the current byte. 8 indicates a new byte must be read from ins.
     */
    private int bytePos = 8;

    /**
     * The current byte. bytePos is a 0-based index into this.
     */
    private int curByte;

    /**
     * 
     * @param ins The stream to convert into a bit stream.
     */
    public BitInputStream(InputStream ins) {
        this.ins = ins;
    }

    /**
     * Read bits from the stream.
     * @param n How many bits to read. Can be between 0..32.
     * @return The bits read are the last n bits of the return value, in the order
     *         they were in the stream. The other bits of the return value are zero.
     *         Null is returned in case there were not enough bits in the stream.
     *         In this case the last bits of the stream are lost forever.
     * @throws java.io.IOException
     */
    public Integer readBits(int n) throws IOException {
        int result = 0;
        for (int i = n - 1; i >= 0; --i) {
            if (bytePos == 8) {
                curByte = ins.read();
                if (curByte == -1) {
                    return null;
                }
                bytePos = 0;
            }
            int bit = curByte & (0x80 >> bytePos);  // isolate the next bit
            bit >>= 7 - bytePos;    // move to position 1
            ++bytePos;
            bit <<= i;              // move to position i+1
            result |= bit;
        }
        return result;
    }

    @Override
    public int read() throws IOException {
        Integer result = readBits(8);
        return (result == null)? -1 : result;
    }
}
