package huffman;

import java.util.ArrayList;

public class BitOutput {
    private final ArrayList<Byte> data = new ArrayList<>();
    private byte lastByte;
    private int pos;

    public void putBit(boolean bit) {
        if (pos == 8) {
            data.add(lastByte);
            pos = 0;
        }
        int mask = 0x80 >> pos;
        if (bit) {
            lastByte |= mask;
        } else {
            lastByte &= ~mask;
        }
        ++pos;
    }

    public int getBitCount() {
        return data.size() * 8 + pos;
    }

    public boolean getBit(int position) {
        int byteNum = position / 8;
        int bitNum = position % 8;
        int b = (byteNum < data.size()) ? data.get(byteNum) : lastByte;
        return (b & (0x80 >> bitNum)) > 0;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getBitCount());
        for (int i = 0; i < getBitCount(); ++i) {
            s.append(getBit(i)? '1' : '0');
        }
        return s.toString();
    }
}
