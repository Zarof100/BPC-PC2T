import java.util.*;
import java.io.*;
import java.sql.*;

public class Firma implements Serializable {
    private Map<Integer, Zamestnanec> zamestnanci = new HashMap<>();

    public Zamestnanec pridejZamestnance(TypZamestnance typ, String jmeno, String prijmeni, int rok) {
        int id = 0;
        while (zamestnanci.containsKey(id)) {
            id++;
        }

        Zamestnanec z = vytvorZamestnance(typ, id, jmeno, prijmeni, rok);
        zamestnanci.put(id, z);
        return z;
    }

    private Zamestnanec vytvorZamestnance(TypZamestnance typ, int id, String j, String p, int r) {
        return switch (typ) {
            case ANALYTIK -> new DatovyAnalytik(id, j, p, r);
            case SPECIALISTA -> new BezpecnostniSpecialista(id, j, p, r);
        };
    }

    public Zamestnanec getZamestnanec(int id) {
        return zamestnanci.get(id);
    }

    public void pridejSpolupraci(int id1, int id2, UrovenSpoluprace uroven) {
        Zamestnanec z1 = zamestnanci.get(id1);
        Zamestnanec z2 = zamestnanci.get(id2);

        if (z1 == null || z2 == null || id1 == id2) {
            System.out.println("Zadali jste neexistujícího zaměstnance, nebo 2 krát toho samého.");
            return;
        }
        
        if (z1.maSpolupracovnika(id2)) {
        	System.out.println("Zaměstnec (ID: " + z1.getId() + ") a (ID: " + z2.getId() + ") již spolupracují.");
            return;
        }
        
        z1.pridejSpolupraci(z2, uroven);
        z2.pridejSpolupraci(z1, uroven);

        System.out.println("Spolupráce přidána.");
    }

    public void odeberZamestnance(int id) {
        Zamestnanec z = zamestnanci.remove(id);
        if (z == null) return;

        for (Zamestnanec ostatni : zamestnanci.values()) {
            ostatni.getSpolupracovnici().removeIf(
                s -> s.getKolega().getId() == id
            );
        }
    }

    public void vyhledejZamestnance(int id) {
        Zamestnanec z = zamestnanci.get(id);
        if (z != null) z.vypisInfo();
        else System.out.println("Zaměstnanec nenalezen.");
    }

    public void spustDovednost(int id) {
        Zamestnanec z = zamestnanci.get(id);
        if (z != null) z.specialniFunkce();
        else System.out.println("Zaměstnanec nenalezen.");
    }
    

    public void vypisZamestnancePodleSkupin() {
        List<Zamestnanec> analytici = zamestnanci.values().stream()
                .filter(z -> z.getTyp() == TypZamestnance.ANALYTIK)
                .sorted(Comparator.comparing(Zamestnanec::getPrijmeni))
                .toList();

        List<Zamestnanec> specialisti = zamestnanci.values().stream()
                .filter(z -> z.getTyp() == TypZamestnance.SPECIALISTA)
                .sorted(Comparator.comparing(Zamestnanec::getPrijmeni))
                .toList();

        System.out.println("\n--- Datoví analytici ---");
        analytici.forEach(z ->
                System.out.println(z.getPrijmeni() + " " + z.getJmeno() + " (ID: " + z.getId() + ")"));

        System.out.println("\n--- Bezpečnostní specialisté ---");
        specialisti.forEach(z ->
                System.out.println(z.getPrijmeni() + " " + z.getJmeno() + " (ID: " + z.getId() + ")"));
    }

    public void statistiky() {
        int spatna = 0, prumerna = 0, dobra = 0;
        Zamestnanec maxZam = null;
        int maxVazeb = 0;

        for (Zamestnanec z : zamestnanci.values()) {
            int pocet = z.getSpolupracovnici().size();
            if (pocet > maxVazeb) {
                maxVazeb = pocet;
                maxZam = z;
            }
            for (Spoluprace s : z.getSpolupracovnici()) {
                if (z.getId() < s.getKolega().getId()) {
                    switch (s.getUroven()) {
                        case SPATNA -> spatna++;
                        case PRUMERNA -> prumerna++;
                        case DOBRA -> dobra++;
                    }
                }
            }
        }

        System.out.println("\n--- Statistiky ---");
        if (spatna >= prumerna && spatna >= dobra) System.out.println("Převažující kvalita: ŠPATNÁ");
        else if (prumerna >= spatna && prumerna >= dobra) System.out.println("Převažující kvalita: PRŮMĚRNÁ");
        else System.out.println("Převažující kvalita: DOBRÁ");

        if (maxZam != null) {
            System.out.println("Nejvíce vazeb má: "
                    + maxZam.getPrijmeni() + " " + maxZam.getJmeno()
                    + " (ID: " + maxZam.getId() + ")");
        }
    }

    public void vypisPoctySkupin() {
        int analytici = 0, specialisti = 0;
        for (Zamestnanec z : zamestnanci.values()) {
            if (z.getTyp() == TypZamestnance.ANALYTIK) analytici++;
            else specialisti++;
        }
        System.out.println("\n--- Počty zaměstnanců ---");
        System.out.println("Datoví analytici: " + analytici);
        System.out.println("Bezpečnostní specialisté: " + specialisti);
    }

    public void ulozZamestnanceDoSouboru(int id, String soubor) {
        Zamestnanec z = zamestnanci.get(id);
        if (z == null) {
            System.out.println("Nenalezen.");
            return;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(soubor))) {
            oos.writeObject(z);
            System.out.println("Uloženo do souboru.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void nactiZamestnanceZeSouboru(String soubor) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(soubor))) {
            Zamestnanec z = (Zamestnanec) ois.readObject();
            zamestnanci.put(z.getId(), z);
            System.out.println("Načten zaměstnanec ID: " + z.getId());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:firma.db");
    }

    public void vytvorTabulku() {
        String sql = """
            CREATE TABLE IF NOT EXISTS zamestnanci (
                id INTEGER PRIMARY KEY,
                jmeno TEXT,
                prijmeni TEXT,
                rok INTEGER,
                typ TEXT
            );
        """;

        String sql2 = """
            CREATE TABLE IF NOT EXISTS spoluprace (
                id1 INTEGER,
                id2 INTEGER,
                uroven TEXT
            );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            stmt.execute(sql2);
        } catch (SQLException e) {
            System.out.println("DB není dostupná (nevadí).");
        }
    }

    public void nactiZDB() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT * FROM zamestnanci");
            while (rs.next()) {
                int id = rs.getInt("id");
                String jmeno = rs.getString("jmeno");
                String prijmeni = rs.getString("prijmeni");
                int rok = rs.getInt("rok");
                TypZamestnance typ = TypZamestnance.valueOf(rs.getString("typ"));

                Zamestnanec z = vytvorZamestnance(typ, id, jmeno, prijmeni, rok);
                zamestnanci.put(id, z);
            }

            ResultSet rs2 = stmt.executeQuery("SELECT * FROM spoluprace");
            while (rs2.next()) {
                int id1 = rs2.getInt("id1");
                int id2 = rs2.getInt("id2");
                UrovenSpoluprace uroven = UrovenSpoluprace.valueOf(rs2.getString("uroven"));

                Zamestnanec z1 = zamestnanci.get(id1);
                Zamestnanec z2 = zamestnanci.get(id2);

                if (z1 != null && z2 != null) {
                    z1.pridejSpolupraci(z2, uroven);
                    z2.pridejSpolupraci(z1, uroven);
                }
            }

            System.out.println("Načteno z DB.");

        } catch (SQLException e) {
            System.out.println("DB není dostupná – pokračuji bez ní.");
        }
    }

    public void ulozDoDB() {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM zamestnanci");
                stmt.executeUpdate("DELETE FROM spoluprace");
            }

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT OR IGNORE INTO zamestnanci (id, jmeno, prijmeni, rok, typ) VALUES (?, ?, ?, ?, ?)");
            for (Zamestnanec z : zamestnanci.values()) {
                ps.setInt(1, z.getId());
                ps.setString(2, z.getJmeno());
                ps.setString(3, z.getPrijmeni());
                ps.setInt(4, z.getRokNarozeni());
                ps.setString(5, z.getTyp().name());
                ps.addBatch();
            }
            ps.executeBatch();

            PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT OR IGNORE INTO spoluprace (id1, id2, uroven) VALUES (?, ?, ?)");
            for (Zamestnanec z : zamestnanci.values()) {
                for (Spoluprace s : z.getSpolupracovnici()) {
                    if (z.getId() < s.getKolega().getId()) {
                        ps2.setInt(1, z.getId());
                        ps2.setInt(2, s.getKolega().getId());
                        ps2.setString(3, s.getUroven().name());
                        ps2.addBatch();
                    }
                }
            }
            ps2.executeBatch();

            conn.commit();
            System.out.println("Uloženo do DB.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void ulozDoSouboru(String soubor) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(soubor))) {
            oos.writeObject(this);
            System.out.println("Firma uložena do souboru.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Firma nactiZeSouboru(String soubor) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(soubor))) {
            return (Firma) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Soubor nenalezen, vytvářím novou firmu.");
            return new Firma();
        }
    }
    public boolean jePrazdna() {
        return zamestnanci.isEmpty();
    }
}

