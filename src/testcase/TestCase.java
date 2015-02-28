package testcase;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;

import util.Statistics;
import game.Game;
import game.GameArguments;
import game.GameState;
import model.DECK_SIZE;
import model.HaMap;
import ai.AI;
import ai.StatisticAi;
import ai.mcts.MctsNode;

public class TestCase {

	public AI p1;
	public AI p2;
	public int runs;
	public String name;
	public HaMap map;
	public DECK_SIZE deckSize;
	
	public TestCase(AI p1, AI p2, int runs, String name, HaMap map, DECK_SIZE deckSize) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.runs = runs;
		this.name = name;
		this.map = map;
		this.deckSize = deckSize;
	}
	
	public void run(){
		
		String out = "";
		out += "########### TEST ###########\n";
		out += "~~~~~~~~~ Player 1 ~~~~~~~~~\n";
		out += p1.header();
		out += "~~~~~~~~~ Player 2 ~~~~~~~~~\n";
		out += p2.header();
		out += "~~~~~~~~~~~ SETUP ~~~~~~~~~~\n";
		out += "Map: " + map.name + "\n";
		out += "Deck Size: " + deckSize + "\n";
		out += "Randomness: " + GameState.RANDOMNESS + "\n";
		out += "Runs: " + runs + "\n";
		out += "~~~~~~~~~~ RUNNING ~~~~~~~~~\n";
		
		System.out.print(out);
		
		int p1Wins = 0;
		int p2Wins = 0;
		int draws = 0;

		GameState state;
		state = new GameState(map);
		final GameState clone = state.copy();
		final Game game = new Game(state, new GameArguments(false, p1, p2, map.name, deckSize));
		boolean p1Starting;
		for (int i = 0; i < runs; i++) {
			p1Starting = (i == 0 || i < runs / 2.0);
			if (i != 0)
				clone.imitate(state);
			game.state = clone;
			if (p1Starting) {
				game.player1 = p1;
				game.player2 = p2;
			} else {
				game.player1 = p2;
				game.player2 = p1;
			}
			game.run();

			final int winner = clone.getWinner();
			if (winner == 1 && p1Starting || winner == 2 && !p1Starting)
				p1Wins++;
			else if (winner == 2 && p1Starting || winner == 1 && !p1Starting)
				p2Wins++;
			else
				draws++;
			
			System.out.print(winner);
			
		}
		System.out.print("\n");
		
		String res = "";
		res += "~~~~~~~~~~ RESULTS ~~~~~~~~~\n";
		res += "P1 = " + p1Wins + " = " + ((((p1Wins)+draws/2.0)/(double)runs)*100) + "%\n";
		res += "P2 = " + p2Wins + " = " + ((((p2Wins)+draws/2.0)/(double)runs)*100) + "%\n";
		res += "Dr = " + draws + "\n";
		res += "~~~~~~~~~~~ STATS ~~~~~~~~~~\n";
		if (p1 instanceof StatisticAi){
			res += "P1 " + p1.title() + "\n";
			res += ((StatisticAi)p1).toString();
		}
		if (p2 instanceof StatisticAi){
			res += "P2 " + p2.title() + "\n";
			res += ((StatisticAi)p2).toString();
		} 
		res += "############################\n";
		System.out.print(res);
		
		saveToFile(out + res, "tests/"+name+ "-" + System.currentTimeMillis() + ".hares");
		
	}

	private void saveToFile(String output, String filename) {
		PrintWriter out = null; 
		try { 
			out = new PrintWriter(filename);
			out.print(output); 
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		} finally { 
			if (out!= null) 
				out.close(); 
		}
		
	}
	
}
