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
        "101001101010101",               // Seq15
        "10100110100100110101",          // Seq20
        "1010011010010011011010101",     // Seq25
        "101001101001001101101010110101" // Seq30
    );

    int counter = 0;
    for (String s : sequences)
      System.out.println(String.format("[%d] %s", counter++, s));

    System.out.println("Please select one of the sequences my number");
    int selection = Integer.parseInt(reader.readLine());

    return sequences.get(selection);
  }
}
