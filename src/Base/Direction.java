/* Base 1 - Direction class for all the directions we can travel from one point to the next point */
/* Used in the changeDeltaVector function in Folding */
package Base;

import java.util.SplittableRandom;

public enum Direction {
  Straight,
  Right,
  Left;

  public static Direction getRandomDirection() {
    int randomNumber = new SplittableRandom().nextInt(3);

    return switch (randomNumber) {
      case 0 -> Direction.Left;
      case 1 -> Direction.Right;
      default -> Direction.Straight;
    };
  }
}
