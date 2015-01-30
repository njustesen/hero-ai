package ai.mcts;

public interface ITreePolicy {

	public double urgent(MctsNode node);
	public double best(MctsNode node);
	
}
