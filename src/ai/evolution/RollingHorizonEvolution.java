package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.HaMap;
import action.Action;
import ai.AI;
import ai.evaluation.IStateEvaluator;

public class RollingHorizonEvolution implements AI {

	public int popSize;
	public int budget;
	public double killRate;
	public double mutRate;
	public IStateEvaluator evaluator;
	
	public List<Double> generations;
	public List<Double> bestVisits;
	
	private final List<Genome> pop;
	private List<Action> actions;
	private final Random random;
	

	public RollingHorizonEvolution(int popSize, double mutRate,
			double killRate, int budget, IStateEvaluator evaluator) {
		super();
		this.popSize = popSize;
		this.mutRate = mutRate;
		this.budget = budget;
		this.evaluator = evaluator;
		this.killRate = killRate;
		pop = new ArrayList<Genome>();
		actions = new ArrayList<Action>();
		random = new Random();
		this.generations = new ArrayList<Double>();
		this.bestVisits = new ArrayList<Double>();
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

		Long start = System.currentTimeMillis();
		
		setup(state);

		final List<Genome> killed = new ArrayList<Genome>();
		final GameState clone = new GameState(state.map);
		clone.imitate(state);
		
		int g = 0;
		
		while (System.currentTimeMillis() < start + budget) {

			g++;
			
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
		
		generations.add((double)g);
		bestVisits.add((double)(pop.get(0).visits));

	}

	private void setup(GameState state) {

		pop.clear();
		final GameState clone = new GameState(null);

		for (int i = 0; i < popSize; i++) {
			clone.imitate(state);
			final Genome genome = new WeakGenome();
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
		name += "Budget = " + budget + "ms.\n";
		name += "Mut. rate = " + mutRate + "\n";
		name += "Kill rate = " + killRate + "\n";
		name += "State evaluator = " + evaluator.title() + "\n";
		
		return name;
	}


	@Override
	public String title() {
		return "Rolling Horizon Evolution";
	}

	@Override
	public AI copy() {
		return new RollingHorizonEvolution(popSize, mutRate, killRate, budget, evaluator.copy());
	}

}
