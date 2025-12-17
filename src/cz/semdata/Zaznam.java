package cz.semdata;

import java.util.Objects;

public class Zaznam implements Comparable<Zaznam> {
    private final int id;
    private final String jmeno;
    private int priorita;

    public Zaznam(int id, String jmeno, int priorita) {
        this.id = id;
        this.jmeno = jmeno;
        this.priorita = priorita;
    }


    public Zaznam(int id, String jmeno) {
        this(id, jmeno, 0);
    }

    public int getId() {
        return id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public int getPriorita() {
        return priorita;
    }

    public void setPriorita(int priorita) {
        this.priorita = priorita;
    }

    @Override
    public String toString() {
        return "Zaznam{id=" + id + ", jmeno='" + jmeno + "', priorita=" + priorita + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zaznam)) return false;
        Zaznam zaznam = (Zaznam) o;
        return id == zaznam.id && Objects.equals(jmeno, zaznam.jmeno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jmeno);
    }

    @Override
    public int compareTo(Zaznam o) {

        return Integer.compare(this.id, o.id);
    }
}