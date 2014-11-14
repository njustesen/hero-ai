package lib;


import java.util.HashMap;

import model.Attack;
import model.AttackType;
import model.Heal;

public class UnitClassLib {

	public static HashMap<Card, UnitClass> lib = new HashMap<Card, UnitClass>();
	
	static {
		
		// Add units
		lib.put(Card.KNIGHT, new UnitClass(Card.KNIGHT, (short)1000, (byte)2, (short)200, (byte)20, (byte)0, null, null, false));
		lib.put(Card.ARCHER, new UnitClass(Card.ARCHER, (short)800,  (byte)2, (short)300, (byte)0,  (byte)0, null, null, false));
		lib.put(Card.CLERIC, new UnitClass(Card.CLERIC, (short)800,  (byte)2, (short)200, (byte)0,  (byte)0, null, null, false));
		lib.put(Card.WIZARD, new UnitClass(Card.WIZARD, (short)800,  (byte)2, (short)200, (byte)0,  (byte)10, null, null, false));
		lib.put(Card.NINJA,  new UnitClass(Card.NINJA, (short)800,  (byte)3, (short)200, (byte)0,  (byte)0, null, null, false));
		
		// Add crystal
		lib.put(Card.CRYSTAL, new UnitClass(Card.CRYSTAL, (short)4500, (byte)0, (short)0, (byte)0, (byte)0, null, null, false));
		
		// Add attacks
		lib.get(Card.KNIGHT).attack = new Attack((byte)1,AttackType.Physical,(short)200,1,1,false,true);
		lib.get(Card.ARCHER).attack = new Attack((byte)3,AttackType.Physical,(short)300,0.5,1,false,false);
		lib.get(Card.CLERIC).attack = new Attack((byte)2,AttackType.Magical,(short)200,1,1,false,false);
		lib.get(Card.WIZARD).attack = new Attack((byte)2,AttackType.Magical,(short)200,1,1,true,false);
		lib.get(Card.NINJA).attack = new Attack((byte)1,AttackType.Physical,(short)200,2,1,false,false);
		
		// Add heal
		lib.get(Card.CLERIC).heal = new Heal((byte)2, (short)3, (short)2);
		lib.get(Card.NINJA).swap = true;
		
	}
	
}
