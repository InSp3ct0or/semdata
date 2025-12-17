package cz.semdata;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

public class SpravaOblasti implements Iterable<Oblast> {
    private final IAbstrDoubleList<Oblast> oblasti;
    private int maxKapacitaOblasti;
    private final AtomicInteger idGeneratorOblasti = new AtomicInteger(1);
    private final TabZaznamu tabulkaZaznamu;

    public SpravaOblasti() {
        oblasti = new AbstrDoubleList<>();
        tabulkaZaznamu = new TabZaznamu();
    }

    public TabZaznamu getTabulka() {
        return tabulkaZaznamu;
    }

    public void init(int maxKapacita) {
        if (maxKapacita <= 0) throw new IllegalArgumentException("maxKapacita musi byt > 0");
        this.maxKapacitaOblasti = maxKapacita;
        zrus();
        Oblast prvni = new Oblast(idGeneratorOblasti.getAndIncrement(), maxKapacitaOblasti);
        oblasti.vlozPrvni(prvni);
    }

    public void vlozZaznam(Zaznam zaznam) {
        tabulkaZaznamu.vloz(zaznam);
        for (Oblast o : oblasti) {
            if (!o.jePlna()) {
                o.vlozZaznam(zaznam);
                return;
            }
        }
        Oblast nova = new Oblast(idGeneratorOblasti.getAndIncrement(), maxKapacitaOblasti);
        nova.vlozZaznam(zaznam);
        oblasti.vlozPosledni(nova);
    }

    public void vlozZaznamPozice(Zaznam zaznam, EnumPozice pozice) {
        tabulkaZaznamu.vloz(zaznam);
        Oblast aktu;
        try {
            aktu = oblasti.zpristupniAktualni();
        } catch (Exception e) {
            aktu = oblasti.zpristupniPrvni();
        }
        if (aktu.jePlna()) {
            throw new OblastPlnaException("Aktuální oblast (ID=" + aktu.getId() + ") je plná.");
        }
        aktu.vlozZaznam(zaznam, pozice);
    }

    public void odeberZaznam(Zaznam zaznam) {
        if (zaznam == null) return;
        tabulkaZaznamu.odeber(zaznam.getJmeno());
        for (Oblast o : oblasti) {
            if (o.odeberKonkrektni(zaznam)) {
                return;
            }
        }
    }

    public Oblast zpristupniOblast(EnumPozice pozice) {
        return switch (pozice) {
            case PRVNI -> oblasti.zpristupniPrvni();
            case POSLEDNI -> oblasti.zpristupniPosledni();
            case NASLEDNIK -> oblasti.zpristupniNaslednika();
            case PREDCHUDCE -> oblasti.zpristupniPredchudce();
            case AKTUALNI -> oblasti.zpristupniAktualni();
        };
    }

    public Oblast odeberOblast(EnumPozice pozice) {
        Oblast o;
        try {
            o = switch (pozice) {
                case PRVNI -> oblasti.odeberPrvni();
                case POSLEDNI -> oblasti.odeberPosledni();
                case NASLEDNIK -> oblasti.odeberNaslednika();
                case PREDCHUDCE -> oblasti.odeberPredchudce();
                case AKTUALNI -> oblasti.odeberAktualni();
            };
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Nelze odebrat oblast: " + e.getMessage());
        }
        for (Zaznam z : o) {
            tabulkaZaznamu.odeber(z.getJmeno());
        }
        return o;
    }

    public void zrus() {
        for (Oblast o : oblasti) {
            o.zrus();
        }
        oblasti.zrus();
        tabulkaZaznamu.zrus();
    }

    @Override
    public Iterator<Oblast> iterator() {
        return oblasti.iterator();
    }

    public void automat(String soubor) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(soubor))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";", 2);
                int id = Integer.parseInt(parts[0].trim());
                String jmeno = (parts.length > 1) ? parts[1].trim() : "zaznam" + id;
                vlozZaznam(new Zaznam(id, jmeno));
            }
        }
    }

    public void ulozDoSouboru(String soubor) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(soubor))) {
            bw.write(dumpState());
        }
    }

    public String dumpState() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Oblasti ===").append(System.lineSeparator());
        for (Oblast o : this) {
            sb.append(o).append(System.lineSeparator());
            for (Zaznam z : o) {
                sb.append("   ").append(z).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }
}
