package action;

import gameobjects.Position;

public class UnitAction extends Action  {

	public Position from;
	public Position to;
	
	public UnitAction(Position from, Position to) {
		super();
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		return "UnitAction [from=" + from + ", to=" + to + "]";
	}
	
}
