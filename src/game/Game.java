package game;

import java.io.IOException;
import java.util.Stack;
import model.HaMap;
import ui.UI;
import util.CachedLines;
import util.MapLoader;
import util.pool.ObjectPools;
import action.Action;
import action.SingletonAction;
import action.UndoAction;
import ai.AI;

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
		
		GameState state = ObjectPools.borrowState(map);
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
		this.state = state;

		if (state.map == null){
			try {
				state.map = MapLoader.get(gameArgs.mapName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (gameArgs.gfx)
			this.ui = new UI(this.state, (this.player1 == null),
					(this.player2 == null));

		history = new Stack<GameState>();
		if (CachedLines.posMap.isEmpty() || state.map != CachedLines.map)
			CachedLines.load(state.map);

	}

	public void run() {

		state.init(gameArgs.deckSize);
		GameState initState = ObjectPools.borrowState(state.map);
		initState.imitate(state);
		history.add(initState);
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
				act(player1, player2);
			else if (!state.p1Turn && player2 != null)
				act(player2, player1);
			else if (ui.action != null) {

				if (ui.action instanceof UndoAction)
					undoAction();
				else
					state.update(ui.action);
				ui.lastAction = ui.action;
				ui.resetActions();

			}

			if (state.APLeft != lastTurn) {
				if (state.APLeft < lastTurn){
					GameState clone = ObjectPools.borrowState(state.map);
					clone.imitate(state);
					history.add(clone);
				}
				lastTurn = state.APLeft;
			}

			if (state.APLeft == 5) {
				history.clear();
				GameState clone = ObjectPools.borrowState(state.map);
				clone.imitate(state);
				history.add(clone);
				lastTurn = 5;
			}

		}
		if (ui != null) {
			ui.state = state.copy();
			ui.repaint();
		}

	}

	private void act(AI player, AI other) {
		GameState clone = ObjectPools.borrowState(state.map);
		clone.imitate(state);
		Action action = player.act(clone, TIME_LIMIT);
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
		
		state = ObjectPools.borrowState(state.map);
		state.imitate(history.peek());

	}

}
