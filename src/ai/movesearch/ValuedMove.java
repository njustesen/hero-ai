package ai.movesearch;

import java.util.List;

import action.Action;

public class ValuedMove implements Comparable<ValuedMove>{
	public List<Action> ations;
	public double value;
	public ValuedMove(List<Action> ations, double value) {
		super();
		this.ations = ations;
		this.value = value;
	}
	@Override
	public int compareTo(ValuedMove o) {
		return (int) (value - ((ValuedMove)o).value);
	}
}
