package ai.heuristic;

import java.util.HashMap;
import java.util.Map;

import game.GameState;
import lib.Card;
import lib.CardType;
import lib.UnitClassLib;
import model.SquareType;

public class MaterialEvaluation implements IHeuristic {

	private static Map<Card, Double> values;
	static {
		values = new HashMap<Card, Double>();
		values.put(Card.ARCHER, 1.1);
		values.put(Card.CLERIC, 1.2);
		values.put(Card.CRYSTAL, 4.0);
		values.put(Card.DRAGONSCALE, .3);
		values.put(Card.INFERNO, .5);
		values.put(Card.KNIGHT, 1.0);
		values.put(Card.NINJA, 1.5);
		values.put(Card.REVIVE_POTION, .3);
		values.put(Card.RUNEMETAL, .3);
		values.put(Card.SCROLL, .3);
		values.put(Card.SHINING_HELM, .2);
		values.put(Card.WIZARD, 1.1);
	}
	
	public MaterialEvaluation() {

	}

	public double eval(GameState state, boolean p1) {

		int matDif = matDif(state, p1);
		
		return matDif;

	}

	private int matDif(GameState state, boolean p1) {
		int p1Units = 0;
		int p2Units = 0;
		for (int x = 0; x < state.map.width; x++){
			for (int y = 0; y < state.map.height; y++){
				if (state.units[x][y] != null){
					if (state.units[x][y].p1Owner)
						p1Units += values.get(state.units[x][y].unitClass.card);
					else
						p2Units += values.get(state.units[x][y].unitClass.card);
				}
			}
		}
		// TODO: Opponent hand should be hidden
		for (final Card card : state.p1Deck)
			if (card.type == CardType.UNIT)
				p1Units += values.get(card);
		for (final Card card : state.p1Hand)
			if (card.type == CardType.UNIT)
				p1Units += values.get(card);
		for (final Card card : state.p2Hand)
			if (card.type == CardType.UNIT)
				p2Units += values.get(card);
		for (final Card card : state.p2Deck)
			if (card.type == CardType.UNIT)
				p2Units += values.get(card);

		if (p1)
			return p1Units - p2Units;
		return p2Units - p1Units;
	}

}
