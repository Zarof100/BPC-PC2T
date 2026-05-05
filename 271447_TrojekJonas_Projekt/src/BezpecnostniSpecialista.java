
public class BezpecnostniSpecialista extends Zamestnanec {

	public BezpecnostniSpecialista(int id, String jmeno, String prijmeni, int rok) {
        super(id, jmeno, prijmeni, rok, TypZamestnance.SPECIALISTA);
    }

	@Override
	public void specialniFunkce() {
	    double score = vypocetRizika();
	    System.out.println("Rizikové skóre: " + score);
	}

	private double vypocetRizika() {
	    if (spolupracovnici.isEmpty()) return 0;

	    double suma = 0;

	    for (Spoluprace s : spolupracovnici) {
	        switch (s.getUroven()) {
	            case SPATNA -> suma += 3;
	            case PRUMERNA -> suma += 2;
	            case DOBRA -> suma += 1;
	        }
	    }

	    return suma / spolupracovnici.size();
	}
}