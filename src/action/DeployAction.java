package action;

import gameobjects.GameObjectType;
import gameobjects.Position;

public class DeployAction extends Action {

	public GameObjectType type;
	public Position to;
	
	public DeployAction(GameObjectType type, Position to) {
		super();
		this.type = type;
		this.to = to;
	}
	
}
