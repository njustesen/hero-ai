package ai;

public enum RAND_METHOD {

	BRUTE, 	// Find all possible actions and select one
	TREE, 	// Make tree of action types and pick on type until action found
	SCAN	// Scan game state to find units and then use TREE
	
}
