package ai.heuristic;

import ai.AI;
import game.GameState;

public class RolloutEvaluatior implements IStateEvaluator {

	public int rolls;
	public int depth;	// In turns
	public AI policy;
	public IStateEvaluator evaluator;
	public boolean copy;
	public boolean worst;
	
	public RolloutEvaluatior(int rolls, int depth, AI policy, IStateEvaluator evaluator) {
		super();
		this.rolls = rolls;
		this.depth = depth;
		this.evaluator = evaluator;
		this.policy = policy;
		this.copy = false;
	}
	
	public RolloutEvaluatior(int rolls, int depth, AI policy, IStateEvaluator evaluator, boolean copy) {
		super();
		this.rolls = rolls;
		this.depth = depth;
		this.evaluator = evaluator;
		this.policy = policy;
		this.copy = copy;
		this.worst = false;
	}
	
	public RolloutEvaluatior(int rolls, int depth, AI policy, IStateEvaluator evaluator, boolean copy, boolean worst) {
		super();
		this.rolls = rolls;
		this.depth = depth;
		this.evaluator = evaluator;
		this.policy = policy;
		this.copy = copy;
		this.worst = worst;
	}
	
	@Override
	public double eval(GameState state, boolean p1) {
		
		if (state.isTerminal)
			return evaluator.eval(state, p1);
		
		if (!copy)
			return simulateGame(state, p1);
		
		GameState clone = state.copy();
		double sum = 0;
		double w = 1000000;
		//List<Double> vals = new ArrayList<Double>();
		
		for(int i = 0; i < rolls; i++){
			if (i!=0)
				clone.imitate(state);
			double d = simulateGame(clone, p1);
			//vals.add(d);
			sum += d;
			if (d < w)
				w = d;
		}
		
		//System.out.println(Statistics.avg(vals) + ";" + Statistics.stdDev(vals) + ";");
		if (!worst)
			return sum / rolls;
		return w;
			
	}
	
	private double simulateGame(GameState state, boolean p1) {
		int turns = state.turn;
		while (!state.isTerminal && state.turn < turns + depth)
			state.update(policy.act(state, -1));
		return evaluator.eval(state, p1);
	}

	@Override
	public double normalize(double delta) {
		return evaluator.normalize(delta);
	}

	@Override
	public String title() {
		return "Rollout Evaluator [policy=" + policy.title() + ", evaluator=" + evaluator.title() + "]";
	}

}
