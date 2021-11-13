
package net.myorb.math.computational;

import net.myorb.data.abstractions.SimpleUtilities;
import net.myorb.data.abstractions.ManagedSpace;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.DataConversions;

import net.myorb.math.MultiDimensional;
import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * common methods across integral implementations
 * @author Michael Druckman
 */
public class MultiDimensionalRealIntegralSupport
implements MultiDimensionalIntegral<Double>
{

	static final boolean TRACE_INTEGRAL_USE = false;	// display allocation of Integral objects

	/**
	 * @param integrand the function to be integrated
	 */
	public MultiDimensionalRealIntegralSupport (MultiDimensional.Function<Double> integrand)
	{ identifyTypeFor (this.integrand = integrand); }

	/**
	 * @param integrand the function to be integrated
	 */
	public MultiDimensionalRealIntegralSupport (Function<Double> integrand)
	{ identifyTypeFor (this.integrand1D = integrand); }
	protected Function<Double> integrand1D;

	/**
	 * @param integrand the function to be integrated
	 * @return a real integral implementation from local factory
	 */
	public MultiDimensionalIntegral<Double> newIntegralFor
	(MultiDimensional.Function<Double> integrand) { return integralEngineFactory.newMultiDimensionalIntegral (integrand); }
	public MultiDimensionalIntegral<Double> newIntegralFor (Function<Double> integrand)
	{ return integralEngineFactory.newMultiDimensionalIntegral (integrand); }

	/**
	 * @param integralEngineFactory the factory to use for creation of integral objects
	 */
	public void setIntegralEngineFactory
	(MultiDimensionalIntegralEngineFactory<Double> integralEngineFactory) { this.integralEngineFactory = integralEngineFactory; }
	protected MultiDimensionalIntegralEngineFactory<Double> integralEngineFactory;

	/**
	 * @param parameters the values of parameters for each dimension
	 * @return function value at parameter point
	 */
	public double evaluateIntegrandAt
	(List<Double> parameters) { return this.integrand.f (parameters); }
	public double evaluateIntegrandAt (Double... x) { return this.integrand.f (x); }
	protected MultiDimensional.Function<Double> integrand;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#setDeltas(java.util.List)
	 */
	public void setDeltas (List<Double> deltas)
	{
		this.checkLock ();
		this.delta = deltas;
		this.setDimension (deltas.size ());
		this.setUnitContribution ();
		this.deltaSet = true;
	}
	public void setDeltas (Double... deltas)
	{ setDeltas (SimpleUtilities.toList (deltas)); }


	/**
	 * set dimension allowing delta to be computed by precision
	 * @param dimension the number of dimensions
	 */
	public void setDimension (int dimension)
	{ highestDimension = dimension - 1; }
	protected int highestDimension = -1;


	/**
	 * use precision level to set deltas
	 * @param dimension the dimension to use
	 */
	public void forceDeltaSet (int dimension)
	{
		if (deltaSet) return;
		setDimension (dimension);
		Double delta = Math.pow (10, -level.intValue ()), d[] = new Double[highestDimension+1];
		Arrays.fill (d, 0, d.length, delta);
		setDeltas (d);
	}
	protected boolean deltaSet = false;


	/**
	 * @param dimension the dimension to be queried
	 * @return delta for specified dimension
	 */
	public double getDeltaFor (int dimension)
	{ return delta.get (dimension); }
	protected List<Double> delta;


	/**
	 * product of deltas for all dimensions
	 */
	public void setUnitContribution ()
	{
		this.unitContribution = 1.0;
		for (int d = 0; d <= highestDimension; d++)
		{ this.unitContribution *= getDeltaFor (d); }
	}
	protected Double unitContribution = null;


	/**
	 * @param parameter check for exactly 2 elements
	 */
	public void verify (List<Double> parameter)
	{
		if (parameter != null && parameter.size () - 1 == highestDimension) return;
		throw new RuntimeException (VERIFY_FAIL_MSG);
	}
	public List<Double> verify (Double[] parameter)
	{
		List<Double> list;
		verify (list = SimpleUtilities.toList (parameter));
		return list;
	}
	static final String VERIFY_FAIL_MSG = "Integral requires equal counts of delta, lo, and hi values";


	/**
	 * prevent delta updates after integral computation has started
	 */
	private void checkLock ()
	{ if (this.deltaLock) throw new RuntimeException (LOCK_MSG); }
	static final String LOCK_MSG = "Delta becomes locked once integral computation has started";
	protected boolean deltaLock = false;


	/**
	 * @param lo the LO of the interval for each dimension
	 * @return the mid point of each interval LO
	 */
	public List<Double> startingPointFor (List<Double> lo)
	{
		deltaLock = true;
		List<Double> startingPoint = new ArrayList<Double>();

		for (int i = 0; i < lo.size (); i++)
		{
			// compute LO "Mid-Point" for each dimension
			startingPoint.add (lo.get (i) + delta.get (i) / 2);
		}

		return startingPoint;
	}


	/**
	 * @param lo the LO of the interval for each dimension
	 * @param hi the HI of the interval for each dimension
	 * @return the computed approximation
	 */
	public Double computeApproximation (Double[] lo, Double[] hi)
	{ return computeApproximation (verify (lo), verify (hi)); }


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#setRequestedPrecision(java.lang.Number)
	 */
	public void setRequestedPrecision (Number level) { this.level = level; }
	protected Number level = 4;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#computeApproximation(java.util.List, java.util.List)
	 */
	public Double computeApproximation (List<Double> lo, List<Double> hi)
	{ throw new RuntimeException ("No support for dimension > 1"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.MultiDimensionalIntegral#computeApproximation(java.lang.Object, java.lang.Object)
	 */
	public Double computeApproximation (Double lo, Double hi)
	{ throw new RuntimeException ("No support for dimension = 1"); }


	/**
	 * get local copy of data type manager
	 * @param managed an object that maintains data type
	 */
	public void identifyTypeFor (ManagedSpace<Double> managed)
	{ tmgr = DataConversions.toExpression (managed); }
	protected ExpressionSpaceManager<Double> tmgr;


}

