package ai;

import game.GameState;
import action.Action;

public interface AI {

	public Action act(GameState state, long ms);

	public void init(GameState state, long ms);
	
	public String header();
	
	public String title();

}
