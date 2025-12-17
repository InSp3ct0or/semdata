package cz.semdata;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class AbstrTable<K extends Comparable<K>, V> implements IAbstrTable<K, V> {

    private class Node {
        K key;
        V value;
        Node left, right;
        int size;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.size = 1;
        }
    }

    private Node root;

    @Override
    public void zrus() {
        root = null;
    }

    @Override
    public boolean jePrazdny() {
        return root == null;
    }

    private int getSize(Node n) {
        return n == null ? 0 : n.size;
    }

    private void updateSize(Node n) {
        if (n != null) {
            n.size = 1 + getSize(n.left) + getSize(n.right);
        }
    }

    @Override
    public V najdi(K key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current.value;
            else if (cmp < 0) current = current.left;
            else current = current.right;
        }
        return null;
    }

    @Override
    public void vloz(K key, V value) {
        root = vlozRec(root, key, value);
    }

    private Node vlozRec(Node n, K key, V value) {
        if (n == null) return new Node(key, value);

        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            n.left = vlozRec(n.left, key, value);
        } else if (cmp > 0) {
            n.right = vlozRec(n.right, key, value);
        } else {
            n.value = value;
        }
        updateSize(n);
        return n;
    }

    @Override
    public V odeber(K key) {
        Node toRemove = findNode(root, key);
        if (toRemove == null) return null;
        V oldValue = toRemove.value;
        root = odeberRec(root, key);
        return oldValue;
    }

    private Node findNode(Node n, K key) {
        if (n == null) return null;
        int cmp = key.compareTo(n.key);
        if (cmp == 0) return n;
        return cmp < 0 ? findNode(n.left, key) : findNode(n.right, key);
    }

    private Node odeberRec(Node n, K key) {
        if (n == null) return null;

        int cmp = key.compareTo(n.key);
        if (cmp < 0) {
            n.left = odeberRec(n.left, key);
        } else if (cmp > 0) {
            n.right = odeberRec(n.right, key);
        } else {
            if (n.left == null) return n.right;
            if (n.right == null) return n.left;

            Node minRight = minNode(n.right);
            n.key = minRight.key;
            n.value = minRight.value;
            n.right = odeberRec(n.right, minRight.key);
        }
        updateSize(n);
        return n;
    }

    private Node minNode(Node n) {
        while (n.left != null) n = n.left;
        return n;
    }

    @Override
    public V select(int k) {
        if (k < 1 || k > getSize(root)) return null;
        return selectRec(root, k);
    }

    private V selectRec(Node n, int k) {
        if (n == null) return null;
        int t = getSize(n.left);

        if (t + 1 == k) return n.value;
        else if (k <= t) return selectRec(n.left, k);
        else return selectRec(n.right, k - (t + 1));
    }

    @Override
    public int rank(K key) {
        return rankRec(root, key);
    }

    private int rankRec(Node n, K key) {
        if (n == null) return 0;
        int cmp = key.compareTo(n.key);

        if (cmp == 0) return getSize(n.left) + 1;
        else if (cmp < 0) return rankRec(n.left, key);
        else {
            int rightRank = rankRec(n.right, key);
            return (rightRank == 0) ? 0 : getSize(n.left) + 1 + rightRank;
        }
    }

    @Override
    public Iterator<V> iterator() {
        return vytvorIterator(eTypProhl.HLOUBKA);
    }

    @Override
    public Iterator<V> vytvorIterator(eTypProhl typ) {
        switch (typ) {
            case SIRKA: return new BFSIterator();
            case HLOUBKA: return new DFSIterator();
            default: throw new IllegalArgumentException();
        }
    }

    private class BFSIterator implements Iterator<V> {
        private final AbstrFIFO<Node> queue = new AbstrFIFO<>();

        public BFSIterator() {
            if (root != null) queue.vloz(root);
        }

        @Override
        public boolean hasNext() {
            return !queue.jePrazdny();
        }

        @Override
        public V next() {
            if (!hasNext()) throw new NoSuchElementException();
            Node n = queue.odeber();
            if (n.left != null) queue.vloz(n.left);
            if (n.right != null) queue.vloz(n.right);
            return n.value;
        }
    }

    private class DFSIterator implements Iterator<V> {
        private final AbstrLIFO<Node> stack = new AbstrLIFO<>();
        private Node current = root;

        public DFSIterator() {
            pushLeft(root);
        }

        private void pushLeft(Node n) {
            while (n != null) {
                stack.vloz(n);
                n = n.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !stack.jePrazdny();
        }

        @Override
        public V next() {
            if (!hasNext()) throw new NoSuchElementException();
            Node n = stack.odeber();
            V result = n.value;
            if (n.right != null) pushLeft(n.right);
            return result;
        }
    }
}
