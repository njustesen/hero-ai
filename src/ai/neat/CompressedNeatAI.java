package ai.neat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Card;
import model.Position;
import game.GameState;
import action.Action;
import ai.AI;
import ai.neat.jneat.NNode;
import ai.neat.jneat.Network;
import ai.util.ActionPruner;

public class CompressedNeatAI extends NeatAI{

	public CompressedNeatAI(Network net) {
		super(net);
	}

	private List<Card> initCards;
	public double[] stateToArray(GameState state) {
		
		if (initCards == null)
			countCards(new GameState(state.map), state.p1Turn);
		
		double[] arr = new double[initCards.size() + 5];
		
		// BASE
		arr[0] = state.APLeft;
		arr[1] = crystalHP(state, state.p1Turn);
		arr[2] = crystalHP(state, !state.p1Turn);
		arr[3] = unitHP(state, state.p1Turn);
		arr[4] = unitHP(state, !state.p1Turn);
		
		Map<Card, Integer> cards = new HashMap<Card, Integer>();
		
		// ON BOARD
		int i = 4;
		Card card;
		int u = 0;
		int uIdx = 0;
		for(int x = 0; x < state.map.width; x++){
			for(int y = 0; y < state.map.height; y++){
				if (state.units[x][y] != null){
					
					u = 0;
					card = state.units[x][y].unitClass.card;
					if (cards.containsKey(card))
						u = cards.get(card);
					cards.put(card, u+1);
					uIdx = nextCardIdx(card, u);
					arr[i + uIdx*3 + 0] = (double)state.units[x][y].hp / (double)state.units[x][y].unitClass.maxHP * 1.5;
					arr[i + uIdx*3 + 1] = (double)x /(double)state.map.width;
					arr[i + uIdx*3 + 2] = (double)y /(double)state.map.height;
					
					for(Card eq : state.units[x][y].equipment){
						if (cards.containsKey(eq))
							u = cards.get(eq);
						cards.put(eq, u+1);
						uIdx = nextCardIdx(eq, u);
						arr[i + uIdx*3 + 0] = 1.0;
						arr[i + uIdx*3 + 1] = (double)x /(double)state.map.width;
						arr[i + uIdx*3 + 2] = (double)y /(double)state.map.height;
					}
					
				}
			}
		}
		
		// IN HAND
		for(Card c : Card.values()){
			for(int ci = 0; ci < state.currentHand().count(c); ci++){
				if (cards.containsKey(c))
					u = cards.get(c);
				cards.put(c, u+1);
				uIdx = nextCardIdx(c, u);
				arr[i + uIdx*3 + 0] = 0;
				arr[i + uIdx*3 + 1] = 0;
				arr[i + uIdx*3 + 2] = 0;
			}
			for(int ci = 0; ci < state.currentDeck().count(c); ci++){
				if (cards.containsKey(c))
					u = cards.get(c);
				cards.put(c, u+1);
				uIdx = nextCardIdx(c, u);
				arr[i + uIdx*3 + 0] = 0;
				arr[i + uIdx*3 + 1] = 0;
				arr[i + uIdx*3 + 2] = 0;
			}
		}
		
		return arr;
	}	

	private int nextCardIdx(Card card, int u) {
		int i = 0;
		int idx = 0;
		for(Card c : initCards){
			if (c.equals(card))
				if (i == u)
					return idx;
				else
					i++;
			idx++;
		}
		System.out.println("Could not find card!");
		return -1;
	}

	private void countCards(GameState initState, boolean p1) {
		
		initCards = new ArrayList<Card>();
		
		for(Card card : Card.values()){
			for(int i = 0; i < initState.p1Deck.count(card); i++)
				initCards.add(card);
			for(int i = 0; i < initState.p1Hand.count(card); i++)
				initCards.add(card);
		}
		
	}


	@Override
	public void init(GameState state, long ms) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String title() {
		return "Compressed NEAT AI";
	}

	@Override
	public AI copy() {
		//return new CompressedNeatAI(net);
		// TODO: clone net
		return null;
	}

}
