package cz.semdata;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class SpravaPrioritniFronty {

    private final IAbstrPriorQueue<Zaznam> queue;
    private final Random rand = new Random();

    public SpravaPrioritniFronty() {
        Comparator<Zaznam> comp = (z1, z2) -> Integer.compare(z1.getPriorita(), z2.getPriorita());
        this.queue = new AbstrPriorQueue<>(comp);
    }

    public void vybuduj(int count) {
        zrus();
        for (int i = 0; i < count; i++) {
            int id = i + 1;
            int prio = rand.nextInt(100) + 1;
            queue.vloz(new Zaznam(id, "P_Zaznam_" + id, prio));
        }
    }

    public void vlozZaznam(int id, String jmeno, int priorita) {
        queue.vloz(new Zaznam(id, jmeno, priorita));
    }

    public Zaznam odeberMax() {
        return queue.odeberMax();
    }

    public Zaznam zpristupniMax() {
        return queue.zpristupniMax();
    }

    public void zmenPrioritu(int id, int novaPriorita) {
        Zaznam staryZaznam = null;

        for (Zaznam z : queue) {
            if (z.getId() == id) {
                staryZaznam = z;
                break;
            }
        }

        if (staryZaznam != null) {
            Zaznam novyZaznam = new Zaznam(staryZaznam.getId(), staryZaznam.getJmeno(), novaPriorita);
            queue.zmenPrioritu(staryZaznam, novyZaznam);
        } else {
            throw new IllegalArgumentException("Zaznam s ID " + id + " nenalezen.");
        }
    }

    public Zaznam select(int k) {
        return queue.select(k);
    }

    public int rank(int id) {
        Zaznam dummyTarget = null;
        for (Zaznam z : queue) {
            if (z.getId() == id) {
                dummyTarget = z;
                break;
            }
        }
        if (dummyTarget == null) return -1;

        return queue.rank(dummyTarget);
    }

    public void zrus() {
        queue.zrus();
    }

    public Iterator<Zaznam> iterator() {
        return queue.iterator();
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("Priority Queue:\n");
        for (Zaznam z : queue) {
            sb.append(z).append("\n");
        }
        return sb.toString();
    }
}
