package cz.semdata;

import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ProgData {
    private final SpravaOblasti sprava = new SpravaOblasti();
    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        ProgData app = new ProgData();
        app.run();
    }

    private void run() {
        System.out.println("=== ProgData: sprava oblasti a zaznamu ===");
        boolean running = true;
        while (running) {
            printMenu();
            String cmd = sc.nextLine().trim();
            try {
                switch (cmd) {
                    case "1" -> cmdInit();
                    case "2" -> cmdVlozKlavesnice();
                    case "3" -> cmdVlozZeSouboru();
                    case "4" -> cmdGenerator();
                    case "5" -> cmdZobraz();
                    case "6" -> cmdUlozDoSouboru();
                    case "7" -> cmdOdeberZaznam();
                    case "8" -> cmdOdeberOblast();
                    case "9" -> cmdZrusVse();
                    case "0" -> { running = false; System.out.println("Konec."); }
                    default -> System.out.println("Neplatná volba.");
                }
            } catch (Exception e) {
                System.out.println("Chyba: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\nVyber akci:");
        System.out.println("1) init (vytvorit prvni oblast) ");
        System.out.println("2) vlozit zaznam (z klavesnice) ");
        System.out.println("3) automat ze souboru (vlozit sekvenci) ");
        System.out.println("4) generator (vlozit N nahodnych zaznamu) ");
        System.out.println("5) zobraz stav (oblastí a záznamů) ");
        System.out.println("6) ulozit stav do souboru ");
        System.out.println("7) odebrat zaznam (z aktualni oblasti) ");
        System.out.println("8) odebrat oblast (prvni/posledni/aktualni) ");
        System.out.println("9) zrus vse ");
        System.out.println("0) konec ");
        System.out.print("Volba: ");
    }

    private void cmdInit() {
        System.out.print("Zadej max kapacitu oblasti (cislo >0): ");
        int k = Integer.parseInt(sc.nextLine().trim());
        sprava.init(k);
        System.out.println("Inicializováno s kapacitou " + k + " na oblast.");
    }

    private void cmdVlozKlavesnice() {
        System.out.print("ID zaznamu (int): ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Jmeno: ");
        String jmeno = sc.nextLine().trim();
        Zaznam z = new Zaznam(id, jmeno);
        sprava.vlozZaznam(z);
        System.out.println("Vloženo (first-fit).");
    }

    private void cmdVlozZeSouboru() {
        System.out.print("Cesta k souboru (format id;jmeno na radek): ");
        String path = sc.nextLine().trim();
        try {
            sprava.automat(path);
            System.out.println("Soubor zpracovan.");
        } catch (IOException e) {
            System.out.println("Chyba pri cteni: " + e.getMessage());
        }
    }

    private void cmdGenerator() {
        System.out.print("Kolik zaznamu vygenerovat? ");
        int n = Integer.parseInt(sc.nextLine().trim());
        int baseId = 1000;
        for (int i = 0; i < n; i++) {
            Zaznam z = new Zaznam(baseId + i, "gen_" + (baseId + i));
            sprava.vlozZaznam(z);
        }
        System.out.println("Vygenerováno " + n + " záznamů.");
    }

    private void cmdZobraz() {
        System.out.println("=== Stav oblastí ===");
        System.out.println(sprava.dumpState());
    }

    private void cmdUlozDoSouboru() {
        System.out.print("Cesta kam ulozit stav: ");
        String path = sc.nextLine().trim();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            bw.write(sprava.dumpState());
            System.out.println("Uloženo do " + path);
        } catch (IOException e) {
            System.out.println("Chyba pri zapisu: " + e.getMessage());
        }
    }

    private void cmdOdeberZaznam() {
        System.out.println("Odebrat záznam z: 1=PRVNI,2=POSLEDNI,3=AKTUALNI,4=NASLEDNIK,5=PREDCHUDCE");
        String s = sc.nextLine().trim();
        EnumPozice p = switch (s) {
            case "1" -> EnumPozice.PRVNI;
            case "2" -> EnumPozice.POSLEDNI;
            case "3" -> EnumPozice.AKTUALNI;
            case "4" -> EnumPozice.NASLEDNIK;
            case "5" -> EnumPozice.PREDCHUDCE;
            default -> null;
        };
        if (p == null) { System.out.println("Neplatna volba."); return; }
        Oblast aktualni;
        try {
            aktualni = sprava.zpristupniOblast(EnumPozice.AKTUALNI);
        } catch (Exception e) {
            System.out.println("Nelze získat aktuální oblast: " + e.getMessage());
            return;
        }
        Zaznam removed = aktualni.odeberZaznam(p);
        System.out.println("Odebrano: " + removed);
        if (aktualni.getAktKapacita() == 0) {
            System.out.println("Aktuální oblast (ID=" + aktualni.getId() + ") je prázdná a bude odstraněna.");
            try {
                sprava.odeberOblast(EnumPozice.AKTUALNI);
            } catch (Exception e) {
                System.out.println("Nelze odebrat oblast: " + e.getMessage());
            }
        }
    }

    private void cmdOdeberOblast() {
        System.out.println("Odebrat oblast: 1=PRVNI,2=POSLEDNI,3=AKTUALNI");
        String s = sc.nextLine().trim();
        EnumPozice p = switch (s) {
            case "1" -> EnumPozice.PRVNI;
            case "2" -> EnumPozice.POSLEDNI;
            case "3" -> EnumPozice.AKTUALNI;
            default -> null;
        };
        if (p == null) { System.out.println("Neplatna volba."); return; }
        try {
            Oblast removed = sprava.odeberOblast(p);
            System.out.println("Odebrana oblast: " + removed);
        } catch (Exception e) {
            System.out.println("Chyba pri odebirani oblasti: " + e.getMessage());
        }
    }

    private void cmdZrusVse() {
        sprava.zrus();
        System.out.println("Vse zruseno.");
    }
}
