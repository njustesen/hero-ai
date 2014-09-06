package action;

import lib.Card;
import model.Position;

public class DropAction extends Action {

	public Card type;
	public Position to;
	
	public DropAction(Card type, Position to) {
		super();
		this.type = type;
		this.to = to;
	}

	@Override
	public String toString() {
		return "DropAction [type=" + type + ", to=" + to + "]";
	}
	
}
