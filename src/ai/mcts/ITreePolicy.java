package ai.mcts;

public interface ITreePolicy {

	public double urgent(MctsEdge node, MctsEdge parent);

	public double best(MctsEdge node);

}
