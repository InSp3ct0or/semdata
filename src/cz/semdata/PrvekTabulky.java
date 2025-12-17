package cz.semdata;

import java.util.Objects;

public class PrvekTabulky<K extends Comparable<K>, V> {
    private final K key;
    private V value;

    public PrvekTabulky(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrvekTabulky<?, ?> that = (PrvekTabulky<?, ?>) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}
