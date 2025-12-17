package cz.semdata;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AbstrPriorQueue<T> implements IAbstrPriorQueue<T> {

    private final IAbstrDoubleList<T> sortedList;
    private final IAbstrDoubleList<T> unsortedList;
    private final Comparator<T> comparator;

    public AbstrPriorQueue(Comparator<T> comparator) {
        this.sortedList = new AbstrDoubleList<>();
        this.unsortedList = new AbstrDoubleList<>();
        this.comparator = comparator;
    }

    @Override
    public void zrus() {
        sortedList.zrus();
        unsortedList.zrus();
    }

    @Override
    public boolean jePrazdny() {
        return sortedList.jePrazdny() && unsortedList.jePrazdny();
    }

    @Override
    public void vloz(T data) {
        if (sortedList.jePrazdny()) {
            vlozDoSorted(data);
            return;
        }

        try {
            T lastSorted = sortedList.zpristupniPosledni();

            if (comparator.compare(data, lastSorted) > 0) {
                vlozDoSorted(data);
            } else {
                unsortedList.vlozPosledni(data);
            }
        } catch (NoSuchElementException e) {
            vlozDoSorted(data);
        }
    }

    @Override
    public T odeberMax() {
        if (!sortedList.jePrazdny()) {
            return sortedList.odeberPrvni();
        }
        return null;
    }

    @Override
    public T zpristupniMax() {
        if (!sortedList.jePrazdny()) {
            return sortedList.zpristupniPrvni();
        }
        return null;
    }

    private void prerenesASortuj() {
        while (!unsortedList.jePrazdny()) {
            T item = unsortedList.odeberPrvni();
            vlozDoSorted(item);
        }
    }

    private void vlozDoSorted(T item) {
        if (sortedList.jePrazdny()) {
            sortedList.vlozPrvni(item);
            return;
        }

        sortedList.zpristupniPrvni();
        boolean inserted = false;
        try {
            do {
                T current = sortedList.zpristupniAktualni();
                if (comparator.compare(item, current) > 0) {
                    sortedList.vlozPredchudce(item);
                    inserted = true;
                    break;
                }
                sortedList.zpristupniNaslednika();
            } while (true);
        } catch (NoSuchElementException e) {
            if (!inserted) {
                sortedList.vlozPosledni(item);
            }
        }
    }

    @Override
    public void zmenPrioritu(T staraData, T novaData) {
        boolean found = odeberKonkretni(staraData);

        if (found) {
            vloz(novaData);
        }
    }

    private boolean odeberKonkretni(T data) {
        if (odeberZeSeznamu(sortedList, data)) return true;
        return odeberZeSeznamu(unsortedList, data);
    }

    private boolean odeberZeSeznamu(IAbstrDoubleList<T> list, T target) {
        if (list.jePrazdny()) return false;

        try {
            list.zpristupniPrvni();
            do {
                if (list.zpristupniAktualni().equals(target)) {
                    list.odeberAktualni();
                    return true;
                }
                list.zpristupniNaslednika();
            } while (true);
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public T select(int k) {
        if (!unsortedList.jePrazdny()) {
            prerenesASortuj();
        }

        if (k < 1 || k > sortedList.size()) return null;

        int counter = 1;
        for (T item : sortedList) {
            if (counter == k) return item;
            counter++;
        }
        return null;
    }

    @Override
    public int rank(T data) {
        if (!unsortedList.jePrazdny()) {
            prerenesASortuj();
        }

        int count = 0;
        for (T item : sortedList) {
            if (comparator.compare(item, data) > 0) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    @Override
    public Iterator<T> iterator() {
        if (!unsortedList.jePrazdny()) {
            prerenesASortuj();
        }
        return sortedList.iterator();
    }
}
