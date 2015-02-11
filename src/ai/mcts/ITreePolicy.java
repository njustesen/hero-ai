package ai.mcts;

public interface ITreePolicy {

	public double urgent(AbstractMctsNode node, AbstractMctsNode parent);

	public double best(AbstractMctsNode node);

}
