package com.nonpop.huffman;

import java.util.Objects;

public class Pair<S,T> {
    public final S fst;
    public final T snd;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.fst);
        hash = 89 * hash + Objects.hashCode(this.snd);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (!Objects.deepEquals(this.fst, other.fst)) {
            return false;
        }
        if (!Objects.deepEquals(this.snd, other.snd)) {
            return false;
        }
        return true;
    }

    public Pair(S fst, T snd) {
        this.fst = fst;
        this.snd = snd;
    }
}
