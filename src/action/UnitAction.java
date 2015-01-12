package action;

import model.Position;

public class UnitAction extends Action {

	public Position from;
	public Position to;
	public UnitActionType type;

	public UnitAction(Position from, Position to, UnitActionType type) {
		super();
		this.from = from;
		this.to = to;
		this.type = type;
	}

	@Override
	public String toString() {
		return "UnitAction [from=" + from + ", to=" + to + "]";
	}

}
