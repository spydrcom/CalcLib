
package net.myorb.math.computational;

import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.math.MultiDimensional;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

import java.util.List;

/**
 * use quadrature as a slice integration mechanism in a multi-dimensional integral
 * @author Michael Druckman
 */
public class MultiDimensionalRealQuadrature
		extends MultiDimensionalRealIntegralSupport
	implements MultiDimensionalIntegral<Double>
{


	/**
	 * @param integrand the function to be integrated
	 * @param deltas the delta for each dimension to use in approximations
	 */
	public MultiDimensionalRealQuadrature
	(MultiDimensional.Function<Double> integrand, Double[] deltas)
	{ this (integrand, SimpleUtilities.toList (deltas)); }


	/**
	 * @param integrand the function to be integrated
	 * @param deltas the delta for each dimension to use in approximations
	 */
	public MultiDimensionalRealQuadrature
	(MultiDimensional.Function<Double> integrand, List<Double> deltas)
	{ super (integrand); this.setDeltas (deltas); }


	/**
	 * @param integrand the function to be integrated
	 */
	public MultiDimensionalRealQuadrature
	(MultiDimensional.Function<Double> integrand)
	{ super (integrand); }


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalRealIntegralSupport#getDeltaFor(int)
	 */
	public double getDeltaFor (int dimension)
	{
		return dimension == super.highestDimension ? 1 : super.delta.get (dimension);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#computeApproximation(java.util.List, java.util.List)
	 */
	public Double computeApproximation (List<Double> lo, List<Double> hi)
	{
		MultiDimensionalRealQuadratureContributionManager
			contributionManager = new MultiDimensionalRealQuadratureContributionManager (this, lo, hi);
		return new MultiDimensionalAccumulation<Double, Double>(contributionManager, tmgr)
		.accumulateFrom (startingPointFor (lo)) * unitContribution;
	}


	/**
	 * @param typeOfQuadrature factory for integral algorithm implementation
	 * @return new factory instance for objects of this integral implementation
	 */
	public static MultiDimensionalIntegralEngineFactory<Double> newFactoryInstance
		(MultiDimensionalIntegralEngineFactory<Double> typeOfQuadrature)
	{
		return new MultiDimensionalIntegralEngineFactory<Double>()
			{
				/* (non-Javadoc)
				 * @see net.myorb.math.computational.MultiDimensionalIntegralEngineFactory#newMultiDimensionalIntegral(net.myorb.math.MultiDimensional.Function)
				 */
				public MultiDimensionalIntegral<Double> newMultiDimensionalIntegral
				(MultiDimensional.Function<Double> integrand) { return newInstance (integrand, typeOfQuadrature); }

				/* (non-Javadoc)
				 * @see net.myorb.math.computational.MultiDimensionalIntegralEngineFactory#newMultiDimensionalIntegral(net.myorb.math.MultiDimensional.Function)
				 */
				public MultiDimensionalIntegral<Double> newMultiDimensionalIntegral(Function<Double> integrand)
				{ throw new RuntimeException ("Single dimension integration not supported"); }
			};
	}

	/**
	 * @param integrand function to be integrated
	 * @param typeOfQuadrature factory for integral algorithm implementation
	 * @return a new instance of this integral implementation
	 */
	public static MultiDimensionalIntegral<Double> newInstance
	(MultiDimensional.Function<Double> integrand, MultiDimensionalIntegralEngineFactory<Double> typeOfQuadrature)
	{
		MultiDimensionalRealQuadrature quad = new MultiDimensionalRealQuadrature (integrand);
		quad.setIntegralEngineFactory (typeOfQuadrature);
		return quad;
	}


}


/**
 * implementation of ContributionManager
 */
class MultiDimensionalRealQuadratureContributionManager
		extends MultiDimensionalRealIntegralContributionManager
	implements MultiDimensionalAccumulation.ContributionManager<Double, Double>
{

	public MultiDimensionalRealQuadratureContributionManager
	(MultiDimensionalRealIntegralSupport master, List<Double> lo, List<Double> hi)
	{
		super (master, hi);
		this.integrand = new MultiDimensionalRealQuadratureIntegrand (master); master.forceDeltaSet (lo.size ()); 
		this.processInterval (lo.get (master.highestDimension), hi.get (master.highestDimension));
	}
	protected MultiDimensionalRealQuadratureIntegrand integrand;

	/**
	 * @param highestDimensionIntervalLo interval lo
	 * @param highestDimensionIntervalHi interval hi
	 */
	void processInterval (Double highestDimensionIntervalLo, Double highestDimensionIntervalHi)
	{
		this.highestDimensionDelta = highestDimensionIntervalHi - highestDimensionIntervalLo + 1;
		this.integrand.setInterval (highestDimensionIntervalLo, highestDimensionIntervalHi);
	}
	protected Double highestDimensionDelta;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalRealIntegralContributionManager#getDeltaFor(int)
	 */
	public double getDeltaFor (int dimension)
	{
		return dimension == master.highestDimension ? highestDimensionDelta : master.getDeltaFor (dimension);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalAccumulation.ContributionManager#getContributionFrom(java.util.List)
	 */
	public Double computeContributionFrom (List<Double> portions) { return integrand.eval (portions); }

}


/**
 * the integrand controlled by the quadrature functionality
 */
class MultiDimensionalRealQuadratureIntegrand implements Function<Double>
{

	public MultiDimensionalRealQuadratureIntegrand
			(MultiDimensionalRealIntegralSupport master)
	{ integral = (this.master = master).newIntegralFor (this); }
	protected MultiDimensionalRealIntegralSupport master;
	protected MultiDimensionalIntegral<Double> integral;

	/**
	 * the interval for the highest dimension
	 *   of the slice of integration being evaluated
	 * @param lo the lo of the interval of the integral
	 * @param hi the hi of the interval of the integral
	 */
	public void setInterval (Double lo, Double hi)
	{ this.lo = lo; this.hi = hi; }
	protected Double lo, hi;

	/**
	 * @param parameters full N-dimensional data point 
	 * @return integral of slice given by point
	 */
	public Double eval (List<Double> parameters)
	{
		this.parameters = parameters;
		return integral.computeApproximation (lo, hi);
	}
	protected List<Double> parameters;

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#eval(java.lang.Object)
	 */
	public Double eval (Double x)
	{
		parameters.set (master.highestDimension, x);
		return master.evaluateIntegrandAt (parameters);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<Double> getSpaceDescription () { return master.tmgr; }
	public SpaceManager<Double> getSpaceManager () { return master.tmgr; }

}

