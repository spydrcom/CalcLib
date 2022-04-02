
package net.myorb.math.computational.splines;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.math.computational.Spline;

import net.myorb.math.SpaceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * describe a set of functionalities built on spline primitives
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SegmentOperations <T> extends SegmentProperties <T> implements Spline.Operations <T>
{


	public static int MARGINS = 8;


	public SegmentOperations
		(
			ExpressionComponentSpaceManager <T> mgr,
			SplineMechanisms spline
		)
	{
		super (mgr, spline);
		this.segments = new ArrayList<>();
	}
	protected List < SegmentAbilities <T> > segments;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		return findSegment (x).eval (x);
	}


	/**
	 * find a segment descriptor that covers the parameter
	 * @param x the parameter value to find a segment function for
	 * @return the segment function that covers the parameter
	 * @throws RuntimeException failed segment search
	 */
	public SegmentFunction<T> findSegment (T x)
			throws RuntimeException
	{
		SegmentFunction<T> f;
		double p = mgr.component (x, 0);
		for (int margin=0; margin<MARGINS; margin++)
		{
			for (SegmentAbilities <T> segment : segments)
			{ if ((f = segment.checkFor (p, margin)) != null) return f; }
		}
		throw new RuntimeException ("Segment error");
	}


	/**
	 * use the spline to compute the integral over a range
	 * @param lo the lo end of integral range in function coordinates
	 * @param hi the hi end of integral range in function coordinates
	 * @return the computed value of the integral for the specified range
	 */
	public T evalIntegralOver
		(
			double lo, double hi
		)
	{
		T result = mgr.getZero ();
		for (SegmentAbilities <T> segment : segments)
		{
			T portion = segment.getSegmentFunction ()
				.evalIntegralContribution (lo, hi);
			result = mgr.add (result, portion);
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Spline.Operations#evalIntegral()
	 */
	public T evalIntegral ()
	{
		T result = mgr.getZero ();
		for (SegmentAbilities <T> segment : segments)
		{
			T portion = segment.getSegmentFunction ().evalIntegral ();
			result = mgr.add (result, portion);
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.Spline.Operations#getRepresentation()
	 */
	public Representation getRepresentation ()
	{
		return new Representation ()
		{
			public List <SegmentRepresentation> getSegmentList ()
			{
				List <SegmentRepresentation> list = new ArrayList <> ();
				list.addAll (segments);
				return list;
			}
			public String getInterpretation ()
			{
				return spline.getInterpreterPath ();
			}
		};
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return mgr; }
	public SpaceManager<T> getSpaceManager () { return mgr; }


}

