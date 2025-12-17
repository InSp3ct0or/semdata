package cz.semdata;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class AbstrTableArray<K extends Comparable<K>, V> implements IAbstrTable<K, V> {

    private static final int DEFAULT_CAPACITY = 10;
    private PrvekTabulky<K, V>[] array;
    private int size;

    public AbstrTableArray() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public AbstrTableArray(int capacity) {
        if (capacity <= 0) capacity = DEFAULT_CAPACITY;
        this.array = new PrvekTabulky[capacity];
        this.size = 0;
    }

    @Override
    public void zrus() {
        for (int i = 0; i < size; i++) {
            array[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean jePrazdny() {
        return size == 0;
    }

    @Override
    public V najdi(K key) {
        int index = findIndex(key);
        return index != -1 ? array[index].getValue() : null;
    }

    private int findIndex(K key) {
        for (int i = 0; i < size; i++) {
            if (array[i].getKey().compareTo(key) == 0) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void vloz(K key, V value) {
        int index = findIndex(key);

        if (index != -1) {
            array[index].setValue(value);
            return;
        }

        ensureCapacity();
        array[size] = new PrvekTabulky<>(key, value);
        size++;
    }

    @Override
    public V odeber(K key) {
        int index = findIndex(key);

        if (index == -1) return null;

        V oldValue = array[index].getValue();

        array[index] = array[size - 1];
        array[size - 1] = null;
        size--;

        return oldValue;
    }

    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (size == array.length) {
            int newCapacity = array.length * 2;
            PrvekTabulky<K, V>[] newArray = new PrvekTabulky[newCapacity];
            System.arraycopy(array, 0, newArray, 0, size);
            array = newArray;
        }
    }

    @Override
    public V select(int k) {
        if (k < 1 || k > size) return null;
        return array[k - 1].getValue();
    }

    @Override
    public int rank(K key) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (array[i].getKey().compareTo(key) < 0) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Iterator<V> iterator() {
        return vytvorIterator(eTypProhl.HLOUBKA);
    }

    @Override
    public Iterator<V> vytvorIterator(eTypProhl typ) {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public V next() {
                if (!hasNext()) throw new NoSuchElementException();
                return array[index++].getValue();
            }
        };
    }
}
