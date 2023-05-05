package Util;


import Util.Point.*;
import java.util.LinkedList;
import java.util.List;


public class Folding {

  /* Attributes */
  private final List<Direction> directions; /* Consists out of the directions of the points in their given sequence */
  private int overlaps = 0; /* Counter for overlaps */
  private int energy = 0; /* Counter for energy */
  private float fitness = -1; /* Counter for fitness of folding */

  /* Getters */
  public int getOverlaps()  {return this.overlaps;}
  public int getEnergy()    {return this.energy;}
  public float getFitness() {return this.fitness;}


  /* Constructor */
  public Folding(String seq, List<Direction> directions) {
    this.directions = new LinkedList<>();
    this.directions.addAll(directions);
    this.fitness = analyzeFolding(seq); /* Calculate overlaps and energy => fitness */
  }


  /* Functions */
  /* Depending on the directions and seq given, calculate overlaps and energy => fitness */
  public float analyzeFolding(String seq) {

    /* Sequence and directions have to match in order for successful folding generation */
    if (this.directions.size() + 1 != seq.length()) return -1;

    if (this.fitness >= 0) return fitness;


    Point currentPosition = new Point(0, 0);                                                  // Start at 0/0 and in function iterate to according position
    Point currDelta = new Point(0, 1);                                                        // Standard is move up by 0/1



    // Iterate through all the given directions and calculate new Point in folding from there
    for (int i = 0; i < this.directions.size(); i++) {                                              // MOVE THROUGH ALL THE POINTS IN FOLDING
      System.out.println("Curr: " + currentPosition);
      Point iteratePosition = new Point(currentPosition.x, currentPosition.y);                      // Start at 0/0
      Point backupDelta = new Point(currDelta.x, currDelta.y);                                      // Change from this delta over again(!!!!)

      for (int j = i; j < this.directions.size(); j++) {                                            // FOR ALL FUTURE POSITIONS CHECK IF ONE IS AN OVERLAP AND OR HYDROPHOBIC CONNECTION
        changeDeltaVector(backupDelta, this.directions.get(j));                                     // calculate next delta vector (0/1, 0/-1, 1/0, 1/1)
        moveToNextPositions(iteratePosition, backupDelta);                                          // depending on the current delta change next position

        if (j > i) {                                                                                // Check for overlaps and energy
          if (currentPosition.equals(iteratePosition)) overlaps++;
          if (seq.charAt(i) == '1' && isNeighbour(currentPosition, iteratePosition) && seq.charAt(j + 1) == '1') energy++;
        }

        System.out.println(iteratePosition);
      }

      System.out.println("==============================");

      changeDeltaVector(currDelta, this.directions.get(i));
      moveToNextPositions(currentPosition, currDelta);
    }


    this.fitness = energy - overlaps;
    return this.fitness;
  }

  // Checks the current delta direction (0/1, 0/-1, 1/0, 1/1) together with the next direction to go to and changes the vector to add to the prev point
  private void changeDeltaVector(Point currentDelta, Direction direction) {
    // Calculate the delta for the next position
    int x = currentDelta.x;
    int y = currentDelta.y;

    switch (direction) {
      case Left: // Rotate Vector left
        x = currentDelta.y * (-1);
        y = currentDelta.x;
        break;
      case Straight:
        break;
      case Right: // Rotate Vector right
        x = currentDelta.y;
        y = currentDelta.x * (-1);
        break;
    }

    currentDelta.x = x;
    currentDelta.y = y;
  }


  // Sets new position of next amino acid as well as save the current Delta if it changed with another direction
  private void moveToNextPositions(Point iteratePos, Point currentDelta) {
    // Add the x and y coords to the position to move it to its next position
    iteratePos.x += currentDelta.x;
    iteratePos.y += currentDelta.y;

  }

  // Check if the distance between two points, depending on their x and y position, is adjacent
  private boolean isNeighbour(Point pos2, Point pos1) {
    double dist = Math.sqrt(Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.y - pos2.y, 2)); // Calculate positive hypothenuse (straight ~= 1, diagonal ~= 1.4, overlapping ~= 0)
    return dist == 1;
  }



  /* IMAGING FUNCTIONS */
  /*======================================*/
  // check if overlaps exist at a given position by traversing all other points
  public int getOverlapsAtPosition(int index) {
    int o = 0;
    Point position = getPosition(index);
    for (int i = 0; i < directions.size(); i++) {
      if (position.equals(getPosition(i)) && index != i)
        o++;
    }

    return o;
  }

  // Get position in x / y coords by traversing to the point
  public Point getPosition(int index) {
    Point currentDirectionDelta = new Point(0, 1);
    Point position = new Point(0, 0);

    for (int i = 0; i < index; i++) {
      changeDeltaVector(currentDirectionDelta, this.directions.get(i));                             // calculate next delta vector (0/1, 0/-1, 1/0, 1/1)
      moveToNextPositions(position, currentDirectionDelta);
    }

    return position;
  }

  // Get minimum coordinates point in folding for x and y
  public Point getMinValue() {

    Point currentDirectionDelta = new Point(0, 1);
    Point position = new Point(0, 0);

    Point minValue = new Point(0, 0);

    for (Direction currentDirection : this.directions) {
      changeDeltaVector(currentDirectionDelta, currentDirection);                                   // calculate next delta vector (0/1, 0/-1, 1/0, 1/1)
      moveToNextPositions(position, currentDirectionDelta);
      if (position.x < minValue.x)
        minValue.x = position.x;

      if (position.y < minValue.y)
        minValue.y = position.y;
    }

    return minValue;
  }

  // Get max coordinates point in folding for x and y
  public Point getMaxValue() {
    Point currentDirectionDelta = new Point(0, 1);
    Point position = new Point(0, 0);

    Point maxValue = new Point(0, 0);

    for (Direction currentDirection : this.directions) {
      changeDeltaVector(currentDirectionDelta, currentDirection);                                   // calculate next delta vector (0/1, 0/-1, 1/0, 1/1)
      moveToNextPositions(position, currentDirectionDelta);
      if (position.x > maxValue.x)
        maxValue.x = position.x;

      if (position.y > maxValue.y)
        maxValue.y = position.y;
    }

    return maxValue;
  }
}
