package util;

import java.util.List;

public class Statistics {

	public static double avg(List<Double> vals){
		
		double sum = 0;
		for(double d : vals)
			sum += d;
		
		return sum / vals.size();
		
	}
	
	public static double stdDev(List<Double> vals){
		
		double avg = avg(vals);
		
		double sum = 0;
		for(double d : vals)
			sum += (d - avg) * (d - avg);
		
		double davg = sum / vals.size();
		
		return Math.sqrt(davg);
		
	}
	
}
