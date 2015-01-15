package ai.util;

import game.GameState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lib.Card;
import model.Position;
import model.SquareType;
import model.Unit;
import action.Action;
import action.DropAction;
import action.EndTurnAction;
import action.SwapCardAction;
import action.UnitAction;
import action.UnitActionType;

public class ActionComparator implements Comparator<Action> {

	public GameState state;
	final List<Position> targets = new ArrayList<Position>();

	@Override
	public int compare(Action o1, Action o2) {

		final int val1 = value(o1);
		final int val2 = value(o2);

		if (val1 > val2)
			return -1;
		else if (val2 > val1)
			return 1;
		else if (o1.hashCode() > o2.hashCode())
			return -1;
		else if (o1.hashCode() < o2.hashCode())
			return 1;
		else
			return 0;
	}

	private int value(Action action) {

		if (action instanceof EndTurnAction)
			if (state.APLeft == 0)
				return 10000;
			else
				return 0;

		if (action instanceof SwapCardAction)
			return 0;

		if (action instanceof DropAction) {
			final DropAction drop = ((DropAction) action);
			if (drop.type == Card.INFERNO) {
				targets.clear();
				spellTargets(drop.to, targets);
				int val = -500;
				for (final Position pos : targets)
					if (state.squares[pos.x][pos.y].unit.hp == 0)
						val += state.squares[pos.x][pos.y].unit.maxHP() * 2;
					else
						val += 300;

				return val;
			} else if (drop.type == Card.REVIVE_POTION) {
				if (state.squares[drop.to.x][drop.to.y].unit.hp == 0)
					return 300
							- state.squares[drop.to.x][drop.to.y].unit.maxHP()
							+ state.squares[drop.to.x][drop.to.y].unit.equipment
									.size() * 200;
			} else if (drop.type == Card.SCROLL)
				return state.squares[drop.to.x][drop.to.y].unit.power(state,
						drop.to)
						* 2
						+ state.squares[drop.to.x][drop.to.y].unit.hp;
			else if (drop.type == Card.DRAGONSCALE)
				return state.squares[drop.to.x][drop.to.y].unit.power(state,
						drop.to)
						+ state.squares[drop.to.x][drop.to.y].unit.hp
						* 2;
			else if (drop.type == Card.RUNEMETAL)
				return state.squares[drop.to.x][drop.to.y].unit.power(state,
						drop.to)
						* 2
						+ state.squares[drop.to.x][drop.to.y].unit.hp * 2;
			else if (drop.type == Card.SHINING_HELM)
				return state.squares[drop.to.x][drop.to.y].unit.power(state,
						drop.to) + state.squares[drop.to.x][drop.to.y].unit.hp;
			else
				return 200;
		} else if (action instanceof UnitAction)
			if (((UnitAction) action).type == UnitActionType.ATTACK) {
				final Unit defender = state.squares[((UnitAction) action).to.x][((UnitAction) action).to.y].unit;
				final Unit attacker = state.squares[((UnitAction) action).from.x][((UnitAction) action).from.y].unit;
				if (attacker.unitClass.attack.chain) {
					if (defender.hp == 0)
						return defender.maxHP() * 2 + 200;
					else
						return attacker
								.power(state, ((UnitAction) action).from) + 200;
				} else if (defender.hp == 0)
					return defender.maxHP() * 2;
				else
					return attacker.power(state, ((UnitAction) action).from);
			} else if (((UnitAction) action).type == UnitActionType.HEAL) {
				final Unit target = state.squares[((UnitAction) action).to.x][((UnitAction) action).to.y].unit;
				if (target.hp == 0)
					return 1400;
				else
					return target.maxHP() - target.hp;
			} else if (((UnitAction) action).type == UnitActionType.SWAP)
				return 0;
			else if (((UnitAction) action).type == UnitActionType.MOVE) {
				if (state.squares[((UnitAction) action).to.x][((UnitAction) action).to.y].unit != null)
					return state.squares[((UnitAction) action).to.x][((UnitAction) action).to.y].unit
							.maxHP() * 2;
				if (state.squares[((UnitAction) action).to.x][((UnitAction) action).to.y].type == SquareType.NONE)
					return 0;
				else
					return 30;
			}
		return 0;
	}

	private void spellTargets(Position to, List<Position> spellTargets) {
		for (int x = to.x - 1; x <= to.x + 1; x++)
			for (int y = to.y - 1; y <= to.y + 1; y++)
				if (x >= 0 && x < state.map.width && y >= 0
						&& y < state.map.height) {
					final Unit unit = state.map.squareAt(x, y).unit;
					if (unit != null && unit.p1Owner != state.p1Turn)
						spellTargets.add(new Position(x, y));
				}
	}
}
