package utils;

import java.util.Random;

public class DataSources {
    public static byte[] randomData(int size, boolean nonnegative) {
        byte[] res = new byte[size];
        Random r = new Random(42);
        for (int i = 0; i < size; ++i) {
            if (nonnegative) {
                res[i] = (byte)(r.nextInt(128));
            } else {
                res[i] = (byte)(r.nextInt(256));
            }
        }
        return res;
    }

    // data with byte i appearing with probability (i+1)/S, where
    // S = 1+2+...+256
    public static byte[] weighedLinearRandomData(int size) {
        byte[] res = new byte[size];
        Random r = new Random(42);
        final int S = 256*257/2;
        for (int i = 0; i < size; ++i) {
            int p = r.nextInt(S);
            for (int j = 0; j < 256; ++j) {
                p -= j;
                if (p <= j) {
                    res[i] = (byte)j;
                    break;
                }
            }
        }
        return res;
    }

    // data with byte i appearing with probability 2^i/S, where
    // S = 2^0 + 2^1 + ... + 2^255
    public static byte[] weighedExponentialRandomData(int size) {
        byte[] res = new byte[size];
        Random r = new Random(42);
        double S = java.lang.Math.pow(2, 255) - 1;
        for (int i = 0; i < size; ++i) {
            double p = r.nextDouble() * S;
            for (int j = 0; j < 256; ++j) {
                if (p <= j) {
                    res[i] = (byte)j;
                    break;
                }
                p /= 2;
            }
        }
        return res;
    }

    public static byte[] alternatingData(int size) {
        byte[] res = new byte[size];
        for (int i = 0; i < size; i += 2) {
            res[i] = 1;
        }
        return res;
    }

    public static byte[] consecutiveData(int size) {
        byte[] res = new byte[size];
        for (int i = 0; i < size; ++i) {
            res[i] = (byte)(i & 0xff);
        }
        return res;
    }
}
