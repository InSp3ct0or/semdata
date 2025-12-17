package cz.semdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PerformanceTest {

    private static final int REPLICATION_COUNT = 1000;
    private static final int INITIAL_SIZE = 10000;
    private static final int MAX_PRIORITY = 10000;

    private static class TestResult {
        String implName;
        long vlozTime;
        long najdiTime;
        long odeberTime;

        TestResult(String name) { this.implName = name; }

        @Override
        public String toString() {
            return String.format("| %-30s | %-15d | %-15d | %-15d |",
                    implName, vlozTime, najdiTime, odeberTime);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Semestrální Práce D: Analýza časové výkonnosti ADS Tabulka ===");
        System.out.println("Počet replikací: " + REPLICATION_COUNT + "x pro každou operaci.");
        System.out.println("Počáteční velikost tabulek: " + INITIAL_SIZE + " prvků.");

        List<PrvekTabulky<String, Zaznam>> initialData = generateTestData(INITIAL_SIZE, 0, MAX_PRIORITY);
        List<PrvekTabulky<String, Zaznam>> insertData = generateTestData(REPLICATION_COUNT, INITIAL_SIZE, MAX_PRIORITY);

        List<String> insertKeys = insertData.stream().map(PrvekTabulky::getKey).toList();

        List<String> findKeys = initialData.stream().map(PrvekTabulky::getKey).collect(Collectors.toCollection(ArrayList::new));
        List<String> deleteFromMiddleKeys = new ArrayList<>(findKeys);
        Collections.shuffle(deleteFromMiddleKeys);

        List<TestResult> results = new ArrayList<>();

        results.add(runTests(new AbstrTable<>(), "AbstrTableBVS", initialData, insertData, deleteFromMiddleKeys, findKeys));

        results.add(runTests(new AbstrTableUnSortedList<>(), "AbstrTableUnSortedList", initialData, insertData, deleteFromMiddleKeys, findKeys));

        results.add(runTests(new AbstrTableArray<>(), "AbstrTableArray", initialData, insertData, deleteFromMiddleKeys, findKeys));

        AbstrTableSortedList<String, Zaznam> sortedListTable = new AbstrTableSortedList<>();
        results.add(runTests(sortedListTable, "AbstrTableSortedList (SEQ)", initialData, insertData, deleteFromMiddleKeys, findKeys));

        TestResult sortedBinResult = runTestsBinary(sortedListTable, "AbstrTableSortedList (BIN)", initialData, insertData, deleteFromMiddleKeys, findKeys);
        TestResult seqResult = results.stream().filter(r -> r.implName.contains("(SEQ)")).findFirst().get();
        sortedBinResult.vlozTime = seqResult.vlozTime;
        sortedBinResult.odeberTime = seqResult.odeberTime;
        results.add(sortedBinResult);

        printResults(results);
    }

    private static List<PrvekTabulky<String, Zaznam>> generateTestData(int count, int idStart, int maxPrio) {
        List<PrvekTabulky<String, Zaznam>> data = new ArrayList<>(count);
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int id = idStart + i + 1;
            String key = "key_" + id + "_" + rand.nextInt(100000);
            Zaznam zaznam = new Zaznam(id, "Zaznam_" + id, rand.nextInt(maxPrio));
            data.add(new PrvekTabulky<>(key, zaznam));
        }
        return data;
    }

    private static TestResult runTests(IAbstrTable<String, Zaznam> table, String name,
                                       List<PrvekTabulky<String, Zaznam>> initialData,
                                       List<PrvekTabulky<String, Zaznam>> insertData,
                                       List<String> deleteFromMiddleKeys,
                                       List<String> findKeys) {
        TestResult result = new TestResult(name);

        for (PrvekTabulky<String, Zaznam> entry : initialData) {
            table.vloz(entry.getKey(), entry.getValue());
        }

        long startNajdi = System.nanoTime();
        for (int i = 0; i < REPLICATION_COUNT; i++) {
            table.najdi(findKeys.get(i * (INITIAL_SIZE / REPLICATION_COUNT) % INITIAL_SIZE));
        }
        result.najdiTime = (System.nanoTime() - startNajdi) / REPLICATION_COUNT;

        long startVloz = System.nanoTime();
        for (int i = 0; i < REPLICATION_COUNT; i++) {
            PrvekTabulky<String, Zaznam> entry = insertData.get(i);
            table.vloz(entry.getKey(), entry.getValue());
        }
        result.vlozTime = (System.nanoTime() - startVloz) / REPLICATION_COUNT;

        long startOdeber = System.nanoTime();
        for (int i = 0; i < REPLICATION_COUNT; i++) {
            table.odeber(deleteFromMiddleKeys.get(i));
        }
        result.odeberTime = (System.nanoTime() - startOdeber) / REPLICATION_COUNT;

        table.zrus();
        return result;
    }

    private static TestResult runTestsBinary(AbstrTableSortedList<String, Zaznam> table, String name,
                                             List<PrvekTabulky<String, Zaznam>> initialData,
                                             List<PrvekTabulky<String, Zaznam>> insertData,
                                             List<String> deleteFromMiddleKeys,
                                             List<String> findKeys) {
        TestResult result = new TestResult(name);

        for (PrvekTabulky<String, Zaznam> entry : initialData) {
            table.vloz(entry.getKey(), entry.getValue());
        }

        long startNajdi = System.nanoTime();
        for (int i = 0; i < REPLICATION_COUNT; i++) {
            table.najdiBinary(findKeys.get(i * (INITIAL_SIZE / REPLICATION_COUNT) % INITIAL_SIZE));
        }
        result.najdiTime = (System.nanoTime() - startNajdi) / REPLICATION_COUNT;

        table.zrus();
        return result;
    }

    private static void printResults(List<TestResult> results) {
        System.out.println("\n--- Výsledky Časové Výkonnosti (ns/operaci, průměr z " + REPLICATION_COUNT + " opakování) ---");
        System.out.println("------------------------------------------------------------------------------------------------");
        System.out.println(String.format("| %-30s | %-15s | %-15s | %-15s |",
                "Implementace", "Vlož (ns)", "Najdi (ns)", "Odeber (ns)"));
        System.out.println("------------------------------------------------------------------------------------------------");
        for (TestResult r : results) {
            System.out.println(r);
        }
        System.out.println("------------------------------------------------------------------------------------------------");

        System.out.println("\n*** Analýza pro manuální vytvoření grafů v Excelu ***");
        System.out.println("Zde jsou data pro váš graf (Zkopírujte tyto řádky):");

        System.out.println("\nGraf VLOŽ:");
        for (TestResult r : results) {
            System.out.println(r.implName + "\t" + r.vlozTime);
        }

        System.out.println("\nGraf NAJDI:");
        for (TestResult r : results) {
            System.out.println(r.implName + "\t" + r.najdiTime);
        }

        System.out.println("\nGraf ODEBER:");
        for (TestResult r : results) {
            System.out.println(r.implName + "\t" + r.odeberTime);
        }
    }
}
