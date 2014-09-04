package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {

	public HAMap map;
	public boolean p1Turn;
	public short turn;
	public byte APLeft;
	public Map<Position, GameObject> objects;
	public List<GameObjectType> p1Deck;
	public List<GameObjectType> p2Deck;
	public List<GameObjectType> p1Hand;
	public List<GameObjectType> p2Hand;
	
	public GameState(HAMap map) {
		super();
		this.map = map;
		this.p1Turn = true;
		this.turn = 1;
		APLeft = 5;
		this.objects = new HashMap<Position, GameObject>();
		setupCrystals(map);
		dealCards();
	}
	
	public GameState(HAMap map, boolean p1Turn, short turn, byte APLeft,
			Map<Position, GameObject> objects, List<GameObjectType> p1Hand,
			List<GameObjectType> p2Hand) {
		super();
		this.map = map;
		this.p1Turn = p1Turn;
		this.turn = turn;
		this.APLeft = APLeft;
		this.objects = objects;
		this.p1Hand = p1Hand;
		this.p1Hand = p2Hand;
	}
	
	private void setupCrystals(HAMap map) {
		
		for(byte x = 0; x < map.width; x++){
			for(byte y = 0; y < map.height; y++){
				if(map.squareAt(x, y) == Square.P1CRYSTAL)
					objects.put(new Position(x,y), new Crystal(true));
				else if(map.squareAt(x, y) == Square.P2CRYSTAL)
					objects.put(new Position(x,y), new Crystal(false));
			}
		}
		
	}
	
	private void dealCards() {
		
		p1Deck = new ArrayList<GameObjectType>();
		for (GameObjectType type : Council.deck)
			p1Deck.add(type);
		p2Deck = new ArrayList<GameObjectType>();
		for (GameObjectType type : Council.deck)
			p2Deck.add(type);
		
		p1Hand = drawHandFrom(p1Deck);
		p2Hand = drawHandFrom(p2Deck);
		
	}

	private List<GameObjectType> drawHandFrom(List<GameObjectType> deck) {
		
		List<GameObjectType> hand = new ArrayList<GameObjectType>();
		
		while(deck.size() > 0 && hand.size() < 6){
			int idx = (int) (Math.random() * deck.size());
			GameObjectType card = deck.get(idx);
			deck.remove(idx);
			hand.add(card);
		}
		
		return hand;
	}
	
}
