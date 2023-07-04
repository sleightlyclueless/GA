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
import java.util.SplittableRandom;

public class GeneticAlgorithm {
  /* Attributes */
  private final int maxGenerations;
  private Folding bestFolding;
  private Population population;
  private static BufferedWriter writer; // for the csv

  // P3
  // ================================================================================================================
  private float mutationRate = 0.05f;
  private final float crossoverRate = 0.05f;
  // P4 & P5
  // ================================================================================================================
  private final float initialMutationRate = 0.02f;

  private int selectionChoice = -1;

  /* headline for csv only static once */
  static {
    try {
      writer = Files.newBufferedWriter(Paths.get("results.csv"));
      writer.write("GenNo.; GenAvFit.; FitBestGenCan.; FitBestAll.; BestEnergyAll; BestOverlapAll; mutationRate\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /* Constructor */
  public GeneticAlgorithm(int maxGen, int elementsPerGen, String sequence, int selection) {
    maxGenerations = maxGen;
    population = Population.randomPopulation(sequence, elementsPerGen);                             // 1. Random start population

    mutationRate = initialMutationRate;
    // P3
    // ================================================================================================================
    population.setMutationRate(mutationRate); // initial mutation rate
    population.setCrossoverRate(crossoverRate);
    // ================================================================================================================
    selectionChoice = selection;
  }


  public Folding findBestFolding() {                                                                // 2. highest fitness evaluation selection (called in main)
    // Gen 0
    bestFolding = population.getBestFolding();
    Imaging i = new Imaging();
    i.drawFolding(bestFolding,population.getSequence(), "best_folding_genz.png");

    // ==================== Start with evolutionary process ====================
    int genCounter = 0;
    while (genCounter < maxGenerations) {                                                           // 3. update for the "better" populations
      System.out.printf("Generation #%d%n", genCounter);

      // log all generation data
      logGeneration(genCounter);

      // save best folding overall generations
      if (population.getBestFolding().getFitness() > bestFolding.analyzeFolding(population.getSequence()))
        bestFolding = population.getBestFolding();


      if (selectionChoice == 0)
        population = new Population(population.getSequence(), fitnessProportionateSelection(population)); // 4. find best candidates and move on to next generation
      else
        population = new Population(population.getSequence(), tournamentSelection(population));


      // P4 & P5
      // =============================================================================================================
      // HERE
      //population.setMutationRate(mutationRate);
      population.setMutationRate(calculateNewMutationRate(mutationRate));
      // P3
      // ================================================================================================================
      //population.setMutationRate(mutationRate);
      population.setCrossoverRate(crossoverRate);
      System.out.printf("Generation #%d, Mutation Rate: %f%n", genCounter, population.getMutationRate());
      population.crossover();
      population.mutation();
      // ================================================================================================================

      genCounter++;
    }

    try {
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bestFolding;
  }

  // P4 & P5
  // ================================================================================================================
  private float calculateNewMutationRate(float oldRate){                                            // continuously lower mutation rate by subtracting from it a fraction of the original mutationrate / generationsize
    return mutationRate = Float.max(0, oldRate - initialMutationRate / maxGenerations);       // 0.02 - (0.02 / 1000) = 0.02 - 0.00002 = 0.01998 | 0.01998 - (0.02 / 1000) etc. (ending in 0)
  }


  private void logGeneration(int currentGen) {
    try {
      String formatString = String.format("%d;%f;%f;%f;%d;%d;%f3\n",
          currentGen,                                           // Generation Number
          population.getAverageFitness(),                       // Average Fitness of Generation
          population.getBestFolding().getFitness(),             // Fitness of best candidate of Generation
          bestFolding.analyzeFolding(population.getSequence()), // Best Fitness
          bestFolding.getEnergy(),                              // Best Energy
          bestFolding.getOverlaps(),                            // Best Overlaps
          mutationRate);                                        // Mutation Rate (P4 & P5)

      System.out.println(formatString);
      writer.write(formatString);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private List<Folding> fitnessProportionateSelection(Population population) {

    double popFit = 0;
    for (Folding f : population.getGeneration())                                                    // 1. sum o fitness
      popFit += f.analyzeFolding(population.getSequence());


    Random rng = new Random();
    List<Folding> newPopulation = new ArrayList<>(population.getGeneration().size());

    for (int i = 0; i < population.getGeneration().size(); i++) {                                   // 2. loop population size 1-x: same size generated again
      double selectedVal = rng.nextDouble() * popFit;                                               // random value to be surpassed by threshold, generated out of popFit * rng value between 0 - 1

      float threshold = 0;
      for (Folding f : population.getGeneration()) {                                                // go through each folding of population
        threshold += f.analyzeFolding(population.getSequence());                                    // add up threshold with the fitness of each folding

        if(threshold >= selectedVal) {                                                              // surpassed threshold - take that folding and start (high fitness higher chance to surpass threshold but also smaller fair chance for others)
          newPopulation.add(new Folding(f.getDirections()));                                        // NOTE: Make sure to add new to avoid deep copies
          break;
        }

      }
    }
    return newPopulation;
  }


  // P4 & P5
  // ================================================================================================================
  private List<Folding> tournamentSelection(Population population) {
    final int k = 2;                                                                                // amount of candidates in tournament
    List<Folding> newPopulation = new ArrayList<>();

    while (newPopulation.size() < population.getGeneration().size()) {                              // 4. until we have a new generation with same size
      List<Folding> tournamentCandidates = new ArrayList<>();
      for (int i = 0; i < k; i++) {                                                                 // 1. choose k random candidates out of population
        int aminoSelection = new SplittableRandom().nextInt(population.getGeneration().size());
        tournamentCandidates.add(population.getGeneration().get(aminoSelection));
      }
      newPopulation.add(tournament(tournamentCandidates, population.getSequence()));                // 2. tournament between those candidates
    }
    return newPopulation;
  }

  private Folding tournament(List<Folding> tournamentCandidates, String sequence) {
    double t = 0.75;                                                                                // 75% chance of the higher fitness to win in tournament
    Folding winner = tournamentCandidates.get(0);

    for (Folding folding : tournamentCandidates) {                                                  // loop through all candidates
      if (folding.analyzeFolding(sequence) > winner.analyzeFolding(sequence)){
        winner = folding;                                                                           // if a candidate with a higher fitness is found override first per default
      }
      else if(t< new SplittableRandom().nextDouble())
        winner = folding;                                                                           // however: with a 25% chance of a candidate with a lower fitness override to the new one anyway
    }
    return new Folding(winner.getDirections());                                                     // 3. winner gets added to new generation
  }
}


