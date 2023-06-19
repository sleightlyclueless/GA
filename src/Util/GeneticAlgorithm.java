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
  //private final float mutationRate = 0.02f;
  private final float crossoverRate = 0.05f;
  // P4 & P5
  // ================================================================================================================
  private float mutationRate;
  private final float initialMutationRate = 0.02f;

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
  public GeneticAlgorithm(int maxGen, int elementsPerGen, String sequence) {
    maxGenerations = maxGen;
    population = Population.randomPopulation(sequence, elementsPerGen);                             // 1. Random start population

    mutationRate = initialMutationRate;
    // P3
    // ================================================================================================================
    population.setMutationRate(mutationRate); // initial mutation rate
    population.setCrossoverRate(crossoverRate);
    // ================================================================================================================
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


      //population = new Population(population.getSequence(), fitnessProportionateSelection(population)); // 4. find best candidates and move on to next generation
      population = new Population(population.getSequence(), tournamentSelection(population));


      // P4 & P5
      // =============================================================================================================
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
  private float calculateNewMutationRate(float oldRate){
    return mutationRate = Float.max(0, oldRate - initialMutationRate / maxGenerations);
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

    for (int i = 0; i < population.getGeneration().size(); i++) {                                   // loop population size 1-x: same size generated again
      // random value for selection, generated out of popFit - has to be surpassed by threshold
      double selectedVal = rng.nextDouble() * popFit; // fitness proportionate: rng value between 0 - 1 * total fitness as threshold


      float threshold = 0;
      for (Folding f : population.getGeneration()) {                                                // go through each folding of population
        threshold += f.analyzeFolding(population.getSequence());

        if(threshold >= selectedVal) {                                                              // surpassed threshold - take that folding - break and start over until x foldings
          newPopulation.add(new Folding(f.getDirections()));
          break;
        }

      }
    }
    return newPopulation;
  }


  // P4 & P5
  // ================================================================================================================
  private List<Folding> tournamentSelection(Population population) {
    String sequence = population.getSequence();
    final int k = 2;
    double t = 0.75;

    List<Folding> newGeneration = new ArrayList<>();

    while (newGeneration.size() < population.getGeneration().size()) {
      List<Folding> tournamentCandidates = new ArrayList<>();
      for (int i = 0; i < k; i++) {
        int aminoSelection = new SplittableRandom().nextInt(population.getGeneration().size());
        tournamentCandidates.add(population.getGeneration().get(aminoSelection));
      }
      newGeneration.add(tournament(tournamentCandidates, sequence, t));
    }
    return newGeneration;
  }

  private Folding tournament(List<Folding> tournamentCandidates, String sequence, double t) {
    Folding winner = tournamentCandidates.get(0);

    for (Folding folding :
        tournamentCandidates) {
      if (folding.analyzeFolding(sequence) > winner.analyzeFolding(sequence)){
        winner = folding;
      } else if(t< new SplittableRandom().nextDouble())
        winner = folding;
    }
    return new Folding(winner.getDirections());
  }
}


