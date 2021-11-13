
package net.myorb.math.computational;

import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.math.MultiDimensional;
import net.myorb.math.Function;

import java.util.List;

/**
 * an integral approximation mechanism for multi-dimensional functions
 * @author Michael Druckman
 */
public class MultiDimensionalRealIntegral
		extends MultiDimensionalRealIntegralSupport
	implements MultiDimensionalIntegral<Double>
{


	/**
	 * @param integrand the function to be integrated
	 * @param deltas the delta for each dimension to use in approximations
	 */
	public MultiDimensionalRealIntegral
	(MultiDimensional.Function<Double> integrand, Double[] deltas)
	{ this (integrand, SimpleUtilities.toList (deltas)); }


	/**
	 * @param integrand the function to be integrated
	 * @param deltas the delta for each dimension to use in approximations
	 */
	public MultiDimensionalRealIntegral
	(MultiDimensional.Function<Double> integrand, List<Double> deltas)
	{ super (integrand); this.setDeltas (deltas); }


	/**
	 * N-dimensional version (average function sample)
	 * @param integrand the function to be integrated
	 */
	public MultiDimensionalRealIntegral
	(MultiDimensional.Function<Double> integrand)
	{
		super (integrand);
	}


	/**
	 * 1-dimensional version (uses trapezoid rule)
	 * @param integrand integrand the function to be integrated
	 */
	public MultiDimensionalRealIntegral
	(Function<Double> integrand)
	{
		super (integrand);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#computeApproximation(java.util.List, java.util.List)
	 */
	public Double computeApproximation (List<Double> lo, List<Double> hi)
	{
		forceDeltaSet (lo.size ());

		if (highestDimension == 0 && integrand1D != null)	// single dimensional and 1-D function specified
		{
			IterativeIntegralApproximation<Double> engine =
				new IterativeIntegralApproximation<Double>(integrand1D, lo.get (0), hi.get (0), true);
			engine.execute ((int) Math.pow (2, 3 * level.intValue ()), 1);
			if (TRACE_INTEGRAL_USE) System.out.println ("1-D Integral");
			return engine.getResult ();
		}
		else if (highestDimension == 1 && optimized)		// hard coded 2-D version, non-generic, much faster ???  (is it?)
		{
			if (TRACE_INTEGRAL_USE) System.out.println ("2-D Integral");
			return new DoubleIntegral (integrand, getDeltaFor (0), getDeltaFor (1))
						.computeApproximation (lo, hi);
		}
		else
		{
			MultiDimensionalRealIntegralContributionManager
				contributionManager = new MultiDimensionalRealIntegralContributionManager (this, hi);
			return new MultiDimensionalAccumulation<Double, Double>(contributionManager, tmgr)
			.accumulateFrom (startingPointFor (lo)) * unitContribution;
		}
	}
	boolean optimized = false;								// generic is looking faster now ???


	/**
	 * @return new factory instance for objects of this integral implementation
	 */
	public static MultiDimensionalIntegralEngineFactory<Double> newFactoryInstance ()
	{
		return new MultiDimensionalIntegralEngineFactory<Double>()
			{
				/* (non-Javadoc)
				 * @see net.myorb.math.computational.MultiDimensionalIntegralEngineFactory#newMultiDimensionalIntegral(net.myorb.math.MultiDimensional.Function)
				 */
				public MultiDimensionalIntegral<Double> newMultiDimensionalIntegral (MultiDimensional.Function<Double> integrand) { return newInstance (integrand); }

				/* (non-Javadoc)
				 * @see net.myorb.math.computational.MultiDimensionalIntegralEngineFactory#newMultiDimensionalIntegral(net.myorb.math.MultiDimensional.Function)
				 */
				public MultiDimensionalIntegral<Double> newMultiDimensionalIntegral(Function<Double> integrand)
				{ return newInstance (integrand); }
			};
	}


	/**
	 * @param integrand function to be integrated
	 * @return a new instance of this integral implementation
	 */
	public static MultiDimensionalIntegral<Double> newInstance (MultiDimensional.Function<Double> integrand)
	{ return new MultiDimensionalRealIntegral (integrand); }

	public static MultiDimensionalIntegral<Double> newInstance (Function<Double> integrand)
	{ return new MultiDimensionalRealIntegral (integrand); }


}


