import Util.Folding;
import Util.Imaging;
import Util.Point.Direction;
import java.io.IOException;
import java.util.List;

public class Main {

  public static void main(String[] args) throws IOException {
    Imaging i = new Imaging();
    // 0 - hydrophil | 1 - hydrophobe
    String sequence = "10110001";

    Folding f1 = new Folding(List.of(Direction.Straight, Direction.Left, Direction.Left, Direction.Right, Direction.Right, Direction.Straight, Direction.Right));
    f1.analyzeFolding(sequence);
    System.out.printf("Fitness 1: %f\nOverlaps 1: %d\nEnergy 1: %d\n%n", f1.getFitness(), f1.getOverlaps(), f1.getEnergy());
    i.drawFolding(f1, sequence, "folding1.png");

    Folding f2 = new Folding(List.of(Direction.Straight,Direction.Left, Direction.Left, Direction.Right, Direction.Left, Direction.Left, Direction.Left));
    f2.analyzeFolding(sequence);
    System.out.printf("Fitness 2: %f\nOverlaps 2: %d\nEnergy 2: %d\n%n", f2.getFitness(), f2.getOverlaps(), f2.getEnergy());
    i.drawFolding(f2, sequence, "folding2.png");
  }
}
