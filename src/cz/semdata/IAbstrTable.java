package cz.semdata;

import java.util.Iterator;

public interface IAbstrTable<K extends Comparable<K>, V> extends Iterable<V> {
    void zrus();
    boolean jePrazdny();
    V najdi(K key);
    void vloz(K key, V value);
    V odeber(K key);


    V select(int k);
    int rank(K key);

    Iterator<V> vytvorIterator(eTypProhl typ);
}