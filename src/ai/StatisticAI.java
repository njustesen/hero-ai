package ai;

import java.util.ArrayList;
import java.util.List;

import game.GameState;
import action.Action;
import ai.util.AiStatistics;

public class StatisticAI implements AI {
	
	public AI ai;
	public AiStatistics aiStatistics;
	public List<Double> times;
	
	private Action action;
	private Class<AI> aiClass;
	
	@SuppressWarnings("unchecked")
	public StatisticAI(AI ai) {
		super();
		this.aiStatistics = new AiStatistics();
		this.times = new ArrayList<Double>();
		this.ai = ai;
		this.aiClass = (Class<AI>) ai.getClass();
	}

	public Action act(GameState state, long ms){
		Long start = System.currentTimeMillis();
		action = ai.act(state, ms);
		times.add((double)(System.currentTimeMillis() - start));
		if (aiClass.equals(GreedyTurnAI.class))
			aiStatistics.statsLists.put("moves", ((GreedyTurnAI)ai).moves);
		return action;
	}
	
	public void init(GameState state, long ms){
		ai.init(state, ms);
	}
	
	public String header(){
		return ai.header();
	}
	
	public String title(){
		return ai.title();
	}

	@Override
	public String toString() {
		return aiStatistics.toString();
	}
	
}
