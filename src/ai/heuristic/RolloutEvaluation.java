package ai.heuristic;

import ai.AI;
import game.GameState;

public class RolloutEvaluation implements IHeuristic {

	int rolls;
	int depth;	// In turns
	AI policy;
	IHeuristic heuristic;
	boolean copy;
	
	public RolloutEvaluation(int rolls, int depth, AI policy, IHeuristic heuristic, boolean copy) {
		super();
		this.rolls = rolls;
		this.depth = depth;
		this.heuristic = heuristic;
		this.policy = policy;
		this.copy = copy;
	}
	
	@Override
	public double eval(GameState state, boolean p1) {
		
		if (!copy)
			return simulateGame(state, p1);
		
		GameState clone = new GameState(state.map);
		double sum = 0;
		//List<Double> vals = new ArrayList<Double>();
		
		for(int i = 0; i < rolls; i++){
			clone.imitate(state);
			double d = simulateGame(clone, p1);
			//vals.add(d);
			sum += d;
		}
		
		//System.out.println(Statistics.avg(vals) + ";" + Statistics.stdDev(vals) + ";");
		
		return sum / rolls;
	}
	
	private double simulateGame(GameState state, boolean p1) {
		int turns = state.turn;
		while (!state.isTerminal && state.turn < turns + depth)
			state.update(policy.act(state, -1));
		return heuristic.eval(state, p1);
	}

	@Override
	public double normalize(double delta) {
		return heuristic.normalize(delta);
	}

}
