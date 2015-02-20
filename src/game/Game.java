package game;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import model.DECK_SIZE;
import model.HaMap;
import ui.UI;
import util.CachedLines;
import util.MapLoader;
import action.Action;
import action.SingletonAction;
import action.UndoAction;
import ai.AI;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.HeuristicAI;
import ai.NmSearchAI;
import ai.RandomAI;
import ai.RandomHeuristicAI;
import ai.ScanRandomAI;
import ai.evolution.RollingHorizonEvolution;
import ai.heuristic.HeuristicEvaluation;
import ai.heuristic.MaterialBalanceEvaluation;
import ai.heuristic.RolloutEvaluation;
import ai.mcts.Mcts;
import ai.util.ComplexActionComparator;
import ai.util.RAND_METHOD;

public class Game {

	private static final long TIME_LIMIT = -1;
	public GameState state;
	public UI ui;
	public AI player1;
	public AI player2;
	public GameArguments gameArgs;
	private Stack<GameState> history;
	private int lastTurn;
	
	
	public static void main(String[] args) {

		GameArguments gameArgs = new GameArguments(args);
		
		HaMap map;
		try{
			map = MapLoader.get(gameArgs.mapName);
		} catch (IOException e){
			System.out.println("Map not found.");
			return;
		}
		
		GameState state = new GameState(map);
		final Game game = new Game(state, gameArgs);

		try {
			game.run();
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public Game(GameState state, GameArguments gameArgs) {
		this.gameArgs = gameArgs;
		this.player1 = gameArgs.players[0];
		this.player2 = gameArgs.players[1];
		history = new Stack<GameState>();
		if (state != null)
			this.state = state;

		if (gameArgs.gfx)
			this.ui = new UI(this.state, (this.player1 == null),
					(this.player2 == null));

		history = new Stack<GameState>();
		if (CachedLines.posMap.isEmpty())
			CachedLines.load(state.map);

	}

	public void run() {

		state.init(gameArgs.deckSize);
		history.add(state.copy());
		lastTurn = 5;

		if (player1 != null)
			player1.init(state, -1);
		if (player2 != null)
			player2.init(state, -1);

		while (!state.isTerminal) {

			if (ui != null) {
				ui.state = state.copy();
				ui.repaint();
				try {
					Thread.sleep(gameArgs.sleep);
				} catch (InterruptedException e) {
				}
			}
			

			if (state.p1Turn && player1 != null)
				act(player1, player2, state.copy());
			else if (!state.p1Turn && player2 != null)
				act(player2, player1, state.copy());
			else if (ui.action != null) {

				if (ui.action instanceof UndoAction)
					undoAction();
				else
					state.update(ui.action);
				ui.lastAction = ui.action;
				ui.resetActions();

			}

			if (state.APLeft != lastTurn) {
				if (state.APLeft < lastTurn)
					history.add(state.copy());
				lastTurn = state.APLeft;
			}

			if (state.APLeft == 5) {
				history.clear();
				final GameState copy = state.copy();
				history.add(copy);
				lastTurn = 5;

			}

		}
		if (ui != null) {
			ui.state = state.copy();
			ui.repaint();
		}

	}

	private void act(AI player, AI other, GameState copy) {
		Action action = player.act(copy, TIME_LIMIT);
		if (action == null)
			action = SingletonAction.endTurnAction;
		state.update(action);
		if (ui != null)
			ui.lastAction = action;
		if (other == null)
			try {
				Thread.sleep(gameArgs.sleep);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
	}

	private void undoAction() {

		if (state.APLeft == 5)
			return;

		if (state.isTerminal)
			return;

		history.pop();

		state = history.peek().copy();

	}

}
