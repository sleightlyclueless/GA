package Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

public class Imaging {
  private final int PixelScale = 75;
  private Folding folding;
  private String sequence;
  private Point _min;                                             // smallest coordinates in folding
  private Point _max;                                             // max coordinates in folding


  public void drawFolding(Folding f, String s, String filename) {
    folding = f;
    _min = folding.getMinValue();
    _max = folding.getMaxValue();
    sequence = s;

    // Create a buffered image
    // ================================================
    // Calculate height and width of the image
    int height = 100 * (_max.y - _min.y) + ((s.length()-1) * 5) + 10;
    int width = 100 * (_max.x - _min.x) + ((s.length()-1) * 3);

    // Create an image with rgb values of the calculated width and height
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics graphics = image.createGraphics();
    graphics.fillRect(0, 0, width + 5, height + 5); // Padding of 5px around


    // DRAW GRID FOR FOLDING
    drawGrid(graphics);


    // FOR EACH NODE DRAW IN SPECIFIC PLACE
    for (int i = 0; i < sequence.length(); i++) {
      drawNode(graphics, i, folding.getPosition(i), sequence.charAt(i) == '1');
    }

    // DRAW CONNECTIONS BETWEEN NODES
    drawConnections(graphics, folding);

    graphics.drawString(String.format("Fitness: %f\n Energy: %d\nOverlaps: %d", folding.analyzeFolding(sequence), folding.getEnergy(), folding.getOverlaps()), 5,
        height - 5
    );

    // Try to draw the image... so much stuff has happened...
    try {
      ImageIO.write(image, "png", Paths.get(filename).toFile());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Draw a grid of squares for the complete dimensions of smallest and biggest coordinates
  private void drawGrid(Graphics graphics) {
    graphics.setColor(Color.darkGray);
    for (int x = _min.x; x <= _max.x; x++)
      for (int y = _min.y; y <= _max.y; y++)
        graphics.drawRect((x * PixelScale - _min.x * PixelScale),(y * PixelScale - _min.y * PixelScale), PixelScale, PixelScale);
  }


  private void drawNode(Graphics graphics, int id, Point node, boolean isHydrophobic) {
    // Draw the node
    int xStart = node.x * PixelScale;
    if (_min.x < 0 ) xStart = xStart + ((_min.x * PixelScale) * -1);
    int yStart = (node.y * PixelScale) * -1; // inverse due to origin being top left and y inversed to conventional grid
    if (_max.y > 0 ) yStart = yStart + (_max.y * PixelScale);

    graphics.setColor(isHydrophobic ? Color.BLACK : Color.WHITE);
    graphics.fillRect(xStart + 5, yStart + 5, PixelScale - 10, PixelScale - 10);
    System.out.printf("(%d / %d)\n", xStart, yStart);


    // Check for overlaps
    int overlaps = folding.getOverlapsAtPosition(id);
    graphics.setColor(Color.WHITE);
    if (overlaps > 0) {
      //System.out.println("drawing...");
      graphics.drawString(String.valueOf(overlaps), xStart + PixelScale / 3, yStart + PixelScale / 3);
    }
    if(id == 0)
      graphics.drawString("Start:", (xStart + (PixelScale - 10) / 3),(yStart + (PixelScale - 10) / 3));
    if(id  == sequence.length()-1)
      graphics.drawString("End:", (xStart + (PixelScale - 10) / 3),(yStart + (PixelScale - 10) / 3));
  }

  private void drawConnections(Graphics graphics, Folding folding) {
    graphics.setColor(Color.GRAY);
    for (int i = 0; i < sequence.length()-1; i++) {

      int xStart = folding.getPosition(i).x * PixelScale;
      if (_min.x < 0 ) xStart = xStart + ((_min.x * PixelScale) * -1);

      int yStart = (folding.getPosition(i).y * PixelScale) * -1;
      if (_max.y > 0 ) yStart = yStart + (_max.y * PixelScale);


      int xEnd = folding.getPosition(i + 1).x * PixelScale;
      if (_min.x < 0 ) xEnd = xEnd + ((_min.x * PixelScale) * -1);

      int yEnd = (folding.getPosition(i+1).y * PixelScale) * -1;
      if (_max.y > 0 ) yEnd = yEnd + (_max.y * PixelScale);

      //System.out.printf("Start: (%d / %d)\n", xStart, yStart);
      //System.out.printf("End: (%d / %d)\n", xEnd, yEnd);

      graphics.drawLine(xStart + PixelScale / 2, yStart + PixelScale / 2, xEnd + PixelScale / 2, yEnd + PixelScale / 2);
    }
  }

}
