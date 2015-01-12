package ai.util;

import game.GameState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lib.Card;
import model.Position;
import model.Unit;

import action.Action;
import action.DropAction;

public class ActionPruner {
	
	public void prune(List<Action> actions, GameState state){
		
		List<Action> pruned = new ArrayList<Action>();
		Map<DropAction, List<Position>> spellTargets = new HashMap<DropAction, List<Position>>();
		for(Action action : actions){
			if (action instanceof DropAction){
				DropAction dropAction = ((DropAction)action);
				if (dropAction.type == Card.INFERNO){
					spellTargets.put(((DropAction)action), spellTargets(dropAction.to, state));
				}
			}
		}
		
		for(DropAction spell : spellTargets.keySet())
			if (spellTargets.get(spell).isEmpty() || sameOrBetterSpellEffect(spellTargets, spell))
				pruned.add(spell);
		
		for(Action action : pruned)
			actions.remove(action);
		
	}

	private boolean sameOrBetterSpellEffect(Map<DropAction, List<Position>> spellTargets, DropAction spell) {
		
		for(Action action : spellTargets.keySet()){
			if (action.equals(spell))
				continue;
			for(Position pos : spellTargets.get(spell))
				if (!spellTargets.get(action).contains(pos))
					return true;
		}
		
		return false;
	}

	private List<Position> spellTargets(Position to, GameState state) {
		List<Position> targets = new ArrayList<Position>();
		for(int x = to.x-1; x <= to.x+1; x++){
			for(int y = to.y-1; y <= to.y+1; y++){
				if (x >= 0 && x < state.map.width && y >= 0 && y < state.map.height){
					Unit unit = state.map.squareAt(x, y).unit;
					if (unit != null && unit.p1Owner != state.p1Turn)
						targets.add(new Position(x,y));
				}
			}
		}
		return targets;
	}
	
}
