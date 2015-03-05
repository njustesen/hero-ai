package ai.evolution;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.evaluation.IStateEvaluator;

public class TieredMaxGenome extends Genome {

	public GameState state;
	public IStateEvaluator rolloutHeuristic;
	public IStateEvaluator heuristic;
	public double killRate;
	public int popSize;
	public double mutRate;
	
	public List<List<Genome>> pops;
	private GameState clone;
	private List<Genome> killed;
	private int idx;
	
	public TieredMaxGenome(int popSize, double killRate, double mutRate, IStateEvaluator rolloutHeuristic, IStateEvaluator heuristic) {
		super();
		this.popSize = popSize;
		this.killRate = killRate;
		this.mutRate = mutRate;
		this.heuristic = heuristic;
		this.rolloutHeuristic = rolloutHeuristic;
		this.idx = popSize - (int) Math.floor(popSize * killRate);
	}
	
	@Override
	public double fitness(){
		return value;
	}

	public void run(){
		
		if (pops == null)
			setupEvo();
		
		if (clone.isTerminal){
			value = heuristic.eval(clone, !state.p1Turn);
			return;
		}

		// Crossover - reuse killed genomes
		if (visits > 0)
			crossover(killed);
		
		// Test
		for (List<Genome> pop : pops){
			for (int i = 0; i < pop.size(); i++) {
				//for(int r = 0; r < pops.indexOf(pop); r++){
					clone.imitate(state);
					clone.update(pop.get(i).actions);
					double val = rolloutHeuristic.eval(clone, !state.p1Turn);
					// MAX of own next move
					pop.get(i).visits++;
					if (pop.get(i).visits == 1 || val > pop.get(i).value){
						pop.get(i).value = val;
				//		break;
					}
				//}
			}
			Collections.sort(pop);
		}
		
		// Advance and kill
		killed.clear();
		for (int p = pops.size()-1; p >= 0; p--){
			if (pops.get(p).size() <= 2)
				continue;
			for (int i = 0; i < pops.get(p).size(); i++) {
				if (i <= (pops.get(p).size() / 2) - 1){
					if (pops.size() == p+1)
						pops.add(new ArrayList<Genome>());
					pops.get(p+1).add(pops.get(p).get(i));
				} else if (p==0){
					killed.add(pops.get(p).get(i));
				} else {
					pops.get(p-1).add(pops.get(p).get(i));
				}
			}
			pops.get(p).clear();
			Collections.sort(pops.get(p));
		}
		
		// MIN of opponent next move
		value = pops.get(pops.size()-1).get(0).value;
				
		
	}
	
	private void crossover(List<Genome> genomes) {
		int pa;
		int pb;
		int a;
		int b;
		for (final Genome genome : killed) {
			pa = random.nextInt(pops.size() - 1) + 1;
			a = random.nextInt(pops.get(pa).size());
			pb = random.nextInt(pops.size() - 1) + 1;
			b = random.nextInt(pops.get(pb).size());
			while (pa==pb && b == a){
				pb = random.nextInt(pops.size() - 1) + 1;
				b = random.nextInt(pops.get(pb).size());
			}
			clone.imitate(state);
			genome.visits = 0;
			genome.crossover(pops.get(pa).get(a), pops.get(pb).get(b), clone);
			
			if (genome.actions.isEmpty())
				continue;
			
			pops.get(0).add(genome);
			
			// Mutation
			if (Math.random() < mutRate) {
				clone.imitate(state);
				genome.mutate(clone);
			}
		}
	}

	private void setupEvo() {
		clone = state.copy();
		pops = new ArrayList<List<Genome>>();
		pops.add(new ArrayList<Genome>());
		killed = new ArrayList<Genome>();
		for(int i = 0; i < popSize; i++){
			if (i > 0)
				clone.imitate(state);
			Genome genome = new MinGenome();
			genome.random(clone);
			pops.get(0).add(genome);
		}
	}

	@Override
	public String toString() {
		return "Genome [value=" + value + ", visits="+ visits + ", fitness=" + fitness() + "]";
	}
	
}
