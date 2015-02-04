package ai.heuristic;

import game.GameState;

public class WinLoseEvaluation implements IHeuristic {
	
	public WinLoseEvaluation() {
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
					return -1;
			else if (winner == 2)
				if (p1)
					return -1;
				else
					return 1;
			else
				return 0;
		}
		
		return 0;
		
	}

}
