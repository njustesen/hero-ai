package lib;

import java.util.HashMap;

import model.Attack;
import model.AttackType;
import model.GameObjectType;
import model.Heal;
import model.UnitClass;


public class UnitClassLib {

	public static HashMap<GameObjectType, UnitClass> lib = new HashMap<GameObjectType, UnitClass>();
	
	static {
		
		// Add units
		lib.put(GameObjectType.Knight, new UnitClass(GameObjectType.Knight, (short)1000, (byte)2, (byte)20, (byte)0, null, null, false));
		lib.put(GameObjectType.Archer, new UnitClass(GameObjectType.Archer, (short)800,  (byte)2, (byte)0,  (byte)0, null, null, false));
		lib.put(GameObjectType.Cleric, new UnitClass(GameObjectType.Cleric, (short)800,  (byte)2, (byte)0,  (byte)0, null, null, false));
		lib.put(GameObjectType.Wizard, new UnitClass(GameObjectType.Wizard, (short)800,  (byte)2, (byte)0,  (byte)10, null, null, false));
		lib.put(GameObjectType.Ninja,  new UnitClass(GameObjectType.Ninja, (short)800,  (byte)3, (byte)0,  (byte)0, null, null, false));
		
		// Add attacks
		lib.get(GameObjectType.Knight).attack = new Attack((byte)1,AttackType.Physical,(short)200,1,1,false,true);
		lib.get(GameObjectType.Archer).attack = new Attack((byte)3,AttackType.Physical,(short)300,0.5,1,false,false);
		lib.get(GameObjectType.Cleric).attack = new Attack((byte)2,AttackType.Magical,(short)200,1,1,false,false);
		lib.get(GameObjectType.Wizard).attack = new Attack((byte)2,AttackType.Magical,(short)200,1,1,true,false);
		lib.get(GameObjectType.Ninja).attack = new Attack((byte)1,AttackType.Physical,(short)200,2,1,false,false);
		
		// Add heal
		lib.get(GameObjectType.Cleric).heal = new Heal((byte)2, (short)600, (short)400);
		
	}
	
}
