package cz.semdata;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SpravaZaznamu implements Iterable<Zaznam> {
    private final IAbstrDoubleList<Zaznam> zaznamy = new AbstrDoubleList<>();

    public int getPocet() {
        if (this.zaznamy instanceof AbstrDoubleList) {
            return ((AbstrDoubleList<Zaznam>) this.zaznamy).size();
        } else {
            int c = 0;
            for (Zaznam z : this.zaznamy) {
                ++c;
            }
            return c;
        }
    }

    public void vlozZaznam(Zaznam zaznam, EnumPozice pozice) {
        switch (pozice) {
            case PRVNI -> this.zaznamy.vlozPrvni(zaznam);
            case POSLEDNI -> this.zaznamy.vlozPosledni(zaznam);
            case NASLEDNIK -> this.zaznamy.vlozNaslednika(zaznam);
            case PREDCHUDCE -> this.zaznamy.vlozPredchudce(zaznam);
            case AKTUALNI -> this.zaznamy.vlozNaslednika(zaznam);
            default -> throw new IllegalArgumentException("Neznama pozice: " + pozice);
        }
    }

    public Zaznam zpristupniZaznam(EnumPozice pozice) {
        try {
            return switch (pozice) {
                case PRVNI -> this.zaznamy.zpristupniPrvni();
                case POSLEDNI -> this.zaznamy.zpristupniPosledni();
                case NASLEDNIK -> this.zaznamy.zpristupniNaslednika();
                case PREDCHUDCE -> this.zaznamy.zpristupniPredchudce();
                case AKTUALNI -> this.zaznamy.zpristupniAktualni();
            };
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Nelze zpřístupnit záznam: " + e.getMessage());
        }
    }

    public Zaznam odeberZaznam(EnumPozice pozice) {
        try {
            return switch (pozice) {
                case PRVNI -> this.zaznamy.odeberPrvni();
                case POSLEDNI -> this.zaznamy.odeberPosledni();
                case NASLEDNIK -> this.zaznamy.odeberNaslednika();
                case PREDCHUDCE -> this.zaznamy.odeberPredchudce();
                case AKTUALNI -> this.zaznamy.odeberAktualni();
            };
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Nelze odebrat záznam: " + e.getMessage());
        }
    }


    public boolean odeberKonkrektni(Zaznam target) {
        if (target == null || this.zaznamy.jePrazdny()) return false;
        try {

            Zaznam akt = this.zaznamy.zpristupniPrvni();
            if (target.equals(akt)) {
                this.zaznamy.odeberAktualni();
                return true;
            }

            while (true) {
                try {
                    akt = this.zaznamy.zpristupniNaslednika();
                    if (target.equals(akt)) {
                        this.zaznamy.odeberAktualni();
                        return true;
                    }
                } catch (NoSuchElementException ne) {
                    break;
                }
            }
        } catch (NoSuchElementException e) {

        }
        return false;
    }


    public boolean obsahuje(Zaznam target) {
        if (target == null || this.zaznamy.jePrazdny()) return false;
        try {
            Zaznam akt = this.zaznamy.zpristupniPrvni();
            if (target.equals(akt)) return true;
            while (true) {
                try {
                    akt = this.zaznamy.zpristupniNaslednika();
                    if (target.equals(akt)) return true;
                } catch (NoSuchElementException ne) {
                    break;
                }
            }
        } catch (NoSuchElementException e) {

        }
        return false;
    }

    public void zrus() {
        this.zaznamy.zrus();
    }

    public Iterator<Zaznam> iterator() {
        return this.zaznamy.iterator();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SpravaZaznamu{pocet=").append(this.getPocet()).append(", [");
        boolean first = true;
        for (Zaznam z : this.zaznamy) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(z);
            first = false;
        }
        sb.append("]}");
        return sb.toString();
    }
}
