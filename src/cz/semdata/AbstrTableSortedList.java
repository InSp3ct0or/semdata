package cz.semdata;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AbstrTableSortedList<K extends Comparable<K>, V> implements IAbstrTable<K, V> {

    private final IAbstrDoubleList<PrvekTabulky<K, V>> list;

    public AbstrTableSortedList() {
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
    public void vloz(K key, V value) {
        PrvekTabulky<K, V> newEntry = new PrvekTabulky<>(key, value);

        if (list.jePrazdny()) {
            list.vlozPrvni(newEntry);
            return;
        }

        PrvekTabulky<K, V> existingEntry = najdiEntrySequential(key);
        if (existingEntry != null) {
            existingEntry.setValue(value);
            return;
        }

        try {
            list.zpristupniPrvni();
            do {
                PrvekTabulky<K, V> currentEntry = list.zpristupniAktualni();
                if (key.compareTo(currentEntry.getKey()) < 0) {
                    list.vlozPredchudce(newEntry);
                    return;
                }
                list.zpristupniNaslednika();
            } while (true);
        } catch (NoSuchElementException e) {
            list.vlozPosledni(newEntry);
        }
    }

    private PrvekTabulky<K, V> najdiEntrySequential(K key) {
        if (jePrazdny()) return null;

        try {
            list.zpristupniPrvni();
            do {
                PrvekTabulky<K, V> currentEntry = list.zpristupniAktualni();
                if (currentEntry.getKey().compareTo(key) == 0) {
                    return currentEntry;
                }
                if (currentEntry.getKey().compareTo(key) > 0) {
                    return null;
                }
                list.zpristupniNaslednika();
            } while (true);
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public V najdi(K key) {
        PrvekTabulky<K, V> entry = najdiEntrySequential(key);
        return entry != null ? entry.getValue() : null;
    }

    public V najdiBinary(K key) {
        if (jePrazdny()) return null;

        PrvekTabulky<K, V>[] array = copyListToArray();

        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = key.compareTo(array[mid].getKey());

            if (cmp < 0) {
                high = mid - 1;
            } else if (cmp > 0) {
                low = mid + 1;
            } else {
                return array[mid].getValue();
            }
        }
        return null;
    }

    private PrvekTabulky<K, V>[] copyListToArray() {
        int size = list.size();
        @SuppressWarnings("unchecked")
        PrvekTabulky<K, V>[] array = (PrvekTabulky<K, V>[]) new PrvekTabulky[size];

        int i = 0;
        for (PrvekTabulky<K, V> entry : list) {
            array[i++] = entry;
        }
        return array;
    }

    @Override
    public V odeber(K key) {
        if (jePrazdny()) return null;

        try {
            list.zpristupniPrvni();
            do {
                PrvekTabulky<K, V> currentEntry = list.zpristupniAktualni();
                int cmp = currentEntry.getKey().compareTo(key);

                if (cmp == 0) {
                    return list.odeberAktualni().getValue();
                }
                if (cmp > 0) {
                    return null;
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
            if (entry.getKey().compareTo(key) <= 0) {
                count++;
            } else {
                break;
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
