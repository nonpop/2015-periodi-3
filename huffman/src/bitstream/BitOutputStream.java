package bitstream;

import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream {
    private OutputStream outs;

    /**
     * Position in the current byte. 8 indicates the current byte must be written to outs.
     */
    private int bytePos = 0;

    /**
     * The current byte. bytePos is a 0-based index into this.
     */
    private int curByte;

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
            if (bytePos == 8) {
                outs.write(curByte);
                curByte = 0;
                bytePos = 0;
            }
            int bit = bits & (1 << i);
            if (bit != 0) {     // note: using >0 fails here because signed >:[
                curByte |= (0x80 >> bytePos);
            }
            ++bytePos;
        }
    }

    /**
     * Closes the stream making sure any remaining bits are written. If there
     * are n=1..7 bits remaining, then the last 8-n bits written are zeroes.
     */
    void close() throws IOException {
        if (outs != null) {
            if (bytePos > 0) {
                outs.write(curByte);
            }
            outs.close();
            outs = null;
        }
    }
}
