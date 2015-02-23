package ai.heuristic;

import ai.util.NormUtil;
import game.GameState;
import lib.UnitClassLib;
import model.Card;
import model.CardType;
import model.SquareType;

public class HeuristicEvaluation implements IHeuristic {

	private static final double MAX_VAL = 20000;
	private boolean winVal;
	
	public HeuristicEvaluation(boolean winVal) {
		this.winVal = winVal;
	}

	public double eval(GameState state, boolean p1) {

		int hpDif = hpDif(state, p1);
		
		if (!winVal)
			return hpDif;
		
		if (hpDif == 0)
			return 0.5;
		else if (hpDif > 0)
			return 1;
		
		return 0;

	}

	private int hpDif(GameState state, boolean p1) {
		int p1Units = 0;
		int p2Units = 0;
		int up = 0;
		for (int x = 0; x < state.map.width; x++){
			for (int y = 0; y < state.map.height; y++){
				if (state.units[x][y] != null){
					up = 1;
					if (state.units[x][y].hp <= 0)
						up = 0;
					if (state.units[x][y].p1Owner)
						p1Units += state.units[x][y].hp
								+ state.units[x][y].unitClass.maxHP * (1 + up)
								+ squareVal(state.map.squares[x][y]) * up;
					else
						p2Units += state.units[x][y].hp
								+ state.units[x][y].unitClass.maxHP * (1 + up)
								+ squareVal(state.map.squares[x][y]) * up;
				}
			}
		}
		// TODO: Opponent hand should be hidden
		for (final Card card : state.p1Deck)
			if (card.type == CardType.UNIT)
				p1Units += UnitClassLib.lib.get(card).maxHP * 1.75;
		for (final Card card : state.p1Hand)
			if (card.type == CardType.UNIT)
				p1Units += UnitClassLib.lib.get(card).maxHP * 1.75;
		for (final Card card : state.p2Hand)
			if (card.type == CardType.UNIT)
				p2Units += UnitClassLib.lib.get(card).maxHP * 1.75;
		for (final Card card : state.p2Deck)
			if (card.type == CardType.UNIT)
				p2Units += UnitClassLib.lib.get(card).maxHP * 1.75;

		if (p1)
			return p1Units - p2Units;
		return p2Units - p1Units;
	}

	private int squareVal(SquareType square) {
		
		switch(square){
		case ASSAULT : return 100;
		case DEPLOY_1 : return -75;
		case DEPLOY_2 : return -75;
		case DEFENSE : return 100;
		case POWER : return 100;
		case NONE : return 0;
		}
		
		return 0;
		
	}

	@Override
	public double normalize(double delta) {
		return NormUtil.normalize(delta, -MAX_VAL, MAX_VAL, 1, 0);
	}
}
