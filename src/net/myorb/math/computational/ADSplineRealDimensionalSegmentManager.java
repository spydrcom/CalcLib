
package net.myorb.math.computational;

import net.myorb.data.abstractions.SimpleUtilities;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.MultiDimensional;
import net.myorb.math.SpaceManager;

import java.util.List;

/**
 * a Segment Manager for Multi Dimensional
 *  Real Number Anti Derivative Spline objects
 * @author Michael Druckman
 */
public abstract class ADSplineRealDimensionalSegmentManager
	implements AntiDerivativeSplineMultiDimensional.SegmentManager<Double>,  MultiDimensional.Function<Double>
{

	/**
	 * @param lo the interval LO of each dimension 
	 * @param hi the interval HI of each segment of each dimension
	 */
	public ADSplineRealDimensionalSegmentManager
		(
			double [] lo, double [][] hi
		)
	{
		this.base = lo; this.hi = hi;
		this.dimensions = this.base.length;
		this.segments = new int[this.dimensions];

		for (int d = 0; d < this.dimensions; d++)
		{ this.segments[d] = hi[d].length; }
	}

	/**
	 * @return a new Double spline object using this segment manager
	 */
	public AntiDerivativeSplineMultiDimensional<Double> newSplineInstance ()
	{
		return new AntiDerivativeSplineMultiDimensional<Double> (this, sm);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSplineMultiDimensional.SegmentManager#getDimensionCount()
	 */
	public int getDimensionCount ()
	{
		return dimensions;
	}
	protected int dimensions;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSplineMultiDimensional.SegmentManager#getSegmentCount(int)
	 */
	public int getSegmentCount (int forDimension)
	{
		return segments[forDimension];
	}
	protected int[] segments;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSplineMultiDimensional.SegmentManager#getFirstSegmentBase(int)
	 */
	public Double getFirstSegmentBase (int forDimension)
	{
		return base[forDimension];
	}
	protected double [] base;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSplineMultiDimensional.SegmentManager#getHi(int, int)
	 */
	public Double getHi (int forDimension, int forSegment)
	{
		if (forSegment < 0) return 0.0;
		return hi[forDimension][forSegment];
	}
	protected double [][] hi;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSplineMultiDimensional.SegmentManager#getContributionFrom(java.util.List)
	 */
	public abstract Double getContributionFrom (List<Integer> segmentAt);

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSplineMultiDimensional.SegmentManager#getContributionFrom(java.util.List, java.util.List, java.util.List)
	 */
	public abstract Double getContributionFrom
	(List<Double> dimensionLo, List<Double> dimensionHi, List<Integer> includedSegments);

	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#getSpaceManager()
	 */
	public SpaceManager<Double> getSpaceDescription () { return sm; }
	protected SpaceManager<Double> sm = new ExpressionFloatingFieldManager ();

	/* (non-Javadoc)
	 * @see net.myorb.math.MultiDimensional.Function#f(java.util.List)
	 */
	public abstract Double f (List<Double> dataPoint);
	protected List<Double> toList (Double... d) { return SimpleUtilities.toList (d); }
	public Double f (Double... x) { return f (toList (x)); }

}
