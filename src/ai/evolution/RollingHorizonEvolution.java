package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.HaMap;
import action.Action;
import ai.AI;
import ai.heuristic.IStateEvaluator;

public class RollingHorizonEvolution implements AI {

	public int popSize;
	public int generations;
	public double killRate;
	public double mutRate;
	public IStateEvaluator evaluator;
	
	private final List<Genome> pop;
	private List<Action> actions;
	private final Random random;
	private boolean strong;

	public RollingHorizonEvolution(int popSize, double mutRate,
			double killRate, int generations, IStateEvaluator evaluator, boolean strong) {
		super();
		this.popSize = popSize;
		this.mutRate = mutRate;
		this.generations = generations;
		this.evaluator = evaluator;
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
		final GameState clone = new GameState(state.map);
		clone.imitate(state);
		
		for (int g = 0; g < generations; g++) {

			//System.out.println("Generation=" + g + " Pop size=" + pop.size());

			// Test pop
			double val = 0;
			for (final Genome genome : pop) {
				// System.out.print("|");
				clone.imitate(state);
				clone.update(genome.actions);
				val = evaluator.eval(clone, state.p1Turn);
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

		//System.out.println("Best Genome: " + pop.get(0).actions);
		//System.out.println("Visits: " + pop.get(0).visits);
		//System.out.println("Value: " + pop.get(0).avgValue());

		actions = pop.get(0).actions;

	}

	private void setup(GameState state) {

		pop.clear();
		final GameState clone = new GameState(null);

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
	public void init(GameState state, long ms) {
		// TODO: 
	}
	
	@Override
	public String header() {
		String name = title()+"\n";
		name += "Pop. size = " + popSize + "\n";
		name += "Generations = " + generations + "\n";
		name += "Mut. rate = " + mutRate + "\n";
		name += "Kill rate = " + killRate + "\n";
		name += "State evaluator = " + evaluator.title() + "\n";
		
		return name;
	}

	@Override
	public String title() {
		return "Nested Evolution";
	}

}
