package ai.mcts;

public interface ITreePolicy {

	public double urgent(MctsNode node, MctsNode parent);

	public double best(MctsNode node);

}
