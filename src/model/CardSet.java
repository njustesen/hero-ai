package model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CardSet {
	
	public static Map<Integer, Card> cardIdxs;
	
	static {
		cardIdxs = new HashMap<Integer, Card>();
		for (Card card : Card.values()) 
			cardIdxs.put(card.ordinal(), card);
	}

	public int[] cards;
	public int size;
	public int seed;
	
	public CardSet(){
		cards = new int[cardIdxs.size()];
		size = 0;
	}
	
	public Card determined(){
		int r = (int) (seed % size);
		int c = 0;
		int i = 0;
		while(c < r){
			c += cards[i];
			i++;
		}
		seed = (seed * (seed - 31)) / (seed + 29);
		return cardIdxs.get(i);
	}
	
	public Card random(){
		int r = (int) (Math.random() * size);
		int c = 0;
		int i = 0;
		while(c < r){
			c += cards[i];
			i++;
		}
		return cardIdxs.get(i);
	}

	public void add(Card card) {
		cards[card.ordinal()]++;
		size++;
	}
	
	public void remove(Card card) {
		cards[card.ordinal()]--;
		size++;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		int prime = 7;
		for(int i = 0; i < cards.length; i++)
			hash = hash * prime + cards[i];
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CardSet other = (CardSet) obj;
		if (!Arrays.equals(cards, other.cards))
			return false;
		return true;
	}
	
}
