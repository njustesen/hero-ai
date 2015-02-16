package ai.heuristic;

import java.util.HashMap;
import java.util.Map;

import ai.util.NormUtil;
import game.GameState;
import model.Card;
import model.CardType;

public class MaterialBalanceEvaluation implements IHeuristic {

	private static final double MAX_VAL = 21;
	private static Map<Card, Double> values;
	static {
		values = new HashMap<Card, Double>();
		values.put(Card.ARCHER, 1.1);
		values.put(Card.CLERIC, 1.2);
		values.put(Card.DRAGONSCALE, .3);
		values.put(Card.INFERNO, 0.8);
		values.put(Card.KNIGHT, 1.0);
		values.put(Card.NINJA, 1.5);
		values.put(Card.REVIVE_POTION, .3);
		values.put(Card.RUNEMETAL, .3);
		values.put(Card.SCROLL, .5);
		values.put(Card.SHINING_HELM, .2);
		values.put(Card.WIZARD, 1.1);
	}
	
	public MaterialBalanceEvaluation() {

	}

	public double eval(GameState state, boolean p1) {

		if (state.isTerminal){
			if (state.getWinner() == 0)
				return 0;
			return matDif(state, p1) * 2;
		}
		
		return matDif(state, p1);
		
	}

	private double matDif(GameState state, boolean p1) {
		int p1Units = 0;
		int p1Crystals = 0;
		int p2Units = 0;
		int p2Crystals = 0;
		for (int x = 0; x < state.map.width; x++){
			for (int y = 0; y < state.map.height; y++){
				if (state.units[x][y] != null){
					if (state.units[x][y].p1Owner){
						if (state.units[x][y].unitClass.card == Card.CRYSTAL)
							p1Crystals += state.units[x][y].hp / 1500;
						else
							p1Units += values.get(state.units[x][y].unitClass.card);
					} else {
						if (state.units[x][y].unitClass.card == Card.CRYSTAL)
							p2Crystals += state.units[x][y].hp / 1500;
						else
							p2Units += values.get(state.units[x][y].unitClass.card);
					}
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

		//double p1Val = (double)p1Units * (double)p1Crystals * 0.2;
		//double p2Val = (double)p2Units * (double)p2Crystals * 0.2;
		double p1Val = (double)p1Units;
		double p2Val = (double)p2Units;
		
		if (p1)
			return p1Val - p2Val;
		return p2Val - p1Val;
	}

	@Override
	public double normalize(double delta) {
		return NormUtil.normalize(delta, -MAX_VAL, MAX_VAL, 1, 0);
	}

}
