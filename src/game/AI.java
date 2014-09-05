package game;

import action.Action;

public interface AI {

	public Action act(GameState state, long ms);
	
}
