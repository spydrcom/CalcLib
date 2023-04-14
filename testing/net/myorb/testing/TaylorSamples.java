
package net.myorb.testing;

import net.myorb.math.computational.*;
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.data.abstractions.SpaceDescription;
import net.myorb.math.SpaceManager;

public class TaylorSamples
{

	static ExpressionFloatingFieldManager mgr = new ExpressionFloatingFieldManager ();

	public static void main (String[] args)
	{
		TaylorSeriesEvaluations <Double> TS = new TaylorSeriesEvaluations <> (mgr);
		System.out.println (TS.compute (new F (), 0.0, 5, 1E-5, 1E-3));
	}
	static class F implements net.myorb.math.Function<Double>
	{

		@Override
		public Double eval(Double x) {
			return Math.sin(x);
		}

		@Override
		public SpaceDescription<Double> getSpaceDescription() {
			return mgr;
		}

		@Override
		public SpaceManager<Double> getSpaceManager() {
			return mgr;
		}
		
	}
}
