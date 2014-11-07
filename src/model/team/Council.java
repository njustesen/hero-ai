package model.team;

import java.util.ArrayList;
import java.util.List;

import lib.Card;

public class Council {

	public static List<Card> deck;
	static {
		deck = new ArrayList<Card>();
		
		deck.add(Card.KNIGHT);
		
		deck.add(Card.KNIGHT);
		deck.add(Card.KNIGHT);
		
		deck.add(Card.ARCHER);
		deck.add(Card.ARCHER);
		deck.add(Card.ARCHER);
		
		deck.add(Card.CLERIC);
		deck.add(Card.CLERIC);
		deck.add(Card.CLERIC);
		
		//for(int i = 0; i < 100; i++)
			deck.add(Card.WIZARD);
		deck.add(Card.WIZARD);
		deck.add(Card.WIZARD);
		
		deck.add(Card.NINJA);
		
		deck.add(Card.INFERNO);
		deck.add(Card.INFERNO);

		//for(int i = 0; i < 10; i++)
			deck.add(Card.INFERNO);

		deck.add(Card.RUNEMETAL);
		deck.add(Card.RUNEMETAL);
		deck.add(Card.RUNEMETAL);
		
		deck.add(Card.DRAGONSCALE);
		deck.add(Card.DRAGONSCALE);
		deck.add(Card.DRAGONSCALE);

		deck.add(Card.SHINING_HELM);
		deck.add(Card.SHINING_HELM);
		deck.add(Card.SHINING_HELM);
		
		//for(int i = 0; i < 6; i++)
		deck.add(Card.REVIVE_POTION);
		deck.add(Card.REVIVE_POTION);
		
		deck.add(Card.SCROLL);
		deck.add(Card.SCROLL);
		
	}
	
}
