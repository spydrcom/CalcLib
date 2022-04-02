
package net.myorb.math.computational.splines;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

/**
 * implementation for FittedSegmentRepresentation
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SegmentProperties <T>
	extends SegmentParameters implements SegmentAbilities <T>
{


	public SegmentProperties
		(
			SegmentRepresentation rep,
			ExpressionComponentSpaceManager<T> mgr,
			SplineMechanisms spline
		)
	{
		super (rep);
		this.spline = spline;
		this.mgr = mgr;
	}
	public SegmentProperties
		(
			ExpressionComponentSpaceManager<T> mgr,
			SplineMechanisms spline
		)
	{
		this.spline = spline;
		this.mgr = mgr;
	}
	protected ExpressionComponentSpaceManager <T> mgr;
	protected SplineMechanisms spline;


	/**
	 * @param using data which defines this segment
	 * @return THIS object return for chains
	 */
	public SegmentProperties <T> connectFunction
			(SegmentRepresentation using)
	{
		this.copyFrom (using); this.setMargins ();
		this.connectFunction ();
		return this;
	}

	/**
	 * allocate a segment function object for this segment
	 */
	public void connectFunction ()
	{
		this.segmentFunction = new SegmentFunction <T> (this, mgr, spline);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.FittedFunction.FittedSegmentRepresentation#getSegmentFunction()
	 */
	public SegmentFunction <T>
		getSegmentFunction () { return this.segmentFunction; }
	protected SegmentFunction <T> segmentFunction;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.FittedFunction.FittedSegmentRepresentation#checkFor(double, int)
	 */
	public SegmentFunction <T> checkFor (double value, int margins)
	{
		return isWithin (marginRange[margins], value) ?
				segmentFunction : null;
	}
	public boolean isWithin (double[] range, double value)
	{
		return value >= range[0] && value <= range[1];
	}
	public void setMargins ()
	{
		for (int n = 0; n < marginRange.length; n++)
		{
			marginRange[n] = new double[]
			{
				lo - n*delta,
				hi + n*delta
			};
		}
	}
	protected double [][] marginRange = new double [FittedFunction.MARGINS][2];


}

