package cz.semdata;

import java.util.Iterator;

public class AbstrFIFO<T> implements Iterable<T> {
    private final IAbstrDoubleList<T> list = new AbstrDoubleList<>();

    public void zrus() { list.zrus(); }
    public boolean jePrazdny() { return list.jePrazdny(); }

    public void vloz(T data) {
        list.vlozPosledni(data);
    }

    public T odeber() {
        return list.odeberPrvni();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}