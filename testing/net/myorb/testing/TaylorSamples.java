
package net.myorb.testing;

import net.myorb.math.computational.*;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.SpaceManager;

/**
 * simple test of the Taylor demonstration class
 * @author Michael Druckman
 */
public class TaylorSamples
{

	// data type manager using simple Double float
	static ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();

	/**
	 * entry point for the demonstration execution
	 * @param args not used
	 */
	public static void main (String[] args)
	{
		TaylorSeriesEvaluations <Double> TS = new TaylorSeriesEvaluations <> (mgr);
		GeneratingFunctions.Coefficients <Double> C = TS.compute (new F (), 0.0, 6, 1E-3, 1E-1);

		System.out.println ("Derivatives:");
		TS.derivatives.display ();

		System.out.print ("Coefficients:  ");
		System.out.println (C);
	}

	/**
	 * implementation of function to be evaluated
	 */
	static class F implements net.myorb.math.Function<Double>
	{

		public Double eval (Double x)
		{
			return Math.sin (x);
		}

		public SpaceDescription <Double> getSpaceDescription () { return mgr; }
		public SpaceManager<Double> getSpaceManager () { return mgr; }
	}
}
