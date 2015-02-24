package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.HaMap;
import action.Action;
import ai.AI;
import ai.heuristic.IHeuristic;

public class NestedEvolution implements AI {

	public int popSize;
	public int nestedPopSize;
	public int generations;
	public double killRate;
	public double mutRate;
	public IHeuristic heuristic;
	public int nested;
	
	private final List<NestedGenome> pop;
	private List<Action> actions;
	private final Random random;
	private NestedGenome bestGenome;
	
	

	public NestedEvolution(int popSize, int nestedPopSize, double mutRate,
			double killRate, int generations, IHeuristic heuristic) {
		super();
		this.popSize = popSize;
		this.nestedPopSize = nestedPopSize;
		this.mutRate = mutRate;
		this.generations = generations;
		this.heuristic = heuristic;
		this.killRate = killRate;
		pop = new ArrayList<NestedGenome>();
		actions = new ArrayList<Action>();
		random = new Random();
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

		final List<NestedGenome> killed = new ArrayList<NestedGenome>();
		final GameState clone = new GameState(state.map);
		clone.imitate(state);
		
		for (int g = 0; g < generations; g++) {

			//System.out.println("Generation=" + g + " Pop size=" + pop.size());

			// Test pop
			for (final NestedGenome genome : pop) {
				// System.out.print("|");
				clone.imitate(state);
				genome.state = clone;
				genome.run();
				genome.visits++;
			}

			// Kill worst genomes
			Collections.sort(pop);
			killed.clear();
			final int idx = (int) Math.floor(pop.size() * killRate);
			for (int i = idx; i < pop.size(); i++)
				killed.add(pop.get(i));
			
			if (!pop.get(0).equals(bestGenome))
				bestGenome = pop.get(0);
			
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

		actions = pop.get(0).actions;

	}

	private void setup(GameState state) {

		pop.clear();
		final GameState clone = new GameState(null);

		for (int i = 0; i < popSize; i++) {
			clone.imitate(state);
			final NestedGenome genome = new NestedGenome(nested, nestedPopSize, killRate, mutRate);
			genome.random(clone);
			pop.add(genome);
		}

	}

	@Override
	public Action init(GameState state, long ms) {

		return null;
	}

}
