package cz.semdata;

import java.util.Iterator;

public class Oblast implements Iterable<Zaznam> {
    private final int id;
    private final int maxKapacita;
    private final SpravaZaznamu spravaZaznamu;

    public Oblast(int id, int maxKapacita) {
        if (maxKapacita <= 0) throw new IllegalArgumentException("maxKapacita musi byt > 0");
        this.id = id;
        this.maxKapacita = maxKapacita;
        this.spravaZaznamu = new SpravaZaznamu();
    }

    public int getId() {
        return id;
    }

    public int getMaxKapacita() {
        return maxKapacita;
    }

    public int getAktKapacita() {
        return spravaZaznamu.getPocet();
    }

    public boolean jePlna() {
        return getAktKapacita() >= maxKapacita;
    }

    public boolean odeberKonkrektni(Zaznam target) {
        return spravaZaznamu.odeberKonkrektni(target);
    }

    public void vlozZaznam(Zaznam z, EnumPozice pozice) {
        if (jePlna()) {
            throw new IllegalStateException("Oblast " + id + " je plna.");
        }
        spravaZaznamu.vlozZaznam(z, pozice);
    }

    public void vlozZaznam(Zaznam z) {
        vlozZaznam(z, EnumPozice.POSLEDNI);
    }

    public Zaznam zpristupniZaznam(EnumPozice pozice) {
        return spravaZaznamu.zpristupniZaznam(pozice);
    }

    public Zaznam odeberZaznam(EnumPozice pozice) {
        return spravaZaznamu.odeberZaznam(pozice);
    }

    public void zrus() {
        spravaZaznamu.zrus();
    }

    @Override
    public Iterator<Zaznam> iterator() {
        return spravaZaznamu.iterator();
    }

    @Override
    public String toString() {
        return "Oblast{" +
                "id=" + id +
                ", max=" + maxKapacita +
                ", akt=" + getAktKapacita() +
                '}';
    }
}
