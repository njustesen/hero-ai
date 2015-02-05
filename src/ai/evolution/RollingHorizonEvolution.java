package ai.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.HAMap;
import game.GameState;
import action.Action;
import ai.AI;
import ai.heuristic.IHeuristic;

public class RollingHorizonEvolution implements AI {

	public int popSize;
	public int generations;
	public double killRate; 
	public double mutRate;
	public IHeuristic heuristic;
	
	private List<Genome> pop;
	private List<Action> actions;
	private Random random;
	
	public RollingHorizonEvolution(int popSize, double mutRate, double killRate, int generations, IHeuristic heuristic) {
		super();
		this.popSize = popSize;
		this.mutRate = mutRate;
		this.generations = generations;
		this.heuristic = heuristic;
		this.killRate = killRate;
		this.pop = new ArrayList<Genome>();
		this.actions = new ArrayList<Action>();
		this.random = new Random();
	}

	@Override
	public Action act(GameState state, long ms) {
		
		if (actions.isEmpty())
			search(state);
				
		Action next = actions.get(0);
		actions.remove(0);
		return next;
	}


	private void search(GameState state) {

		setup(state);
		
		List<Genome> killed = new ArrayList<Genome>();
		GameState clone = new GameState(HAMap.mapA);
		
		for(int g = 0; g < generations; g++){
			
			System.out.println("Generation=" + g + " Pop size=" + pop.size());
			
			// Test pop
			for (Genome genome : pop){
				System.out.print("|");
				clone.imitate(state);
				clone.update(genome.actions);
				double val = heuristic.eval(clone, state.p1Turn);
				genome.visits++;
				genome.value += val;
			}
			
			// Kill bad genomes
			Collections.sort(pop);
			killed.clear();
			int idx = (int) Math.floor(pop.size() * killRate);
			for(int i = idx; i < pop.size(); i++)
				killed.add(pop.get(i));
			
			if (g != generations){
				// Crossover new ones
				for(Genome genome : killed){
					int a = random.nextInt(idx);
					int b = random.nextInt(idx);
					while(b == a)
						b = random.nextInt(idx);
					if (a == b)
						System.out.println("whuut!");
					clone.imitate(state);
					genome.crossover(pop.get(a), pop.get(b), clone);
					// Mutation
					if (Math.random() < mutRate){
						clone.imitate(state);
						genome.mutate(clone);
					}
					clone.imitate(state);
					if (!genome.isLegal(clone))
						System.out.println("Not legal");
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
		GameState clone = new GameState(HAMap.mapA);
		
		for(int i = 0; i < popSize; i++){
			clone.imitate(state);
			Genome genome = new Genome();
			genome.random(clone);
			pop.add(genome);
		}
		
	}

	@Override
	public Action init(GameState state, long ms) {
		
		
		
		return null;
	}

	
	
}
