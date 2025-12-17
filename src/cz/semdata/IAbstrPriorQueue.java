package cz.semdata;

import java.util.Iterator;

public interface IAbstrPriorQueue<T> extends Iterable<T> {
    void zrus();
    boolean jePrazdny();

    void vloz(T data);

    T odeberMax();
    T zpristupniMax();


    void zmenPrioritu(T starData, T novaData);

    T select(int k);
    int rank(T data);

    Iterator<T> iterator();
}