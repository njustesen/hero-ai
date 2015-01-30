package ai.mcts;

public class UCT implements ITreePolicy {

	private static final double NO_VISIT_VAL = 0.5;
	public static double C = 1 / Math.sqrt(2);
	
	@Override
	public double urgent(MctsNode node) {
		
		if (node.visits == 0)
			return NO_VISIT_VAL;
		
		return node.avgValue() + 2*C*Math.sqrt( (2*Math.log(node.parent.visits)) / (node.visits));
		
	}
	
	@Override
	public double best(MctsNode node) {
		
		if (node.visits == 0)
			return NO_VISIT_VAL;
		
		return node.avgValue();
		
	}

}
