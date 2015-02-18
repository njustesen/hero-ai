package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.HAMap;
import action.Action;
import ai.AI;
import ai.heuristic.IHeuristic;

public class RollingHorizonEvolution implements AI {

	public static List<Integer> foundIn = new ArrayList<Integer>(); 
	public static List<List<Integer>> bestHash = new ArrayList<List<Integer>>(); 
	public static List<List<Double>> bestFitness = new ArrayList<List<Double>>(); 
	public static List<List<Double>> bestVals = new ArrayList<List<Double>>();
	public static List<Integer> bestG = new ArrayList<Integer>();
	public static List<List<Integer>> bestVisits = new ArrayList<List<Integer>>(); 
	
	public int popSize;
	public int generations;
	public double killRate;
	public double mutRate;
	public IHeuristic heuristic;

	private final List<Genome> pop;
	private List<Action> actions;
	private final Random random;
	private Genome bestGenome;
	private boolean strong;

	public RollingHorizonEvolution(int popSize, double mutRate,
			double killRate, int generations, IHeuristic heuristic, boolean strong) {
		super();
		this.popSize = popSize;
		this.mutRate = mutRate;
		this.generations = generations;
		this.heuristic = heuristic;
		this.killRate = killRate;
		pop = new ArrayList<Genome>();
		actions = new ArrayList<Action>();
		random = new Random();
		this.strong = strong;
	}

	@Override
	public Action act(GameState state, long ms) {

		if (actions.isEmpty())
			search(state);

		final Action next = actions.get(0);
		actions.remove(0);
		return next;
	}

	private void search(GameState state) {

		setup(state);

		final List<Genome> killed = new ArrayList<Genome>();
		final GameState clone = new GameState(HAMap.mapA);
		clone.imitate(state);
		
		int found = 0;
		List<Double> fitness = new ArrayList<Double>();
		List<Double> vals = new ArrayList<Double>();
		List<Integer> hash = new ArrayList<Integer>();
		List<Integer> visits = new ArrayList<Integer>();
		for (int g = 0; g < generations; g++) {

			//System.out.println("Generation=" + g + " Pop size=" + pop.size());

			// Test pop
			for (final Genome genome : pop) {
				// System.out.print("|");
				clone.imitate(state);
				clone.update(genome.actions);
				final double val = heuristic.normalize(heuristic.eval(clone, state.p1Turn));
				if (genome.visits == 0 || val < genome.value)
					genome.value = val;
				genome.visits++;
			}

			// Kill worst genomes
			Collections.sort(pop);
			killed.clear();
			final int idx = (int) Math.floor(pop.size() * killRate);
			for (int i = idx; i < pop.size(); i++)
				killed.add(pop.get(i));
			
			if (!pop.get(0).equals(bestGenome)){
				bestGenome = pop.get(0);
				found = g+1;
			}
			hash.add(bestGenome.hashCode());
			visits.add(bestGenome.visits);
			fitness.add(bestGenome.fitness());
			vals.add(bestGenome.value);
			
			if (g != generations)
				// Crossover new ones
				for (final Genome genome : killed) {
					final int a = random.nextInt(idx);
					int b = random.nextInt(idx);
					while (b == a)
						b = random.nextInt(idx);

					clone.imitate(state);
					genome.crossover(pop.get(a), pop.get(b), clone);

					// Mutation
					if (Math.random() < mutRate) {
						clone.imitate(state);
						genome.mutate(clone);
					}

				}
			
		}
		
		foundIn.add(found);
		bestFitness.add(fitness);
		bestVals.add(vals);
		bestG.add(bestGenome.visits);
		bestHash.add(hash);
		bestVisits.add(visits);
		//System.out.println("Best Genome: " + pop.get(0).actions);
		//System.out.println("Visits: " + pop.get(0).visits);
		//System.out.println("Value: " + pop.get(0).avgValue());

		actions = pop.get(0).actions;

	}

	private void setup(GameState state) {

		pop.clear();
		final GameState clone = new GameState(HAMap.mapA);

		for (int i = 0; i < popSize; i++) {
			clone.imitate(state);
			final Genome genome;
			if (strong)
				genome = new StrongGenome(generations);
			else
				genome = new WeakGenome();
			genome.random(clone);
			pop.add(genome);
		}

	}

	@Override
	public Action init(GameState state, long ms) {

		return null;
	}

}
