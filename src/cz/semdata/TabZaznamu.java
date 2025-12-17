package cz.semdata;

import java.util.Iterator;

public class TabZaznamu {
    private final IAbstrTable<String, Zaznam> table;

    public TabZaznamu() {
        this.table = new AbstrTable<>();
    }

    public void vloz(Zaznam z) {
        if (z != null) {
            table.vloz(z.getJmeno(), z);
        }
    }

    public Zaznam najdi(String jmeno) {
        return table.najdi(jmeno);
    }

    public Zaznam odeber(String jmeno) {
        return table.odeber(jmeno);
    }

    public Iterator<Zaznam> vytvorIterator(eTypProhl typ) {
        return table.vytvorIterator(typ);
    }

    public Zaznam select(int k) {
        return table.select(k);
    }

    public int rank(String jmeno) {
        return table.rank(jmeno);
    }

    public void zrus() {
        table.zrus();
    }

    public boolean jePrazdna() {
        return table.jePrazdny();
    }
}