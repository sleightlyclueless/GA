package Util;

import Base.Folding;
import Base.Population;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
  /* Attributes */
  private static BufferedWriter writer; // for the csv
  private final int maxGenerations;
  private Folding bestFolding;
  private Population population;

  /* headline for csv only static once */
  static {
    try {
      writer = Files.newBufferedWriter(Paths.get("results.csv"));
      writer.write("Generation Number, Average Fitness of Generation, Fitness of best candidate of Generation, Best Fitness, Best Energy, Best Overlaps\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /* Constructor */
  public GeneticAlgorithm(int maxGen, int elementsPerGen, String sequence) {
    maxGenerations = maxGen;
    population = Population.randomPopulation(sequence, elementsPerGen);                             // 1. Random start population
  }


  public Folding findBestFolding() {                                                                // 2. highest fitness evaluation
    bestFolding = population.getBestFolding();
    Imaging i = new Imaging();
    i.drawFolding(bestFolding,population.getSequence(), "best_folding_genz.png");

    int genCounter = 0;
    while (genCounter < maxGenerations) {                                                           // 3. update for the "better" populations
      // log all generation data
      logGeneration(genCounter);

      // if a better population was found log it
      if (population.getFitnessOfBestFolding() > bestFolding.analyzeFolding(population.getSequence())) bestFolding = population.getBestFolding();
      population = new Population(population.getSequence(), fitnessProportionateSelection(population)); // 4. find the nearest best folding
      System.out.printf("Generation #%d%n", genCounter);
      genCounter++;
    }

    try {
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bestFolding;
  }


  private void logGeneration(int currentGen) {
    try {
      String formatString = String.format("%d,%f,%f,%f,%d,%d\n",
          currentGen, population.getAverageFitness(),
          population.getFitnessOfBestFolding(),
          bestFolding.analyzeFolding(population.getSequence()),
          bestFolding.getEnergy(),
          bestFolding.getOverlaps());

      System.out.println(formatString);
      writer.write(formatString);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private List<Folding> fitnessProportionateSelection(Population population) {

    // get sum of fitness in all population candidates
    double popFit = 0;
    for (Folding f : population.getGeneration())
      popFit += f.analyzeFolding(population.getSequence());

    // GO THROUGH ALL GENERATIONS
    // give each candidate in population the chance to survive rng´ed by its fitness level in ration to total fitness
    List<Folding> newPopulation = new ArrayList<>(population.getGeneration().size());
    Random rng = new Random();
    for (int i = 0; i < population.getGeneration().size(); i++) {
      double selectedVal = rng.nextDouble() * popFit; // 0, 0.1, ..., 1 * total fitness as rng value in the selection

      // GO THROUGH ALL FOLDINGS IN GENERATION
      // sum counter to avoid choosing too many foldings. If we added up to the population were done
      float curSum = 0;
      for (Folding f : population.getGeneration()) {
        curSum += f.analyzeFolding(population.getSequence());
        if(curSum >= selectedVal) { // if we surpassed the total of all candidates we filtered out the better ones - break
          newPopulation.add(new Folding(f.getDirections()));
          break;
        }
      }
    }
    return newPopulation;
  }
}


