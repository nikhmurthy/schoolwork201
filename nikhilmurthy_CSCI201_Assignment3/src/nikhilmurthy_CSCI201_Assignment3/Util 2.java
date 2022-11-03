package nikhilmurthy_CSCI201_Assignment3;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;

// referenced from lab 4

public class Util {

	public static String printMessage(StopWatch watch) {
		
		long milisec = watch.getTime(TimeUnit.MILLISECONDS);
		long sec = watch.getTime(TimeUnit.SECONDS);
		long min = watch.getTime(TimeUnit.MINUTES);
		long hour = watch.getTime(TimeUnit.HOURS);
		
		milisec = milisec - 1000*sec;
		sec = sec - 60*min;
		min = hour - 60*hour;
		
		String datetime = "[" + String.format("%02d", hour); 
		datetime += ":" + String.format("%02d", min);
		datetime += ":" + String.format("%02d", sec);
		datetime += "." + String.format("%02d", milisec) + "] ";
		return datetime;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}
