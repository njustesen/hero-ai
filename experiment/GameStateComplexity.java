import java.math.BigInteger;


public class GameStateComplexity {

	private static final int HP = 1000;

	public static void main(String[] args){
		
		BigInteger big = new BigInteger("1");
		
		for(int i = 0; i <= 10; i++)
			big = big.add(units(i, 9, 5, 2, 2));
		
		System.out.println(big);
		
	}
	
	private static BigInteger units(int n, int x, int y, int c, int deploy){
		
		BigInteger units = new BigInteger("1");
		
		for(int i = 1; i <= n; i++)
			units = units.multiply(new BigInteger(""+ (HP*(x*y-c*2-deploy))));
		
		units = units.multiply(items(n));
		
		return units;
		
	}
	
	private static BigInteger items(int n){
		BigInteger items = new BigInteger("1");
		for(int i = 1; i <= (3+3+3+2); i++)
			items = items.multiply(new BigInteger(""+(Math.max(0, n-i)+2)));
		
		items = items.multiply(new BigInteger("" + (2*2*2*2)));	// Potions and spells
		
		return items;
	}
	
}
