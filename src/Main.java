import Base.Folding;
import Util.GeneticAlgorithm;
import Util.Imaging;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Main {
  public static void main(String[] args) throws IOException {

    int popultationSize = 5000;
    int generations = 1000;
    Imaging i = new Imaging();

    // SEQUENCE
    String sequence = chooseStartSequence();
    int selectionMethod = chooseSelectionMethod();

    GeneticAlgorithm ga = new GeneticAlgorithm(generations, popultationSize, sequence, selectionMethod);

    Folding best = ga.findBestFolding();
    i.drawFolding(best, sequence, "best_folding_gen_final.png");
  }

  private static String chooseStartSequence() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    // 0 - hydrophil | 1 - hydrophobe
    List<String> sequences = Arrays.asList(
        "10100110100101100101",                               // Seq20
        "110010010010010010010011",                           // Seq24
        "0010011000011000011000011",                          // Seq25
        "000110011000001111111001100001100100",               // Seq36
        "001001100110000011111111110000001100110010011111",   // Seq48
        "11010101011110100010001000010001000101111010101011", // Seq50
        "001110111111110001111111111010001111111111110000111111011010", // Seq60
        "1111111111110101001100110010011001100100110011001010111111111111" // Seq64
    );

    int counter = 0;
    for (String s : sequences)
      System.out.println(String.format("[%d] %s", counter++, s));

    System.out.println("Please select one of the sequences by number");
    int selection = Integer.parseInt(reader.readLine());
    while (selection < 0 || selection > counter-1) {
      System.out.println("Err: Please choose one of the sequences by number");
      selection = Integer.parseInt(reader.readLine());
    }

    return sequences.get(selection);
  }

  private static int chooseSelectionMethod() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    // 0 - hydrophil | 1 - hydrophobe
    List<String> sequences = Arrays.asList(
        "FitnessProportionateSelection",
        "TournamentSelection"
    );

    int counter = 0;
    for (String s : sequences)
      System.out.println(String.format("[%d] %s", counter++, s));

    System.out.println("Please select one of the selection methods");
    int selection = Integer.parseInt(reader.readLine());
    while (selection < 0 || selection > counter-1) {
      System.out.println("Err: Please choose one of the selection methods by number");
      selection = Integer.parseInt(reader.readLine());
    }

    return selection;
  }
}

// NOTE: CSV -> GRAPH:
// 1. Open csv in excel
// 2. Spalten auswählen
// 3. Einfügen -> Empfohlene Graphik
// 4. Linie


// Fitness - Proportionale Selektion ohne Mutation & Crossover - irgendwann nur noch Klone