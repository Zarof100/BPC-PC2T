
import java.util.*;
import java.io.Serializable;

public abstract class Zamestnanec implements Serializable {
	private int id;
	private String jmeno;
	private String prijmeni;
	private int rokNarozeni;
	private TypZamestnance typ;

    protected List<Spoluprace> spolupracovnici = new ArrayList<>();

    public Zamestnanec(int id, String jmeno, String prijmeni, int rokNarozeni, TypZamestnance typ) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.typ = typ;
    }

    public void pridejSpolupraci(Zamestnanec kolega, UrovenSpoluprace uroven) {
        if (kolega == null || kolega.getId() == this.id) {
            return;
        }

        if (!maSpolupracovnika(kolega.getId())) {
            spolupracovnici.add(new Spoluprace(kolega, uroven));
        }
    }
    
    public boolean maSpolupracovnika(int id) {
        return spolupracovnici.stream()
                .anyMatch(s -> s.getKolega().getId() == id);
    }

    public List<Spoluprace> getSpolupracovnici() {
        return spolupracovnici;
    }
    
    public void vypisInfo() {
        System.out.println("ID: " + id);
        System.out.println("Jméno: " + jmeno + " " + prijmeni);
        System.out.println("Rok narození: " + rokNarozeni);
        System.out.println("Počet spolupracovníků: " + spolupracovnici.size());

        if (!spolupracovnici.isEmpty()) {
            int spatna = 0, prumerna = 0, dobra = 0;

            for (Spoluprace s : spolupracovnici) {
                switch (s.getUroven()) {
                    case SPATNA -> spatna++;
                    case PRUMERNA -> prumerna++;
                    case DOBRA -> dobra++;
                }
            }

            System.out.println("Statistiky spolupráce:");
            System.out.println("Špatná: " + spatna);
            System.out.println("Průměrná: " + prumerna);
            System.out.println("Dobrá: " + dobra);
        }
    }

    
    
    public abstract void specialniFunkce();
    
    public int getId() {
        return id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public int getRokNarozeni() {
        return rokNarozeni;
    }

    public TypZamestnance getTyp() {
        return typ;
    }
}