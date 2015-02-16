package ai.mcts;

public class UCT implements ITreePolicy {

	private static final double NO_VISIT_VAL = 0.0;
	// public static double C = 1 / Math.sqrt(2);
	public static double C = 0.15 / Math.sqrt(2);

	@Override
	public double urgent(MctsEdge edge, MctsEdge from) {

		if (edge.visits == 0)
			return NO_VISIT_VAL;
		
		if (from == null)
			return edge.avg() + 
				2 * C * Math.sqrt((2 * Math.log(0)) / (edge.visits));
		else	
			return edge.avg() + 
				2 * C * Math.sqrt((2 * Math.log(from.visits)) / (edge.visits));
		
	}

	@Override
	public double best(MctsEdge edge) {

		if (edge.visits == 0)
			return NO_VISIT_VAL;

		return edge.avg();

	}

}
