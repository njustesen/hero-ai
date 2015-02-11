package ai.mcts;

public class UCT implements ITreePolicy {

	private static final double NO_VISIT_VAL = 0.0;
	// public static double C = 1 / Math.sqrt(2);
	public static double C = 0.15 / Math.sqrt(2);

	@Override
	public double urgent(AbstractMctsNode node, AbstractMctsNode parent) {

		if (node.getVisits() == 0)
			return NO_VISIT_VAL;

		return node.avgValue() + 2 * C
				* Math.sqrt((2 * Math.log(parent.getVisits())) / (node.getVisits()));

	}

	@Override
	public double best(AbstractMctsNode node) {

		if (node.getVisits() == 0)
			return NO_VISIT_VAL;

		return node.avgValue();

	}

}
