import java.util.Scanner;
import java.time.Year;

public class Main {

    public static void main(String[] args) {
        Firma firma = new Firma();
        firma.vytvorTabulku();
        firma.nactiZDB();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1 - Přidat zaměstnance");
            System.out.println("2 - Přidat spolupráci");
            System.out.println("3 - Odebrat zaměstnance");
            System.out.println("4 - Vyhledat zaměstnance");
            System.out.println("5 - Spustit dovednost zaměstnance");
            System.out.println("6 - Výpis podle skupin");
            System.out.println("7 - Statistiky");
            System.out.println("8 - Počty zaměstnanců");
            System.out.println("0 - Konec");

            int volba = nactiInt(sc, "Vyber možnost:");

            switch (volba) {
                case 1 -> {
                    TypZamestnance typ;

                    while (true) {
                        String vstup = nactiText(sc, "Typ (analytik / specialista):").toLowerCase();

                        switch (vstup) {
                            case "analytik", "a" -> {
                                typ = TypZamestnance.ANALYTIK;
                                break;
                            }
                            case "specialista", "s" -> {
                                typ = TypZamestnance.SPECIALISTA;
                                break;
                            }
                            default -> {
                                System.out.println("Neplatný typ zaměstnance!");
                                continue;
                            }
                        }
                        break;
                    }

                    String jmeno = nactiText(sc, "Jméno:");
                    String prijmeni = nactiText(sc, "Příjmení:");
                    int rok = nactiRok(sc);

                    Zamestnanec z = firma.pridejZamestnance(typ, jmeno, prijmeni, rok);
                    System.out.println("Přidán zaměstnanec s ID: " + z.getId());
                }

                case 2 -> {
                    int id1 = nactiId(sc, "ID zaměstnance:");
                    int id2 = nactiId(sc, "ID kolegy:");

                    UrovenSpoluprace uroven;

                    while (true) {
                        String u = nactiText(sc, "Úroveň (SPATNA / PRUMERNA / DOBRA):").toLowerCase();

                        switch (u) {
                            case "spatna", "s" -> {
                                uroven = UrovenSpoluprace.SPATNA;
                                break;
                            }
                            case "prumerna", "p" -> {
                                uroven = UrovenSpoluprace.PRUMERNA;
                                break;
                            }
                            case "dobra", "d" -> {
                                uroven = UrovenSpoluprace.DOBRA;
                                break;
                            }
                            default -> {
                                System.out.println("Neplatná úroveň, zkuste znovu.");
                                continue;
                            }
                        }
                        break;
                    }

                    firma.pridejSpolupraci(id1, id2, uroven);
                }

                case 3 -> {
                    int id = nactiId(sc, "ID zaměstnance k odebrání:");
                    firma.odeberZamestnance(id);
                    System.out.println("Zaměstnanec odebrán.");
                }

                case 4 -> {
                    int id = nactiId(sc, "Zadej ID:");
                    firma.vyhledejZamestnance(id);
                }

                case 5 -> {
                    int id = nactiId(sc, "Zadej ID:");
                    firma.spustDovednost(id);
                }

                case 6 -> firma.vypisZamestnancePodleSkupin();

                case 7 -> firma.statistiky();

                case 8 -> firma.vypisPoctySkupin();

                case 0 -> {
                    System.out.println("Ukládám data...");
                    firma.ulozDoDB();
                    System.out.println("Konec programu.");
                    return;
                }

                default -> System.out.println("Neplatná volba.");
            }
        }
    }

    public static int nactiInt(Scanner sc, String zprava) {
        while (true) {
            System.out.println(zprava);
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                sc.nextLine();
                return value;
            } else {
                System.out.println("Zadejte platné číslo!");
                sc.nextLine();
            }
        }
    }

    public static int nactiId(Scanner sc, String zprava) {
        while (true) {
            int id = nactiInt(sc, zprava);
            if (id >= 0) {
                return id;
            } else {
                System.out.println("ID musí být nezáporné!");
            }
        }
    }

    public static String nactiText(Scanner sc, String zprava) {
        while (true) {
            System.out.println(zprava);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Hodnota nesmí být prázdná!");
            }
        }
    }

    public static int nactiRok(Scanner sc) {
        while (true) {
            int rok = nactiInt(sc, "Rok narození:");
            int currentYear = Year.now().getValue();

            if (rok > 1900 && rok <= currentYear - 14) {
                return rok;
            } else {
                System.out.println("Neplatný rok!");
            }
        }
    }
}