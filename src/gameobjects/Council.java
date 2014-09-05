package gameobjects;

import java.util.ArrayList;
import java.util.List;

public class Council {

	public static List<GameObjectType> deck;
	static {
		deck = new ArrayList<GameObjectType>();
		
		deck.add(GameObjectType.Knight);
		deck.add(GameObjectType.Knight);
		deck.add(GameObjectType.Knight);
		
		deck.add(GameObjectType.Archer);
		deck.add(GameObjectType.Archer);
		deck.add(GameObjectType.Archer);
		
		deck.add(GameObjectType.Cleric);
		deck.add(GameObjectType.Cleric);
		deck.add(GameObjectType.Cleric);
		
		deck.add(GameObjectType.Wizard);
		deck.add(GameObjectType.Wizard);
		deck.add(GameObjectType.Wizard);
		
		deck.add(GameObjectType.Ninja);
		
		deck.add(GameObjectType.Inferno);
		deck.add(GameObjectType.Inferno);
		
		deck.add(GameObjectType.Runemetal);
		deck.add(GameObjectType.Runemetal);
		deck.add(GameObjectType.Runemetal);
		
		deck.add(GameObjectType.Dragonscale);
		deck.add(GameObjectType.Dragonscale);
		deck.add(GameObjectType.Dragonscale);

		deck.add(GameObjectType.ShiningHelm);
		deck.add(GameObjectType.ShiningHelm);
		deck.add(GameObjectType.ShiningHelm);
		
		deck.add(GameObjectType.RevivePotion);
		deck.add(GameObjectType.RevivePotion);
		
		deck.add(GameObjectType.Scroll);
		deck.add(GameObjectType.Scroll);
		
	}
	
}
