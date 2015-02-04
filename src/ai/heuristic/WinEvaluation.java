package ai.heuristic;

import game.GameState;

public class WinEvaluation implements IHeuristic {
	
	public WinEvaluation() {
		super();
	}
	
	@Override
	public double eval(GameState state, boolean p1) {
		
		if (state.isTerminal){
			int winner = state.getWinner();
			if (winner == 1)
				if (p1)
					return 1;
				else
					return 0;
			else if (winner == 2)
				if (p1)
					return 0;
				else
					return 1;
			else
				return 0;
		}
		
		return 0.5;
		
	}

}
