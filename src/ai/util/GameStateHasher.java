package ai.util;

import game.GameState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Card;
import model.Unit;

public class GameStateHasher {

	final public StringBuilder sb;
	final public Map<Card, Integer> map;

	public GameStateHasher() {
		super();
		this.sb = new StringBuilder();
		this.map = new HashMap<Card, Integer>();
	}

	public String hash(GameState state) {
		sb.setLength(0);
		sb.append(state.turn).append(state.APLeft).append(state.isTerminal ? "T" : "N");
		hashCards(state.p1Hand, sb);
		hashCards(state.p1Deck, sb);
		hashCards(state.p2Hand, sb);
		hashCards(state.p2Deck, sb);
		
		for (int x = 0; x < state.map.width; x++)
			for (int y = 0; y < state.map.height; y++)
				if (state.units[x][y] != null){
					sb.append("x").append(x).append("y").append(y);
					hashUnit(state.units[x][y], sb);
				}

		return sb.toString();
	}

	private void hashUnit(Unit unit, StringBuilder sb) {
		sb.append(unit.unitClass.card.name().substring(0, 2)).append(unit.hp).append("-").append(unit.p1Owner ? 1 : 2);
		for (final Card card : unit.equipment)
			sb.append(card.name().substring(0, 2));
		sb.append("-");
	}
	
	private void hashCards(List<Card> cards, StringBuilder sb) {
		map.clear();
		for (final Card card : cards)
			if (map.keySet().contains(card))
				map.put(card, map.get(card) + 1);
			else
				map.put(card, 1);
		sb.append("|");
		for (final Card card : map.keySet())
			sb.append(card.name().substring(0, 2)).append(map.get(card));
		sb.append("|");
	}
	
}
