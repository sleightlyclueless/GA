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

    int popultationSize = 100;
    int generations = 100;
    Imaging i = new Imaging();

    // SEQUENCE
    String sequence = chooseStartSequence();

    GeneticAlgorithm ga = new GeneticAlgorithm(generations, popultationSize, sequence);

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
        "11010101011110100010001000010001000101111010101011"  // Seq50
    );

    int counter = 0;
    for (String s : sequences)
      System.out.println(String.format("[%d] %s", counter++, s));

    System.out.println("Please select one of the sequences my number");
    int selection = Integer.parseInt(reader.readLine());

    return sequences.get(selection);
  }
}

// NOTE: CSV -> GRAPH:
// 1. Open csv in excel
// 2. Spalten auswählen
// 3. Einfügen -> Empfohlene Graphik
// 4. Linie


// Fitness - Proportionale Selektion ohne Mutation & Crossover - irgendwann nur noch Klone