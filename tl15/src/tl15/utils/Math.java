package tl15.utils;

/**
 * A couple simple math functions, just for the heck of it.
 */
public class Math {
    /**
     * Raise 2 to the given natural power.
     * @param n Must be non-negative.
     * @return 
     */
    public static int twoTo(int n) {
        assert(n >= 0);
        int res = 1;
        while (n-- > 0) {
            res *= 2;
        }
        return res;
    }

    /**
     * Return the larger of the given values.
     * @param a
     * @param b
     * @return 
     */
    public static int max(int a, int b) {
        return (a > b)? a : b;
    }

    /**
     * Return the smaller of the given values.
     * @param a
     * @param b
     * @return 
     */
    public static int min(int a, int b) {
        return (a < b)? a : b;
    }
}
