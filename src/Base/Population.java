package Base;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


// P2
// ================================================================================================================
public class Population {

  /* Attributes */
  private final String sequence; /* sequence of hydrophobe and homophobe points */
  private final List<Folding> generation;
  private final int populationSize;

  /* Getters */
  public String getSequence() { return sequence; }
  public List<Folding> getGeneration() { return generation; }


  // P3
  // ================================================================================================================
  private float mutationRate = 0;
  private float crossoverRate = 0;
  /* Getters */
  public float getMutationRate() { return mutationRate; }
  public float getCrossoverRate() { return crossoverRate; }
  /* Setters */
  public void setMutationRate(float mutationRate) { this.mutationRate = mutationRate; }
  public void setCrossoverRate(float crossoverRate) { this.crossoverRate = crossoverRate; }
  // ================================================================================================================


  /* Constructor */
  /* Initial constructor */
  public Population(String sequence, int populationSize) {
    this.populationSize = populationSize;
    this.sequence = sequence;
    this.generation = new LinkedList<>();
    // Call to randomPopulation in GeneticAlgorithm
  }

  /* Used for next generations - copy constructor */
  public Population(String sequence, List<Folding> folding) {
    this.sequence = sequence;
    this.populationSize = folding.size();
    this.generation = new LinkedList<>();
    for (Folding f : folding)
      this.generation.add(new Folding(f.getDirections()));
  }


  /* Functions */
  /* Fill generation with foldings as many as populationSize given - used for ini Constructor */
  public static Population randomPopulation(String sequence, int elements) {
    Population population = new Population(sequence, elements);

    // for as many foldings as populationsize is required
    for (int i = 0; i < population.populationSize; i++) {

      // Generate random Folding directions for each candidate
      List<Direction> randomDirections = new LinkedList<>();
      for (int j = 0; j < sequence.length() - 1; j++)
        randomDirections.add(Direction.getRandomDirection());

      // Create new folding out of random directions and add it to this population
      Folding folding = new Folding(randomDirections);
      population.generation.add(folding);
    }

    return population;
  }

  // ======= UTIL FOR PRINT AND FINDING BEST CANDIDATE OF EACH AND CUMULATIVE =======
  // move through all foldings of this population and get the one with highest fitness (retval from analyze)
  public Folding getBestFolding() {
    Folding bestFolding = this.generation.get(0);

    for (Folding folding : this.generation)
      if (folding.analyzeFolding(this.sequence) > bestFolding.analyzeFolding(this.sequence))
        bestFolding = folding;

    return bestFolding;
  }

  // same as before but just get the int value of Fitness
  public float getFitnessOfBestFolding() {
    float bestFitness = 0;
    for (Folding folding : this.generation)
      if (folding.analyzeFolding(sequence) > bestFitness)
        bestFitness = folding.analyzeFolding(sequence);

    return bestFitness;
  }

  // map function to get average of all generations
  // ("optimized" to the max from intelliJ
  public float getAverageFitness() {
    return (float) this.generation.stream().mapToDouble(f -> f.analyzeFolding(this.sequence)).average().orElse(0);
  }


  // P3
  // ================================================================================================================
  public void mutation() {
    Random rng = new Random();
    int elementsToMutate = calculateElements(this.mutationRate);
    System.out.println(String.format("Mutating %d elements", elementsToMutate));
    for (int i = 0; i < elementsToMutate; i++) {
      generation.get(rng.nextInt(populationSize)).mutate();
    }
  }

  public void crossover() {
    int toCrossover = calculateElements(this.crossoverRate);
    Random rng = new Random();
    for (int i = 0; i < toCrossover; i++) {
      int f1 = rng.nextInt(sequence.length());
      int f2 = rng.nextInt(sequence.length());
      if (f1 == f2)
        continue;
      int positionToCross = rng.nextInt(sequence.length());
      generation.get(f1).crossover(generation.get(f2), positionToCross);
    }
  }

  private int calculateElements(float rate) {
    return (int) (sequence.length() * populationSize * rate);
  }
  // ================================================================================================================
}

