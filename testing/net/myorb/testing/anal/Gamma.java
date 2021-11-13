
package net.myorb.testing.anal;

import net.myorb.math.Function;
import net.myorb.math.SpaceManager;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.data.abstractions.PrimitiveRangeDescription;
import net.myorb.math.specialfunctions.AnalysisTool;

public class Gamma
{

	static String									//		D1				D2					D3				D4					D5
	//lo = "0.000000001", hi = "0.1", inc = "0.1";	// 		0.09752777		0.004835416435		3.2099998E-4	2.401388715E-5		1.91785698E-6
	//lo = "0.1", hi = "1", inc = "0.1";			// 		0.67997685911	0.3491038213		0.224484188		0.1636945			0.1284181
	lo = "1", hi = "10", inc = "0.1";				// 		0.86693			2.0446356631921163	6.2071167		24.02047			113.902297

	public static void main (String[] args)
	{
		PrimitiveRangeDescription range = new PrimitiveRangeDescription (lo, hi, inc);
		AnalysisTool.display (range, new D (5), 25); System.out.println ("===");
	}

	static class D implements Function<Double>
	{

		D (int n) { N = n; }
		int N;

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public Double eval (Double x) { return Math.pow (x, N) / (Math.exp(x) - 1); }

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#getSpaceManager()
		 */
		public SpaceManager<Double> getSpaceDescription() { return sm; }
		public SpaceManager<Double> getSpaceManager() { return sm; }
		SpaceManager<Double> sm = new ExpressionFloatingFieldManager ();
		
	}

}
