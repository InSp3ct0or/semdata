package cz.semdata;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class AbstrTableUnSortedList<K extends Comparable<K>, V> implements IAbstrTable<K, V> {

    private final IAbstrDoubleList<PrvekTabulky<K, V>> list;

    public AbstrTableUnSortedList() {
        this.list = new AbstrDoubleList<>();
    }

    @Override
    public void zrus() {
        list.zrus();
    }

    @Override
    public boolean jePrazdny() {
        return list.jePrazdny();
    }

    @Override
    public V najdi(K key) {
        if (jePrazdny()) return null;

        try {
            list.zpristupniPrvni();
            do {
                PrvekTabulky<K, V> currentEntry = list.zpristupniAktualni();
                if (currentEntry.getKey().compareTo(key) == 0) {
                    return currentEntry.getValue();
                }
                list.zpristupniNaslednika();
            } while (true);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public void vloz(K key, V value) {
        if (najdi(key) != null) {

            PrvekTabulky<K, V> entry = najdiEntry(key);

            if (entry != null) {
                entry.setValue(value);
                return;
            }
        }

        list.vlozPosledni(new PrvekTabulky<>(key, value));
    }

    private PrvekTabulky<K, V> najdiEntry(K key) {
        if (jePrazdny()) return null;

        try {
            list.zpristupniPrvni();
            do {
                PrvekTabulky<K, V> currentEntry = list.zpristupniAktualni();
                if (currentEntry.getKey().compareTo(key) == 0) {
                    return currentEntry;
                }
                list.zpristupniNaslednika();
            } while (true);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public V odeber(K key) {
        if (jePrazdny()) return null;

        try {
            list.zpristupniPrvni();
            do {
                PrvekTabulky<K, V> currentEntry = list.zpristupniAktualni();
                if (currentEntry.getKey().compareTo(key) == 0) {
                    return list.odeberAktualni().getValue();
                }
                list.zpristupniNaslednika();
            } while (true);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public V select(int k) {
        if (k < 1 || k > list.size()) return null;

        int counter = 1;
        for (PrvekTabulky<K, V> entry : list) {
            if (counter == k) return entry.getValue();
            counter++;
        }
        return null;
    }

    @Override
    public int rank(K key) {
        int count = 0;
        for (PrvekTabulky<K, V> entry : list) {
            if (entry.getKey().compareTo(key) < 0) {
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
            private final Iterator<PrvekTabulky<K, V>> listIterator = list.iterator();

            @Override
            public boolean hasNext() {
                return listIterator.hasNext();
            }

            @Override
            public V next() {
                return listIterator.next().getValue();
            }
        };
    }
}
