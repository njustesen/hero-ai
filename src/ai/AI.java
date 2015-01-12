package ai;

import game.GameState;
import action.Action;

public interface AI {

	public Action act(GameState state, long ms);

	public Action init(GameState state, long ms);

}
