package utils;

public class Math {
    public static int twoTo(int n) {
        int res = 1;
        while (n-- > 0) {
            res *= 2;
        }
        return res;
    }

    public static int max(int a, int b) {
        return (a > b)? a : b;
    }
}
