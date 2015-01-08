package ai.util;

import java.util.ArrayList;
import java.util.List;

import evaluate.GameStateEvaluator;

import action.Action;
import action.EndTurnAction;
import game.AI;
import game.GameState;

public class MoveSearch {
	
	class Node {
		public Action action;
		public Node parent;
		public List<Node> children;
		public Node(Action action, Node parent){
			this.action = action;
			this.parent = parent;
			this.children = new ArrayList<Node>();
		}
		public void print(int depth) {
			for(int i = 0; i < depth; i++)
				System.out.print("\t");
			System.out.print(action);
			for(Node node : children)
				node.print(depth++);
		}
		public int size() {
			int i = 1;
			for(Node child : children)
				i += child.size();
			return i;
		}
		public List<List<Action>> moves(int depth) {
			List<List<Action>> moves = new ArrayList<List<Action>>();
			if (children.isEmpty()){
				List<Action> actions = new ArrayList<Action>();
				actions.add(action);
				moves.add(actions);
			} else {
				for(Node child : children){
					List<List<Action>> newMoves = child.moves(depth+1);
					for(List<Action> move : newMoves){
						List<Action> newMove = new ArrayList<Action>();
						newMove.add(action);
						newMove.addAll(move);
						moves.add(newMove);
					}
				}
			}
			return moves;
		}
		public void movesLeaf(List<List<Action>> moves, int depth) {
			if (children.isEmpty()){
				List<Action> actions = new ArrayList<Action>();
				actions.add(action);
				Node p = parent;
				while(p!=null && p.action != null){
					actions.add(p.action);
					p = p.parent;
				}
				moves.add(actions);
			} else {
				for(Node child : children){
					child.movesLeaf(moves,depth+1);
				}
			}
		}
	}
	
	GameStateEvaluator evalutator;
	
	public List<List<Action>> possibleMoves(GameState state) {
		
		List<List<Action>> moves = new ArrayList<List<Action>>();
		evalutator = new GameStateEvaluator();

		Node root = initTree(state, 0);	
		if (root.children.isEmpty()){
			List<Action> actions = new ArrayList<Action>();
			actions.add(new EndTurnAction());
			moves.add(actions);
			return moves;
		}
		
		root.movesLeaf(moves,0);
		
		return moves;
		
	}

	private Node initTree(GameState state, int depth) {
		
		Node root = new Node(null, null);
//		if (depth>=4)
//			return root;
		
		List<Action> possible = state.possibleActions();
		for(Action action : possible){
			if (depth<5){
				GameState next = state.copy();
				next.update(action);
				Node node = initTree(next, depth+1);
				root.children.add(node);
				node.parent = root;
				node.action = action;
			}
		}
		
		return root;
	}
	
}
