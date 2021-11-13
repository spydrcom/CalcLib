
package net.myorb.math.computational;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.data.abstractions.PrimitiveRangeDescription;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * a Segment Manager for Real Number Anti Derivative Spline objects
 * @author Michael Druckman
 */
public abstract class ADSplineRealSegmentManager
	implements AntiDerivativeSpline.SegmentManager<Double>, Function<Double>
{

	/**
	 * @return a new Double spline object using this segment manager
	 */
	public AntiDerivativeSpline<Double> newSplineInstance ()
	{
		return new AntiDerivativeSpline<Double> (this, sm);
	}

	/**
	 * @return a description of the interval the function is defined on
	 */
	public PrimitiveRangeDescription getDomain ()
	{
		return new PrimitiveRangeDescription
		(
			getFirstSegmentBase (), upTo [upTo.length - 1], 0
		);
	}

	/**
	 * change the quadrature precision expectation
	 * @param precision the new value of maximum expected error
	 */
	protected void setPrecision (double precision) { this.precision = precision; }
	protected double precision = 1E-4;

	/**
	 * a table is used to describe the segments
	 * @param upTo the list of HI end domain values for the segments
	 * @param area the list of area values for the segments
	 */
	public ADSplineRealSegmentManager (double[] upTo, double[] area)
	{
		this.sm = new ExpressionFloatingFieldManager ();
		this.upTo = upTo; this.area = area;
	}
	protected double[] upTo, area;

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#getSpaceManager()
	 */
	public SpaceManager<Double> getSpaceDescription () { return sm; }
	public SpaceManager<Double> getSpaceManager () { return sm; }
	protected SpaceManager<Double> sm;;

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSpline.SegmentManager#getSegmentCount()
	 */
	public int getSegmentCount () { return area.length; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSpline.SegmentManager#getSegmentHi(int)
	 */
	public Double getSegmentHi (int forSegment) { return upTo[forSegment]; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSpline.SegmentManager#getSegmentArea(int)
	 */
	public Double getSegmentArea (int forSegment) { return area[forSegment]; }

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSpline.SegmentManager#getAreaBetween(java.lang.Object, java.lang.Object)
	 */
	public Double getAreaBetween (Double lo, Double hi)
	{
		return TanhSinhQuadratureAlgorithms.Integrate (this, lo, hi, precision, null);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.computational.AntiDerivativeSpline.SegmentManager#getFirstSegmentBase()
	 */
	public abstract Double getFirstSegmentBase ();

	/* (non-Javadoc)
	 * @see net.myorb.math.Function#eval(java.lang.Object)
	 */
	public abstract Double eval (Double x);

}

