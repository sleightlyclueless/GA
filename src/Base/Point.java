/* Base 2 - Point class, representing a point in a folding with its x and y pos being calculated in a folding from prev-pos + delta-vector out of directions */
package Base;

// P1
// ================================================================================================================
/* Class for points in 2d dimension */
public class Point {
  public int x, y;

  /* Constructor with coordinates for their position */
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // Custom toString if for debugging etc. points are to be printed
  @Override
  public String toString() {
    return "(" + this.x + "," + this.y + ")";
  }

  // Custom equals for x and y comparison
  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Point otherClass)) return false;
    if (other == this) return true;
    return otherClass.x == x && otherClass.y == y;
  }
}