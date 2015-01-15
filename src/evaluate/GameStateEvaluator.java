package evaluate;

import game.GameState;
import lib.Card;
import lib.CardType;
import lib.UnitClassLib;
import model.Square;

public class GameStateEvaluator {

	private static final boolean pos = true;
	
	public GameStateEvaluator() {

	}

	public double eval(GameState state, boolean p1) {

		int hpDif = hpDif(state, p1);
		
		return hpDif;

	}

	private int hpDif(GameState state, boolean p1) {
		int p1Units = 0;
		int p2Units = 0;
		final int m = 0;
		for (int x = 0; x < state.map.width; x++)
			for (int y = 0; y < state.map.height; y++)
				if (state.squares[x][y].unit != null)
					if (state.squares[x][y].unit.p1Owner)
						p1Units += state.squares[x][y].unit.hp
								+ state.squares[x][y].unit.unitClass.maxHP
								+ squareVal(state.squares[x][y]);
					else
						p2Units += state.squares[x][y].unit.hp
								+ state.squares[x][y].unit.unitClass.maxHP
								+ squareVal(state.squares[x][y]);
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

	private int squareVal(Square square) {
		
		switch(square.type){
		case ASSAULT : return 100;
		case DEPLOY_1 : return -75;
		case DEPLOY_2 : return -75;
		case DEFENSE : return 100;
		case POWER : return 100;
		case NONE : return 0;
		}
		
		return 0;
		
	}
}
