package action;

import gameobjects.GameObjectType;
import gameobjects.Position;

public class DropAction extends Action {

	public GameObjectType type;
	public Position to;
	
	public DropAction(GameObjectType type, Position to) {
		super();
		this.type = type;
		this.to = to;
	}
	
}
