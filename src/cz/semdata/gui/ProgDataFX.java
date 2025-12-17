package cz.semdata.gui;

import cz.semdata.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Iterator;
import java.util.Random;

public class ProgDataFX extends Application {

    private final SpravaOblasti spravaOblasti = new SpravaOblasti();
    private final SpravaPrioritniFronty spravaFronty = new SpravaPrioritniFronty();

    private TextArea logArea;
    private Random rand = new Random();

    private TextField tfIdB, tfNameB, tfKapB, tfParamB;

    private TextField tfCountC, tfIdC, tfNameC, tfPrioC, tfArgC, tfNewPrioC;

    private Label lblCardId, lblCardName, lblCardPrio, lblCardStatus;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Semestrální práce - Datové struktury (Sem B + Sem C)");

        BorderPane root = new BorderPane();

        TabPane tabPane = new TabPane();

        Tab tabB = new Tab("Sem B: Oblasti & Strom", createSemBContent(primaryStage));
        tabB.setClosable(false);

        Tab tabC = new Tab("Sem C: Prioritní Fronta", createSemCContent(primaryStage));
        tabC.setClosable(false);

        tabPane.getTabs().addAll(tabC, tabB);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(200);
        logArea.setFont(javafx.scene.text.Font.font("Monospaced", 12));

        root.setCenter(tabPane);
        root.setBottom(logArea);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        log("Aplikace spuštěna. Vyberte záložku.");
    }

    private VBox createSemBContent(Stage stage) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f4f4f4;");

        Label lblInit = new Label("Inicializace (Oblasti):");
        tfKapB = new TextField("5"); tfKapB.setPromptText("Max kapacita");
        Button btnInit = new Button("Init");
        btnInit.setOnAction(e -> actionInitB());

        Label lblInsert = new Label("Vložení Záznamu (B):");
        tfIdB = new TextField(); tfIdB.setPromptText("ID");
        tfNameB = new TextField(); tfNameB.setPromptText("Jméno (Klíč)");
        Button btnAdd = new Button("Vložit");
        btnAdd.setOnAction(e -> actionAddB());
        Button btnGen = new Button("Generovat N");
        btnGen.setOnAction(e -> actionGenB());

        Label lblTable = new Label("Operace Tabulky (BVS):");
        tfParamB = new TextField(); tfParamB.setPromptText("Parametr (Klíč/k)");

        HBox ops1 = new HBox(5);
        Button btnFind = new Button("Najdi (Klíč)");
        btnFind.setOnAction(e -> actionFindB());
        Button btnRemove = new Button("Odeber (Klíč)");
        btnRemove.setOnAction(e -> actionRemoveKeyB());
        ops1.getChildren().addAll(btnFind, btnRemove);

        HBox ops2 = new HBox(5);
        Button btnSelect = new Button("Select (k)");
        btnSelect.setOnAction(e -> actionSelectB());
        Button btnRank = new Button("Rank (Klíč)");
        btnRank.setOnAction(e -> actionRankB());
        ops2.getChildren().addAll(btnSelect, btnRank);

        Label lblShow = new Label("Výpisy:");
        ComboBox<String> cbIter = new ComboBox<>();
        cbIter.getItems().addAll("Oblastí (Listy)", "Strom (In-Order)", "Strom (BFS)");
        cbIter.getSelectionModel().selectFirst();
        Button btnShow = new Button("Zobrazit");
        btnShow.setOnAction(e -> actionShowB(cbIter.getValue()));

        HBox files = new HBox(5);
        Button btnLoad = new Button("Načíst");
        btnLoad.setOnAction(e -> actionLoadB(stage));
        Button btnSave = new Button("Uložit");
        btnSave.setOnAction(e -> actionSaveB(stage));
        files.getChildren().addAll(btnLoad, btnSave);

        Button btnClear = new Button("Zrušit vše (Sem B)");
        btnClear.setOnAction(e -> { spravaOblasti.zrus(); log("Sem B: Vše zrušeno."); });

        panel.getChildren().addAll(
                lblInit, new HBox(5, tfKapB, btnInit), new Separator(),
                lblInsert, new HBox(5, tfIdB, tfNameB), new HBox(5, btnAdd, btnGen), new Separator(),
                lblTable, tfParamB, ops1, ops2, new Separator(),
                lblShow, new HBox(5, cbIter, btnShow), new Separator(),
                files, btnClear
        );
        return panel;
    }

    private void actionInitB() {
        try {
            int k = Integer.parseInt(tfKapB.getText());
            spravaOblasti.init(k);
            log("Sem B: Inicializováno. Kapacita: " + k);
        } catch (Exception e) { log("Chyba: " + e.getMessage()); }
    }

    private void actionAddB() {
        try {
            int id = Integer.parseInt(tfIdB.getText());
            String name = tfNameB.getText();
            if (name.isEmpty()) throw new IllegalArgumentException("Jméno nesmí být prázdné");
            spravaOblasti.vlozZaznam(new Zaznam(id, name));
            log("Sem B: Vloženo: " + id + ", " + name);
            tfIdB.clear(); tfNameB.clear();
        } catch (Exception e) { log("Chyba vložení: " + e.getMessage()); }
    }

    private void actionGenB() {
        try {
            int n = Integer.parseInt(tfParamB.getText());
            int startId = 1000 + rand.nextInt(1000);
            for (int i = 0; i < n; i++) {
                int id = startId + i;
                String name = "z_" + id + "_" + (char)('A' + rand.nextInt(26));
                spravaOblasti.vlozZaznam(new Zaznam(id, name));
            }
            log("Sem B: Vygenerováno " + n + " záznamů.");
        } catch (Exception e) { log("Chyba: Zadejte počet do 'Parametr'."); }
    }

    private void actionFindB() {
        String key = tfParamB.getText();
        Zaznam z = spravaOblasti.getTabulka().najdi(key);
        log(z != null ? "Nalezeno: " + z : "Nenalezeno: " + key);
    }

    private void actionRemoveKeyB() {
        String key = tfParamB.getText();
        Zaznam z = spravaOblasti.getTabulka().najdi(key);
        if (z != null) {
            spravaOblasti.odeberZaznam(z);
            log("Odebráno: " + z);
        } else {
            log("Nenalezeno pro odebrání: " + key);
        }
    }

    private void actionSelectB() {
        try {
            int k = Integer.parseInt(tfParamB.getText());
            Zaznam z = spravaOblasti.getTabulka().select(k);
            log("Select(" + k + "): " + z);
        } catch (Exception e) { log("Chyba: " + e.getMessage()); }
    }

    private void actionRankB() {
        String key = tfParamB.getText();
        int r = spravaOblasti.getTabulka().rank(key);
        log("Rank(" + key + "): " + r);
    }

    private void actionShowB(String mode) {
        logArea.clear();
        if (mode.startsWith("Oblastí")) {
            logArea.setText(spravaOblasti.dumpState());
        } else if (mode.contains("Strom")) {
            eTypProhl typ = mode.contains("BFS") ? eTypProhl.SIRKA : eTypProhl.HLOUBKA;
            StringBuilder sb = new StringBuilder("=== Výpis Stromu (" + typ + ") ===\n");
            Iterator<Zaznam> it = spravaOblasti.getTabulka().vytvorIterator(typ);
            while (it.hasNext()) sb.append(it.next()).append("\n");
            logArea.setText(sb.toString());
        }
    }

    private void actionLoadB(Stage stage) {
        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(stage);
        if (f != null) {
            try {
                spravaOblasti.automat(f.getAbsolutePath());
                log("Načteno: " + f.getName());
            } catch (Exception e) { log("Chyba: " + e.getMessage()); }
        }
    }

    private void actionSaveB(Stage stage) {
        FileChooser fc = new FileChooser();
        File f = fc.showSaveDialog(stage);
        if (f != null) {
            try {
                spravaOblasti.ulozDoSouboru(f.getAbsolutePath());
                log("Uloženo: " + f.getName());
            } catch (Exception e) { log("Chyba: " + e.getMessage()); }
        }
    }

    private VBox createSemCContent(Stage stage) {
        VBox controlPanel = new VBox(10);
        controlPanel.setPadding(new Insets(10));

        Label lblBuild = new Label("Vybuduj / Generuj:");
        tfCountC = new TextField("10"); tfCountC.setPromptText("Počet prvků");
        Button btnBuild = new Button("Vybuduj (náhodně)");
        btnBuild.setOnAction(e -> actionBuildC());

        Label lblInsert = new Label("Vložení záznamu:");
        tfIdC = new TextField(); tfIdC.setPromptText("ID");
        tfIdC.setPrefWidth(60);
        tfNameC = new TextField(); tfNameC.setPromptText("Jméno");
        tfPrioC = new TextField(); tfPrioC.setPromptText("Priorita (int)");
        tfPrioC.setPrefWidth(80);
        Button btnAdd = new Button("Vložit");
        btnAdd.setOnAction(e -> actionAddC());

        Label lblOps = new Label("Základní operace:");
        HBox opsBox = new HBox(5);
        Button btnDelMax = new Button("Odeber Max");
        btnDelMax.setOnAction(e -> actionDelMaxC());
        Button btnPeek = new Button("Zpřístupni Max (Karta)");
        btnPeek.setOnAction(e -> actionPeekC());
        opsBox.getChildren().addAll(btnDelMax, btnPeek);

        Label lblAdv = new Label("Pokročilé (Select/Rank/Změna):");
        tfArgC = new TextField(); tfArgC.setPromptText("Parametr (k / ID)");
        tfNewPrioC = new TextField(); tfNewPrioC.setPromptText("Nová Prio");

        HBox advBox1 = new HBox(5);
        Button btnSelect = new Button("Select(k)");
        btnSelect.setOnAction(e -> actionSelectC());
        Button btnRank = new Button("Rank(ID)");
        btnRank.setOnAction(e -> actionRankC());
        advBox1.getChildren().addAll(btnSelect, btnRank);

        Button btnChange = new Button("Změň prioritu (ID)");
        btnChange.setOnAction(e -> actionChangePrioC());

        Label lblList = new Label("Výpisy:");
        Button btnList = new Button("Vypsat frontu (Iterátor)");
        btnList.setOnAction(e -> actionListC());
        Button btnClear = new Button("Zrušit Frontu");
        btnClear.setOnAction(e -> { spravaFronty.zrus(); log("Fronta zrušena."); updateZaznamCard(null); actionListC(); });

        controlPanel.getChildren().addAll(
                lblBuild, new HBox(5, tfCountC, btnBuild), new Separator(),
                lblInsert, new HBox(5, tfIdC, tfNameC, tfPrioC, btnAdd), new Separator(),
                lblOps, opsBox, new Separator(),
                lblAdv, new HBox(5, tfArgC, tfNewPrioC), advBox1, btnChange, new Separator(),
                lblList, new HBox(5, btnList, btnClear)
        );

        HBox mainContent = new HBox(10);
        mainContent.setPadding(new Insets(10));
        mainContent.setStyle("-fx-background-color: #eef6ff;");

        VBox zaznamCard = createZaznamCard();
        HBox.setHgrow(controlPanel, Priority.ALWAYS);

        mainContent.getChildren().addAll(controlPanel, zaznamCard);

        return new VBox(mainContent);
    }

    private VBox createZaznamCard() {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        card.setStyle("-fx-border-color: #3f60b5; -fx-border-width: 2; -fx-background-color: #ffffff; -fx-border-radius: 5;");

        Label title = new Label("AKTIVNÍ ZÁZNAM");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #3f60b5;");

        lblCardId = new Label("ID: --");
        lblCardName = new Label("Jméno: --");
        lblCardPrio = new Label("Priorita: --");
        lblCardStatus = new Label("Stav: Prázdná fronta / Zobrazte MAX");
        lblCardStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #c94444;");

        VBox dataBox = new VBox(5);
        dataBox.setPadding(new Insets(5, 0, 5, 0));
        dataBox.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1 0 1 0;");
        dataBox.getChildren().addAll(lblCardId, lblCardName, lblCardPrio);

        card.getChildren().addAll(title, new Separator(), dataBox, lblCardStatus);

        return card;
    }

    private void updateZaznamCard(Zaznam z) {
        if (z == null) {
            lblCardId.setText("ID: --");
            lblCardName.setText("Jméno: --");
            lblCardPrio.setText("Priorita: --");
            lblCardStatus.setText("Stav: Fronta je prázdná.");
            lblCardStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #c94444;");
        } else {
            lblCardId.setText("ID: " + z.getId());
            lblCardName.setText("Jméno: " + z.getJmeno());
            lblCardPrio.setText("Priorita: " + z.getPriorita());

            if (lblCardStatus.getText().startsWith("Select")) {
                lblCardStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a7e4b;");
            } else {
                lblCardStatus.setText("Stav: Aktuální Max prvek.");
                lblCardStatus.setStyle("-fx-font-weight: bold; -fx-text-fill: #3f60b5;");
            }
        }
    }

    private void actionBuildC() {
        try {
            int n = Integer.parseInt(tfCountC.getText());
            spravaFronty.vybuduj(n);
            log("Sem C: Vybudována fronta (" + n + " prvků).");
            updateZaznamCard(null);
            actionListC();
        } catch (Exception e) { log("Chyba: " + e.getMessage()); }
    }

    private void actionAddC() {
        try {
            int id = Integer.parseInt(tfIdC.getText());
            String name = tfNameC.getText();
            int prio = Integer.parseInt(tfPrioC.getText());
            spravaFronty.vlozZaznam(id, name, prio);
            log("Sem C: Vloženo [" + id + ", " + name + ", p=" + prio + "]");
            updateZaznamCard(null);
            actionListC();
            tfIdC.clear(); tfNameC.clear(); tfPrioC.clear();
        } catch (Exception e) { log("Chyba vložení: " + e.getMessage()); }
    }

    private void actionDelMaxC() {
        try {
            Zaznam z = spravaFronty.odeberMax();
            log("Sem C: Odebráno MAX: " + (z == null ? "Fronta je prázdná" : z));
            updateZaznamCard(null);
            actionListC();
        } catch (Exception e) { log("Chyba: " + e.getMessage()); }
    }

    private void actionPeekC() {
        Zaznam z = spravaFronty.zpristupniMax();
        updateZaznamCard(z);
        log("Sem C: Zpřístupněn MAX: " + (z == null ? "Fronta je prázdná" : z));
    }

    private void actionSelectC() {
        try {
            int k = Integer.parseInt(tfArgC.getText());
            Zaznam z = spravaFronty.select(k);
            if (z != null) {
                lblCardStatus.setText("Select(" + k + ") - K-tý prvek.");
                updateZaznamCard(z);
                log("Sem C: Select(" + k + ") -> " + z);
            } else {
                updateZaznamCard(null);
                log("Sem C: Select(" + k + ") -> Mimo rozsah");
            }
        } catch (Exception e) { log("Chyba: Parametr musí být číslo."); }
    }

    private void actionRankC() {
        try {
            int id = Integer.parseInt(tfArgC.getText());
            int r = spravaFronty.rank(id);
            if (r == -1) log("Sem C: Záznam s ID " + id + " nenalezen.");
            else log("Sem C: Rank(ID=" + id + ") = " + r + " (počet prvků s vyšší prioritou)");
        } catch (Exception e) { log("Chyba: Parametr ID musí být číslo."); }
    }

    private void actionChangePrioC() {
        try {
            int id = Integer.parseInt(tfArgC.getText());
            int novaPrio = Integer.parseInt(tfNewPrioC.getText());
            spravaFronty.zmenPrioritu(id, novaPrio);
            log("Sem C: Priorita záznamu ID " + id + " změněna na " + novaPrio + ".");
            updateZaznamCard(null);
            actionListC();
        } catch (Exception e) { log("Chyba: " + e.getMessage()); }
    }

    private void actionListC() {
        logArea.setText(spravaFronty.dump());
    }

    private void log(String msg) {
        logArea.appendText(msg + "\n");
    }
}
