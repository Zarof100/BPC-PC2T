import java.util.*;

public class DatovyAnalytik extends Zamestnanec {

    public DatovyAnalytik(int id, String jmeno, String prijmeni, int rok) {
        super(id, jmeno, prijmeni, rok, TypZamestnance.ANALYTIK);
    }

    
    private int spolecniSpolupracovnici(Zamestnanec jiny) {
        Set<Integer> mojeSpoluprace = spolupracovnici.stream()
                .map(s -> s.getKolega().getId())
                .collect(java.util.stream.Collectors.toSet());

        return (int) jiny.getSpolupracovnici().stream()
                .map(s -> s.getKolega().getId())
                .filter(mojeSpoluprace::contains)
                .count();
    }
    
    @Override
    public void specialniFunkce() {
        Zamestnanec nejlepsi = null;
        int maxSpolecnych = 0;

        if (spolupracovnici.isEmpty()) {
            System.out.println("Datový analytic nemá žádné spolupracovníky.");
            return;
        }

        for (Spoluprace s : spolupracovnici) {
            Zamestnanec kolega = s.getKolega();
            int spolecni = spolecniSpolupracovnici(kolega);

            if (spolecni > maxSpolecnych) {
                maxSpolecnych = spolecni;
                nejlepsi = kolega;
            }
        }

        if (nejlepsi != null) {
            System.out.println("Nejvíce společných spolupracovníků má s: "
                    + nejlepsi.getPrijmeni() + " " + nejlepsi.getJmeno()
                    + " (ID: " + nejlepsi.getId() + ")");
        } else {
            System.out.println("Datový analytic nemá žádného spolupracovníka se kterým by měl společné spolupracovníky");
        }
    }
}