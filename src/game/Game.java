package game;

import java.util.Stack;

import model.HAMap;

import action.Action;
import action.EndTurnAction;
import action.UndoAction;
import ai.GreedyActionAI;
import ai.GreedyTurnAI;
import ai.NmSearchAI;
import ai.RandomAI;
import ai.ScanRandomAI;
import ai.util.RAND_METHOD;
import ui.UI;

public class Game {

	private static final long TIME_LIMIT = 3000;
	private static final long ANIMATION = 1000;
	private static int SLEEP = 20;
	private static boolean GFX = true;
	public GameState state;
	public UI ui;
	public AI player1;
	public AI player2;
	private Stack<GameState> history;
	private int lastTurn;
	
	public static void main(String [ ] args)
	{
		
		AI[] players = new AI[2];
		int p = -1;
		for(int a = 0; a < args.length; a++){
			if (args[a].toLowerCase().equals("p1")){
				p = 0;
				continue;
			} else if (args[a].toLowerCase().equals("p2")){
				p = 1;
				continue;
			}
			if (p == 0 || p==1){
				if (args[a].toLowerCase().equals("human")){
					players[p] = null;
				} else if (args[a].toLowerCase().equals("random")){
					players[p] = new RandomAI((p==0), RAND_METHOD.TREE);
				} else if (args[a].toLowerCase().equals("scanrandom")){
					players[p] = new ScanRandomAI((p==0));
				} else if (args[a].toLowerCase().equals("nmsearch")){
					a++;
					int n = Integer.parseInt(args[a]);
					a++;
					int m = Integer.parseInt(args[a]);
					players[p] = new NmSearchAI((p==0), n, m);
				} if (args[a].toLowerCase().equals("greedyaction")){
					players[p] = new GreedyActionAI();
				} if (args[a].toLowerCase().equals("greedyturn")){
					players[p] = new GreedyTurnAI();
				}
				p = -1;
			} else if (args[a].toLowerCase().equals("sleep")){
				a++;
				SLEEP = Integer.parseInt(args[a]);
				continue;
			} else if (args[a].toLowerCase().equals("gfx")){
				a++;
				GFX = Boolean.parseBoolean(args[a]);
				continue;
			}
		}
		
		Game game = new Game(null, GFX, players[0], players[1]);
		//Game game = new Game(null, true, new EvaAI(true,1000,1000), null);
		
		game.run();
		
		//Game game = new Game(null, false, new RandomAI(true), new RandomAI(false));
		//experiment(game, 100);
		//System.out.println(ImageLib.lib.get("crystal-1").getHeight());
		
	}

	public Game(GameState state, boolean ui, AI player1, AI player2){
		this.player1 = player1;
		this.player2 = player2;
		this.history = new Stack<GameState>();
		if (state != null)
			this.state = state;
		else 
			this.state = new GameState(HAMap.getMap());
		
		if (ui)
			this.ui = new UI(this.state, (this.player1==null), (this.player2==null));
		
		history = new Stack<GameState>();
		
	}
	
	public void run(){
		
		int turnLimit = 50000;
		
		history.add(state.copy());
		lastTurn = 5;
		
		while(!state.isTerminal && state.turn < turnLimit){
			
			if (SLEEP >= 20 && ui != null){
				ui.state = state.copy();
				ui.repaint();
				try {
					Thread.sleep(SLEEP);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if (state.p1Turn && player1 != null) {
				System.out.println("PLAYER 1 TURN");
				act(player1, player2, state.copy());
			} else if (!state.p1Turn && player2 != null){
				System.out.println("PLAYER 2 TURN");
				act(player2, player1, state.copy());
			} else {
				
				if (ui.action != null){
					
					if (ui.action instanceof UndoAction){
						undoAction();
					} else {
						state.update(ui.action);
					}
					ui.lastAction = ui.action;
					ui.resetActions();
					
				}
				
			}
			
			if (state.APLeft != lastTurn){
				if (state.APLeft < lastTurn){
					history.add(state.copy());
				}
				lastTurn = state.APLeft;
			}
			
			if (state.APLeft == 5){
				history.clear();
				history.add(state.copy());
				lastTurn = 5;
			}
			
		}
		if (ui != null){
			ui.state = state.copy();
			ui.repaint();
		}
		
	}

	private void act(AI p1, AI p2, GameState copy) {
		Action action = p1.act(copy, TIME_LIMIT);
		if (action == null)
			action = new EndTurnAction();
		state.update(action);
		if (ui != null)
			ui.lastAction = action;
		if (p2 == null){
			try {
				Thread.sleep(ANIMATION);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
