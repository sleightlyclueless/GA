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
    // Gen 0
    bestFolding = population.getBestFolding();
    Imaging i = new Imaging();
    i.drawFolding(bestFolding,population.getSequence(), "best_folding_genz.png");

    // ====================
    // Start with evolutionary process
    int genCounter = 0;
    while (genCounter < maxGenerations) {                                                           // 3. update for the "better" populations
      // log all generation data
      logGeneration(genCounter);

      // if a better population was found log it
      if (population.getFitnessOfBestFolding() > bestFolding.analyzeFolding(population.getSequence())) bestFolding = population.getBestFolding();
      population = new Population(population.getSequence(), fitnessProportionateSelection(population)); // 4. find best candidates and move on to next generation
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


    Random rng = new Random();
    List<Folding> newPopulation = new ArrayList<>(population.getGeneration().size());

    for (int i = 0; i < population.getGeneration().size(); i++) {                                   // always get 1 each loop from 0-x: so same size generated again
      double selectedVal = rng.nextDouble() * popFit; // fitness proportionate: rng value between 0 - 1 * total fitness as threshold


      float curSum = 0;
      for (Folding f : population.getGeneration()) {                                                // go through each folding of population, add up sum. Higher values higher chance of passing the rng threshold
        curSum += f.analyzeFolding(population.getSequence());

        if(curSum >= selectedVal) {                                                                 // if we surpassed the threshold take that folding into next generation - break and start over until x foldings filled
          newPopulation.add(new Folding(f.getDirections()));
          break;
        }

      }
    }
    return newPopulation;
  }
}


