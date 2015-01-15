package ai.util;

import game.GameState;

public interface IHeuristic {

	public double eval(GameState state, boolean p1);
	
}
