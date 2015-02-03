package utils;

import java.util.Arrays;
import java.util.Iterator;

/** A stripped-down ArrayList.
 * @param <T> The type of the elements. */
public class List<T> implements Iterable<T> {
    private Object[] array;
    private int size = 0;

    public List() {
        array = new Object[16];      // don't put 0 here; otherwise grow() won't work
    }

    /**
     * 
     * @param initialSize The initial size of the vector in bits. Must be > 0!
     */
    public List(int initialSize) {
        if (initialSize <= 0) {
            throw new IllegalArgumentException("initialSize must be > 0");
        }
        array = new Object[initialSize];
    }

    public List(Iterable<T> source) {
        array = new Object[1];
        for (T element : source) {
            add(element);
        }
    }

    /**
     * 
     * @return The number of elements in the list.
     */
    public int size() {
        return size;
    }

    /**
     * Get an element  from the list.
     * @param i The index of the element to get.
     * @return The element.
     */
    public T get(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException();
        }
        return (T)array[i];
    }

    /**
     * Add an element to the end of the list.
     * @param element The element to add.
     */
    public void add(T element) {
        if (size < array.length) {
            array[size++] = element;
        } else {
            grow();
            add(element);
        }
    }

    /**
     * Double the capacity of the list.
     */
    private void grow() {
        Object[] newArray = new Object[array.length * 2];
        for (int i = 0; i < array.length; ++i) {
            newArray[i] = array[i];
        }
        array = newArray;
    }

    /**
     * Reverse the order of the elements in the list.
     */
    public void reverse() {
        for (int i = 0; i < size / 2; ++i) {
            Object tmp = array[i];
            array[i] = array[(size - 1) - i];
            array[(size - 1) - i] = tmp;
        }
    }

    /** 
     * Remove the first element of the list.
     */
    public void removeFirst() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        for (int i = 1; i < size; ++i) {
            array[i - 1] = array[i];
        }
        --size;
    }

    /**
     * Remove the last element of the list.
     */
    public void removeLast() {
        if (size == 0) {
            throw new IllegalStateException();
        }
        --size;
    }

    /**
     * Clears the list by setting the element counter to zero.
     * No resources are actually freed.
     */
    public void clear() {
        size = 0;
    }

    /**
     * Check if the list has no elements.
     * @return True if the list has no elements; otherwise false.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int curIdx = 0;

            @Override
            public boolean hasNext() {
                return curIdx < size;
            }

            @Override
            public T next() {
                return (T)array[curIdx++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Arrays.deepHashCode(this.array);
        hash = 11 * hash + this.size;
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
        final List<?> other = (List<?>) obj;
        if (!Arrays.deepEquals(this.array, other.array)) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        return true;
    }
}
