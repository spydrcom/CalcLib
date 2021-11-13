
package net.myorb.testing.anal;

import net.myorb.math.specialfunctions.AnalysisTool;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.data.abstractions.PrimitiveRangeDescription;
import net.myorb.math.*;

/**
 * analysis tool for Exponential Integral spline evaluation
 * @author Michael Druckman
 */
public class ExpInt
{

	static String			inc = "0.1",
	// lo = "-2000",		hi = "-100";			// area =  -3.636283132409623E-46,	error = 1.8836461997049885E-47
	// lo = "-500",			hi = "-100";			// area =  -3.66313009576577E-46,	error = 5.708113410067998E-47
	 lo = "-100",			hi = "-10",				// area =  -4.156968923160761E-6,	error = 4.4364337077026547E-10
	// lo = "-10",			hi = "-1";				// area =  -0.21937977742658596,	error = 3.384037500220316E-8
	// lo = "-1",			hi = "-0.1";			// area =  -1.603540024023858,		error = 2.5272742798509994E-8
	// lo = "-0.1",			hi = "-1E-6";			// area = -11.4153
	// lo = "-1E-6",		hi = "-1E-10";			// area =  -9.21033937

	// lo = "-1E-6",		hi = "-1E-8";			// -4.605169195987684
	//   lo = "-1E-8",		hi = "1E-9";			// -1.2296131411688662
	// lo = "1E-9",			hi = "1E-6";			//  6.907756277975643

	// lo = "1E-10",		hi = "1E-6";			// area =  9.21034137
	// lo = "1E-6",			hi = "0.1";				// area = 11.615481
	// lo = "0.1",          hi = "1";				// area =  3.51793063
	// lo = "1",            hi = "10";				// area = 2490.333858425
	// lo = "1", 			hi = "10";
							zero = "0";

	public static void main (String[] args)
	{
		PrimitiveRangeDescription
		range = new PrimitiveRangeDescription (lo, hi, inc);
		AnalysisTool.display (range, new Ei (), 25);
	}

	static class Ei implements Function<Double>
	{

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#eval(java.lang.Object)
		 */
		public Double eval (Double x) { return Math.exp (x) / x; }

		/* (non-Javadoc)
		 * @see net.myorb.math.Function#getSpaceManager()
		 */
		public SpaceManager<Double> getSpaceDescription() { return sm; }
		public SpaceManager<Double> getSpaceManager() { return sm; }
		SpaceManager<Double> sm = new ExpressionFloatingFieldManager ();
		
	}

}

