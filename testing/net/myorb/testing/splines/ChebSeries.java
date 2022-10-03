
package net.myorb.testing.splines;

import net.myorb.math.GeneratingFunctions.Coefficients;
import net.myorb.math.computational.splines.ChebyshevSeriesExpansion;
import net.myorb.math.realnumbers.RealFunctionWrapper;

public class ChebSeries
{

	// cos ( b * ln t )  / ( (exp t + 1) * sqrt t )

	static final double B = 2;
	public static double eta (double b, double t)
	{
		return Math.cos ( b * Math.log (t) )  / ( (Math.exp (t) + 1) * Math.sqrt (t) );
	}
	public static double eta_s (double z)
	{
		return eta (B, (z+1)/8);
	}

	public static double poly (double x)
	{
		return 2*Math.pow(x,3) - 3*Math.pow(x,2);
	}

	public static void main (String[] args)
	{
		int order = 22;
		RealFunctionWrapper f =
			new RealFunctionWrapper
			(
				(x) -> eta_s (x)
			);
		ChebyshevSeriesExpansion series = new ChebyshevSeriesExpansion ();
		Coefficients <Double> c = series.computeSeries (f.toCommonFunction (), order);
		System.out.println (c);
	}

}
