package utils;

/**
 * A pair of objects.
 * @param <S> Type of the first element.
 * @param <T> Type of the second element.
 */
public class Pair<S, T> {
    public final S first;
    public final T second;

    public Pair(S first, T second) {
        this.first = first;
        this.second = second;
    }
}
