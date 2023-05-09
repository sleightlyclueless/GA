package Util;

/* Class for points in 2d dimension */
public class Point {
  public int x, y;

  /* Constructor with coordinates for their position */
  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
  @Override
  public String toString() {
    return "(" + this.x + "," + this.y + ")";
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Point otherClass)) return false;
    if (other == this) return true;
    return otherClass.x == x && otherClass.y == y;
  }

  /* Directions in which they point to */
  public enum Direction {
    Straight,
    Right,
    Left;
  }
}