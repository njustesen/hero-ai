package action;

import lib.Card;

public class SwapCardAction extends Action {

	public Card card;
	
	public SwapCardAction(Card card) {
		super();
		this.card = card;
	}

	@Override
	public String toString() {
		return "SwapCardAction [card=" + card.name() + "]";
	}
	
}
