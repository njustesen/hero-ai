package ai;

import game.GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lib.Card;
import lib.CardType;
import model.Position;
import model.SquareType;
import action.Action;
import action.DropAction;
import action.SingletonAction;
import action.UnitAction;
import action.UnitActionType;

public class ScanRandomAI implements AI {

	private static final double PROP_HAND = 0.25;
	private static final double SWAP_PROP = 0.05;
	private static final double HEAL_PROP = 0.60;
	public boolean p1;
	private final List<Position> myUnits;
	private final List<Position> enemyUnits;
	private final List<Position> emptySpaces;
	private final List<Integer> handOrder;

	public ScanRandomAI(boolean p1) {
		this.p1 = p1;
		handOrder = new ArrayList<Integer>();
		handOrder.add(0);
		handOrder.add(1);
		handOrder.add(2);
		handOrder.add(3);
		handOrder.add(4);
		handOrder.add(5);
		myUnits = new ArrayList<Position>();
		enemyUnits = new ArrayList<Position>();
		emptySpaces = new ArrayList<Position>();
	}

	@Override
	public Action act(GameState state, long ms) {
		// long aiStart = System.nanoTime();

		if (state.APLeft <= 0)
			return SingletonAction.endTurnAction;

		locateUnits(state);

		Collections.shuffle(myUnits);
		Collections.shuffle(enemyUnits);
		Collections.shuffle(emptySpaces);

		Action action = null;
		final double rand = Math.random();
		if (rand < PROP_HAND) {
			action = handAction(state);
			if (action == null)
				action = unitAction(state);
		}
		if (action == null) {
			action = unitAction(state);
			if (action == null)
				action = handAction(state);
		}

		if (action == null)
			return SingletonAction.endTurnAction;

		return action;
	}

	private Action unitAction(GameState state) {

		Action action = null;

		for (final Position pos : myUnits) {

			if (state.squares[pos.x][pos.y].unit.hp <= 0)
				continue;

			final List<UnitActionType> types = new ArrayList<UnitActionType>();

			if (state.squares[pos.x][pos.y].unit.unitClass.swap)
				types.add(UnitActionType.SWAP);

			if (state.squares[pos.x][pos.y].unit.unitClass.heal != null)
				types.add(UnitActionType.HEAL);

			if (state.squares[pos.x][pos.y].unit.unitClass.speed > 0)
				types.add(UnitActionType.MOVE);

			if (state.squares[pos.x][pos.y].unit.unitClass.attack != null)
				types.add(UnitActionType.ATTACK);

			Collections.shuffle(types);

			for (final UnitActionType type : types) {
				switch (type) {
				case MOVE:
					action = moveAction(state, pos);
					break;
				case ATTACK:
					action = attackAction(state, pos);
					break;
				case HEAL:
					action = healAction(state, pos);
					break;
				case SWAP:
					action = swapAction(state, pos);
					break;
				}
				if (action != null)
					break;
			}

			if (action != null)
				return action;

		}

		return action;
	}

	private Action swapAction(GameState state, Position pos) {

		for (final Position other : myUnits) {

			if (other.equals(pos)
					|| state.squares[other.x][other.y].unit.hp <= 0)
				continue;

			return new UnitAction(pos, other, UnitActionType.SWAP);

		}

		return null;
	}

	private Action healAction(GameState state, Position pos) {

		for (final Position other : myUnits) {

			if (other.equals(pos)
					|| state.squares[other.x][other.y].unit.fullHealth())
				continue;

			if (state.squares[other.x][other.y].unit.unitClass.card == Card.CRYSTAL)
				continue;

			if (pos.distance(other) > state.squares[pos.x][pos.y].unit.unitClass.heal.range)
				continue;

			return new UnitAction(pos, other, UnitActionType.HEAL);

		}

		return null;
	}

	private Action attackAction(GameState state, Position pos) {

		if (state.squares[pos.x][pos.y].unit.unitClass.attack == null)
			return null;

		for (final Position other : enemyUnits) {

			final int distance = pos.distance(other);

			if (state.squares[other.x][other.y].unit.hp <= 0
					&& distance > state.squares[pos.x][pos.y].unit.unitClass.speed)
				continue;

			if (state.squares[other.x][other.y].unit.hp > 0
					&& distance > state.squares[pos.x][pos.y].unit.unitClass.attack.range)
				continue;

			if (distance > 1 && state.losBlocked(p1, pos, other))
				continue;

			return new UnitAction(pos, other, UnitActionType.ATTACK);

		}

		return null;
	}

	private Action moveAction(GameState state, Position pos) {

		for (final Position empty : emptySpaces) {

			if (pos.distance(empty) > state.squares[pos.x][pos.y].unit.unitClass.speed)
				continue;

			if (!state.squares[pos.x][pos.y].unit.p1Owner
					&& state.squares[empty.x][empty.y].type == SquareType.DEPLOY_1)
				continue;

			if (state.squares[pos.x][pos.y].unit.p1Owner
					&& state.squares[empty.x][empty.y].type == SquareType.DEPLOY_2)
				continue;

			return new UnitAction(pos, empty, UnitActionType.MOVE);

		}

		return null;
	}

	private Action handAction(GameState state) {

		Collections.shuffle(handOrder);
		Action action = null;

		for (final Integer i : handOrder) {
			if (i >= state.currentHand().size())
				continue;
			final Card card = state.currentHand().get(i);

			final double rand = Math.random();
			if (!state.currentDeck().isEmpty() && rand <= SWAP_PROP)
				action = SingletonAction.swapActions.get(card);
			else {
				if (card.type == CardType.ITEM)
					action = dropItemAction(state, card);
				if (card.type == CardType.UNIT)
					action = dropUnitAction(state, card);
				if (card.type == CardType.SPELL)
					action = dropSpellAction(state, card);
			}
			if (action != null)
				return action;
		}

		return null;
	}

	private Action dropSpellAction(GameState state, Card card) {

		for (final Position pos : enemyUnits)
			return new DropAction(card, pos);
		// TODO: also around unit

		return null;
	}

	private Action dropUnitAction(GameState state, Card card) {

		if (state.p1Turn)
			for (final Position pos : state.map.p1DeploySquares)
				if (state.squares[pos.x][pos.y].unit == null
						|| state.squares[pos.x][pos.y].unit.hp <= 0)
					return new DropAction(card, pos);
		if (!state.p1Turn)
			for (final Position pos : state.map.p2DeploySquares)
				if (state.squares[pos.x][pos.y].unit == null
						|| state.squares[pos.x][pos.y].unit.hp <= 0)
					return new DropAction(card, pos);

		return null;
	}

	private Action dropItemAction(GameState state, Card card) {

		for (final Position pos : myUnits) {

			if (state.squares[pos.x][pos.y].unit.unitClass.card == Card.CRYSTAL)
				continue;

			if (card == Card.REVIVE_POTION
					&& state.squares[pos.x][pos.y].unit.fullHealth())
				continue;

			if (card != Card.REVIVE_POTION
					&& state.squares[pos.x][pos.y].unit.hp <= 0
					|| state.squares[pos.x][pos.y].unit.equipment
							.contains(card))
				continue;

			return new DropAction(card, pos);

		}

		return null;
	}

	private void locateUnits(GameState state) {

		emptySpaces.clear();
		myUnits.clear();
		enemyUnits.clear();

		for (int x = 0; x < state.map.width; x++)
			for (int y = 0; y < state.map.height; y++)
				if (state.squares[x][y].unit != null) {
					if (state.squares[x][y].unit.p1Owner == p1)
						myUnits.add(new Position(x, y));
					else
						enemyUnits.add(new Position(x, y));
				} else
					emptySpaces.add(new Position(x, y));
	}

	@Override
	public Action init(GameState state, long ms) {
		// TODO Auto-generated method stub
		return null;
	}

}
