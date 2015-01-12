package evaluate;

import game.GameState;
import lib.Card;
import lib.CardType;
import lib.UnitClassLib;

public class GameStateEvaluator {

	public GameStateEvaluator() {

	}

	public double eval(GameState state, boolean p1) {

		/*
		 * int p1CrystalHP = 0; int p2CrystalHP = 0; for(Position pos :
		 * state.map.p1Crystals){ if (state.squares[pos.x][pos.y].unit != null
		 * && state.squares[pos.x][pos.y].unit.unitClass.card == Card.CRYSTAL){
		 * p1CrystalHP += state.squares[pos.x][pos.y].unit.hp; } } for(Position
		 * pos : state.map.p2Crystals){ if (state.squares[pos.x][pos.y].unit !=
		 * null && state.squares[pos.x][pos.y].unit.unitClass.card ==
		 * Card.CRYSTAL){ p2CrystalHP += state.squares[pos.x][pos.y].unit.hp; }
		 * }
		 */

		int p1Units = 0;
		int p2Units = 0;
		for (int x = 0; x < state.map.width; x++) {
			for (int y = 0; y < state.map.height; y++) {
				if (state.squares[x][y].unit != null) {
					int m = 0;
					if (state.squares[x][y].unit.hp != 0)
						m = state.squares[x][y].unit.unitClass.maxHP;
					if (state.squares[x][y].unit.p1Owner)
						p1Units += state.squares[x][y].unit.hp + m;
					else
						p2Units += state.squares[x][y].unit.hp + m;
				}
			}
		}
		// TODO: Opponent hand should be hidden
		for (final Card card : state.p1Deck) {
			if (card.type == CardType.UNIT) {
				p1Units += UnitClassLib.lib.get(card).maxHP * 1.75;
			}
		}
		for (final Card card : state.p1Hand) {
			if (card.type == CardType.UNIT) {
				p1Units += UnitClassLib.lib.get(card).maxHP * 1.75;
			}
		}
		for (final Card card : state.p2Hand) {
			if (card.type == CardType.UNIT) {
				p2Units += UnitClassLib.lib.get(card).maxHP * 1.75;
			}
		}
		for (final Card card : state.p2Deck) {
			if (card.type == CardType.UNIT) {
				p2Units += UnitClassLib.lib.get(card).maxHP * 1.75;
			}
		}

		if (p1)
			return p1Units - p2Units;
		return p2Units - p1Units;

	}

}
