package cz.semdata;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class AbstrDoubleList<T> implements IAbstrDoubleList<T> {

    private class Node {
        T data;
        Node next;
        Node prev;
        Node(T data) { this.data = data; }
    }

    private Node first;
    private Node last;
    private Node current;
    private int size;
  

    public AbstrDoubleList() {
        first = last = current = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    @Override
    public void zrus() {
        Node n = first;
        while (n != null) {
            Node next = n.next;
            n.prev = null;
            n.next = null;
            n.data = null;
            n = next;
        }
        first = last = current = null;
        size = 0;
    }

    @Override
    public boolean jePrazdny() {
        return size == 0;
    }

    @Override
    public void vlozPrvni(T data) {
        Node n = new Node(data);
        if (jePrazdny()) {
            first = last = current = n;
        } else {
            n.next = first;
            first.prev = n;
            first = n;
        }
        current = n;
        size++;
    }

    @Override
    public void vlozPosledni(T data) {
        Node n = new Node(data);
        if (jePrazdny()) {
            first = last = current = n;
        } else {
            last.next = n;
            n.prev = last;
            last = n;
            current = n;
        }
        size++;
    }

    @Override
    public void vlozNaslednika(T data) {
        if (jePrazdny()) {
            vlozPrvni(data);
            return;
        }
        Node n = new Node(data);
        Node c = current;
        Node succ = c.next;
        c.next = n;
        n.prev = c;
        n.next = succ;
        if (succ != null) succ.prev = n;
        else last = n;
        current = n;
        size++;
    }

    @Override
    public void vlozPredchudce(T data) {
        if (jePrazdny()) {
            vlozPrvni(data);
            return;
        }
        Node n = new Node(data);
        Node c = current;
        Node pred = c.prev;
        n.next = c;
        c.prev = n;
        n.prev = pred;
        if (pred != null) pred.next = n;
        else first = n;
        current = n;
        size++;
    }

    @Override
    public T zpristupniAktualni() {
        if (jePrazdny() || current == null) throw new NoSuchElementException("Aktualni prvek neexistuje.");
        return current.data;
    }

    @Override
    public T zpristupniPrvni() {
        if (jePrazdny()) throw new NoSuchElementException("Seznam je prazdny.");
        current = first;
        return first.data;
    }

    @Override
    public T zpristupniPosledni() {
        if (jePrazdny()) throw new NoSuchElementException("Seznam je prazdny.");
        current = last;
        return last.data;
    }

    @Override
    public T zpristupniNaslednika() {
        if (jePrazdny() || current == null || current.next == null) throw new NoSuchElementException("Naslednik neexistuje.");
        current = current.next;
        return current.data;
    }

    @Override
    public T zpristupniPredchudce() {
        if (jePrazdny() || current == null || current.prev == null) throw new NoSuchElementException("Predchudce neexistuje.");
        current = current.prev;
        return current.data;
    }

    @Override
    public T odeberAktualni() {
        if (jePrazdny() || current == null) throw new NoSuchElementException("Aktualni nelze odebrat.");
        Node toRemove = current;
        T data = toRemove.data;

        Node p = toRemove.prev;
        Node n = toRemove.next;

        if (p != null) p.next = n; else first = n;
        if (n != null) n.prev = p; else last = p;

        current = first;
        size--;
        return data;
    }

    @Override
    public T odeberPrvni() {
        if (jePrazdny()) throw new NoSuchElementException("Seznam je prazdny.");
        Node toRemove = first;
        T data = toRemove.data;
        first = first.next;
        if (first != null) first.prev = null; else last = null;
        current = first;
        size--;
        return data;
    }

    @Override
    public T odeberPosledni() {
        if (jePrazdny()) throw new NoSuchElementException("Seznam je prazdny.");
        Node toRemove = last;
        T data = toRemove.data;
        last = last.prev;
        if (last != null) last.next = null; else first = null;
        current = first;
        size--;
        return data;
    }

    @Override
    public T odeberNaslednika() {
        if (jePrazdny() || current == null || current.next == null) throw new NoSuchElementException("Naslednik neexistuje.");
        Node toRemove = current.next;
        T data = toRemove.data;
        Node n = toRemove.next;
        current.next = n;
        if (n != null) n.prev = current; else last = current;
        current = first;
        size--;
        return data;
    }

    @Override
    public T odeberPredchudce() {
        if (jePrazdny() || current == null || current.prev == null) throw new NoSuchElementException("Predchudce neexistuje.");
        Node toRemove = current.prev;
        T data = toRemove.data;
        Node p = toRemove.prev;
        current.prev = p;
        if (p != null) p.next = current; else first = current;
        current = first;
        size--;
        return data;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Node iter = first;

            @Override
            public boolean hasNext() {
                return iter != null;
            }

            @Override
            public T next() {
                if (iter == null) throw new NoSuchElementException();
                T d = iter.data;
                iter = iter.next;
                return d;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node n = first;
        boolean firstElem = true;
        while (n != null) {
            if (!firstElem) sb.append(", ");
            sb.append(n.data);
            firstElem = false;
            n = n.next;
        }
        sb.append("]");
        return sb.toString();
    }


}
