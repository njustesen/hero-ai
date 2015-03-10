package ai.evaluation;

import java.util.HashMap;
import java.util.Map;

import ai.util.NormUtil;
import game.GameState;
import lib.UnitClassLib;
import model.Card;
import model.CardType;

public class MaterialBalanceEvaluator implements IStateEvaluator {

	private static int MAX_VAL = 0;
	private static Map<Card, Double> values;
	static {
		values = new HashMap<Card, Double>();
		values.put(Card.ARCHER, 1.1);
		MAX_VAL += 1.1*3;
		values.put(Card.CLERIC, 1.2);
		MAX_VAL += 1.2*3;
		values.put(Card.DRAGONSCALE, .4);
		MAX_VAL += .4*3;
		values.put(Card.INFERNO, 1.2);
		MAX_VAL += 1.2*2;
		values.put(Card.KNIGHT, 1.0);
		MAX_VAL += 1*3;
		values.put(Card.NINJA, 1.5);
		MAX_VAL += 1.5;
		values.put(Card.REVIVE_POTION, .9);
		MAX_VAL += .9*2;
		values.put(Card.RUNEMETAL, .4);
		MAX_VAL += .4*3;
		values.put(Card.SCROLL, .9);
		MAX_VAL += .9*2;
		values.put(Card.SHINING_HELM, .4);
		MAX_VAL += .4*3;
		values.put(Card.WIZARD, 1.1);
		MAX_VAL += 1.1*3;
	}

	private boolean winVal;
	
	public MaterialBalanceEvaluator(boolean winVal) {
		this.winVal = winVal;
	}

	public double eval(GameState state, boolean p1) {

		if (state.isTerminal){
			int winner = state.getWinner();
			if (winner == 0){
				if (winVal)
					return 0.5;
				return 0;
			} else if (winVal){
				if (winner == 1)
					if (p1)
						return 1;
					else
						return 0;
				else
					if (p1)
						return 0;
					else
						return 1;
			}
			return matDif(state, p1) * 2;
		}
		
		if (!winVal)
			return matDif(state, p1);
		
		
		
		double delta = matDif(state, p1);
		
		if (delta == 0)
			return 0.5;
		if (delta > 0)
			return 1;
		
		return 0;
		
	}

	private double matDif(GameState state, boolean p1) {
		double p1Units = 0;
		double p1Crystals = 0;
		double p2Units = 0;
		double p2Crystals = 0;
		for (int x = 0; x < state.map.width; x++){
			for (int y = 0; y < state.map.height; y++){
				if (state.units[x][y] != null){
					if (state.units[x][y].p1Owner){
						if (state.units[x][y].unitClass.card == Card.CRYSTAL)
							p1Crystals += (double)state.units[x][y].hp / (double)(state.units[x][y].unitClass.maxHP * state.map.p1Crystals.size());
						else{
							p1Units += values.get(state.units[x][y].unitClass.card);
							for(Card card : state.units[x][y].equipment)
								p1Units += values.get(card);
						}
					} else {
						if (state.units[x][y].unitClass.card == Card.CRYSTAL)
							p2Crystals += (double)state.units[x][y].hp / (double)(state.units[x][y].unitClass.maxHP * state.map.p1Crystals.size());
						else {
							p2Units += values.get(state.units[x][y].unitClass.card);
							for(Card card : state.units[x][y].equipment)
								p2Units += values.get(card);
						}
					}
				}
			}
		}
		// TODO: Opponent hand should be hidden
		for (final Card card : Card.values()){
			if (card == Card.CRYSTAL)
				continue;
			p1Units += values.get(card) * state.p1Deck.count(card);
			p2Units += values.get(card) * state.p2Deck.count(card);
			p1Units += values.get(card) * state.p1Hand.count(card);
			p2Units += values.get(card) * state.p2Hand.count(card);
		}
		//double p1Val = (double)p1Units * (double)p1Crystals * 0.2;
		//double p2Val = (double)p2Units * (double)p2Crystals * 0.2;
		p1Crystals = MAX_VAL * p1Crystals;
		p2Crystals = MAX_VAL * p2Crystals;
		//double p1Val = Math.min(p1Units, p1Crystals) + Math.max(p1Units, p1Crystals) * (Math.min(p1Units, p1Crystals) / MAX_VAL);
		//double p2Val = Math.min(p2Units, p2Crystals) + Math.max(p2Units, p2Crystals) * (Math.min(p2Units, p2Crystals) / MAX_VAL);
		double p1Val = Math.min(p1Units, p1Crystals) + Math.max(p1Units, p1Crystals);
		double p2Val = Math.min(p2Units, p2Crystals) + Math.max(p2Units, p2Crystals);
		
		if (p1)
			return p1Val - p2Val;
		return p2Val - p1Val;
	}

	@Override
	public double normalize(double delta) {
		return NormUtil.normalize(delta, -MAX_VAL, MAX_VAL, 1, 0);
	}

	@Override
	public String title() {
		return "Material Balance Evaluator";
	}

	@Override
	public IStateEvaluator copy() {
		return new MaterialBalanceEvaluator(winVal);
	}
	
	

}
