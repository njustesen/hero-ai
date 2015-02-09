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

	public int popSize;
	public int generations;
	public double killRate;
	public double mutRate;
	public IHeuristic heuristic;

	private final List<Genome> pop;
	private List<Action> actions;
	private final Random random;

	public RollingHorizonEvolution(int popSize, double mutRate,
			double killRate, int generations, IHeuristic heuristic) {
		super();
		this.popSize = popSize;
		this.mutRate = mutRate;
		this.generations = generations;
		this.heuristic = heuristic;
		this.killRate = killRate;
		pop = new ArrayList<Genome>();
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

		final List<Genome> killed = new ArrayList<Genome>();
		final GameState clone = new GameState(HAMap.mapA);

		for (int g = 0; g < generations; g++) {

			System.out.println("Generation=" + g + " Pop size=" + pop.size());

			// Test pop
			for (final Genome genome : pop) {
				// System.out.print("|");
				clone.imitate(state);
				clone.update(genome.actions);
				final double val = heuristic.eval(clone, state.p1Turn);
				genome.visits++;
				genome.value += val;
			}

			// Kill worst genomes
			Collections.sort(pop);
			killed.clear();
			final int idx = (int) Math.floor(pop.size() * killRate);
			for (int i = idx; i < pop.size(); i++)
				killed.add(pop.get(i));

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

		System.out.println("Best Genome: " + pop.get(0).actions);
		System.out.println("Visits: " + pop.get(0).visits);
		System.out.println("Value: " + pop.get(0).avgValue());

		actions = pop.get(0).actions;

	}

	private void setup(GameState state) {

		pop.clear();
		final GameState clone = new GameState(HAMap.mapA);

		for (int i = 0; i < popSize; i++) {
			clone.imitate(state);
			final Genome genome = new Genome();
			genome.random(clone);
			pop.add(genome);
		}

	}

	@Override
	public Action init(GameState state, long ms) {

		return null;
	}

}
