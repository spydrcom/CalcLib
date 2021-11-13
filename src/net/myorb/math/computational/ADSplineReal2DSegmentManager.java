
package net.myorb.math.computational;

import java.util.List;

/**
 * boiler plate for a Segment Manager for 
 *  TWO dimensional Real Number Anti Derivative Spline objects.
 *  this supports only 2 dimensional functions while parent is N-dimensional.
 *  for actual unit test, see: net.myorb.testing.anal.TwoDimSpline
 * @author Michael Druckman
 */
public class ADSplineReal2DSegmentManager
	extends ADSplineRealDimensionalSegmentManager
{

	static final boolean
		TRACE_INTEGRALS = false,														// display intervals for each requested approximation
		USE_GENERIC = true, USE_CCQ = false, USE_TSQ = false;							// selection criteria for type of integration to be used
	static MultiDimensionalIntegralEngineFactory<Double> INTEGRATION_ENGINE_FACTORY =
		USE_TSQ ? TSQIntegration.newFactoryInstance () :								// TSQ specific - quadrature hybrid N-Dimensional Integral (fastest by far)
		USE_GENERIC ? MultiDimensionalRealIntegral.newFactoryInstance () :				// Generic N-Dimensional Integral implementation (slowest, teaching aid only)
		USE_CCQ ? CCQIntegration.newFactoryInstance () :								// CCQ specific - quadrature hybrid N-Dimensional Integral (using DCT)
		DoubleIntegral.newFactoryInstance ();											// non-generic, 2D only, slower than QUAD versions

	/**
	 * spline definition must present HI and LO tables
	 * @param lo the LO value of the interval for each dimension
	 * @param hi the HI of each interval indexed by dimension and segment
	 * @param delta the space between samples
	 */
	public ADSplineReal2DSegmentManager (double [] lo, double [][] hi, double delta)
	{ super (lo, hi); initializeIntegrationEngine (usingSquareRegions (delta)); }
	public ADSplineReal2DSegmentManager (double [] lo, double [][] hi)
	{ super (lo, hi); initializeIntegrationEngine (); }

	/**
	 * @param deltas the space between samples in each dimension
	 */
	private void initializeIntegrationEngine (List<Double> deltas)
	{ (integral = INTEGRATION_ENGINE_FACTORY.newMultiDimensionalIntegral (this)).setDeltas (deltas); }
	private void initializeIntegrationEngine () { integral = INTEGRATION_ENGINE_FACTORY.newMultiDimensionalIntegral (this); }
	public MultiDimensionalIntegral<Double> getIntegral () { return integral; }
	protected MultiDimensionalIntegral<Double> integral;

	/**
	 * @param delta edge size of regions
	 * @return delta list for square regions
	 */
	protected List<Double> usingSquareRegions (double delta)
	{ return toList (delta, delta); }

	/**
	 * actual double integral approximation
	 * @param xLo lo end of the x axis interval
	 * @param xHi hi end of the x axis interval
	 * @param yLo lo end of the y axis interval
	 * @param yHi hi end of the y axis interval
	 * @return approximate integral value
	 */
	public Double computeIntegralApproximation
	(double xLo, double xHi, double yLo, double yHi)
	{
		if (TRACE_INTEGRALS)
		{
			System.out.println ();
			System.out.println ("INTEGRAL");
			System.out.println ("X: " + xLo + ".." + xHi);
			System.out.println ("Y: " + yLo + ".." + yHi);
			System.out.println ();
		}

		if (xLo >= xHi || yLo >= yHi) return 0.0;

		return integral.computeApproximation
		(
			toList (xLo, yLo), toList (xHi, yHi)
		);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ADSplineRealDimensionalSegmentManager#getContributionFrom(java.util.List, java.util.List, java.util.List)
	 */
	public Double getContributionFrom
	(List<Double> dimensionLo, List<Double> dimensionHi, List<Integer> includedSegments)
	{
		double
			xHi = dimensionHi.get (0),
			yHi = hiFor (includedSegments.get (1));
		double extraX = computeIntegralApproximation
		(
			dimensionLo.get (0), xHi, 0, yHi
		);
		double extraY = computeIntegralApproximation
		(
			0, xHi, dimensionLo.get (1), dimensionHi.get (1)
		);
		return extraX + extraY;
	}
	protected double hiFor (int segment) { return segment < 0 ? 0.0 : hi[1][segment]; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.ADSplineRealDimensionalSegmentManager#f(java.util.List)
	 */
	public Double f (List<Double> dataPoint)
	{
		double
			x = dataPoint.get (0),
			y = dataPoint.get (1);
		return integrand (x, y);
	}

	/**
	 * @param x the x axis parameter
	 * @param y the y axis parameter
	 * @return function value at x,y
	 */
	public double integrand (double x, double y) { return 0.0; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSplineMultiDimensional.SegmentManager#getContributionFrom(java.util.List)
	 */
	public Double getContributionFrom (List<Integer> segmentAt)
	{
		return null;
	}

}
