
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.charting.Plot3D;
import net.myorb.math.expressions.charting.ContourPlotProperties;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.TypedRangeDescription;
import net.myorb.math.expressions.VectorPlotEnabled;

import net.myorb.charting.DisplayGraphSegmentTools;
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * implement mechanisms for evaluation of a vector by an enabled transform
 * @param <T> data type for transform
 * @author Michael Druckman
 */
class VectoredTransform<T> extends PlotMatrixTraversal
	implements TypedRangeDescription.TypedRangeProperties<T>
{


	VectoredTransform
		(
			ContourPlotProperties proprties,
			Environment<T> environment
		)
	{
		super (proprties);
		this.initEnvironment (environment);
		this.initSeries ();
	}


	/**
	 * @param environment the system environment object
	 */
	protected void initEnvironment (Environment<T> environment)
	{
		this.mgr = (ExpressionComponentSpaceManager<T>)
				environment.getSpaceManager ();
		this.environment = environment;
	}
	protected ExpressionComponentSpaceManager<T> mgr;
	protected Environment<T> environment;


	/**
	 * transform has been linked to properties object
	 */
	@SuppressWarnings("unchecked") void initTransform ()
	{
		this.transform =
			( (Plot3D<T>) proprties ).getMultiDimensionalVectored ()
				.getVectorPlotEnabledTransform ();
	}
	protected VectorPlotEnabled<T> transform;


	/**
	 * results of vectored transforms are written in an array of point series objects
	 */
	protected void initSeries ()
	{
		series = new ArrayList<DisplayGraphTypes.Point.Series> ();
		series.add (s0 = new DisplayGraphTypes.Point.Series ());
		series.add (s1 = new DisplayGraphTypes.Point.Series ());
	}
	protected List<DisplayGraphTypes.Point.Series> series;
	protected DisplayGraphTypes.Point.Series s0, s1;


	/**
	 * clear and refill the point series objects with transform of new input vector 
	 */
	protected void evalTransform ()
	{
		s0.clear (); s1.clear ();
		transform.evaluateSeries (this, series, environment);
		DisplayGraphSegmentTools.checkRange (s0);
		DisplayGraphSegmentTools.checkRange (s1);
	}


	/**
	 * initialize the position tracking
	 * @param x the x-axis value for the starting position
	 * @param y the y-axis value for the starting position
	 * @param edgeX the unit length of the x-axis
	 * @param edgeY the unit length of the y-axis
	 */
	public void init (double x, double y, double edgeX, double edgeY)
	{
		this.xinc = edgeX / pointsPerAxis;
		this.yinc = edgeY / pointsPerAxis;

		this.hi = mgr.construct (x, this.yhi = y + edgeY);
		this.inc = mgr.construct (0.0, yinc);

		this.lo = mgr.construct (x, y);
		this.x0 = x; this.y0 = y;
	}
	public void init (Point lowCorner, double edgeX, double edgeY)
	{ init (lowCorner.x, lowCorner.y, edgeX, edgeY); }
	protected double x0, y0, yhi, xinc, yinc;


	/**
	 * identify vector to be transformed on next eval call
	 * @param nextX the value of x for the vector to transform next
	 */
	protected void updateVector (double nextX)
	{
		lo = mgr.construct (nextX, y0);
		hi = mgr.construct (nextX, yhi);
	}
	protected T lo, hi, inc;


	/*
	 * implementation of TypedRangeProperties, will be read from transform using VectorPlotEnabled interface
	 */

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties#getTypedLo()
	 */
	public T getTypedLo () { return lo; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties#getTypedIncrement()
	 */
	public T getTypedIncrement () { return inc; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties#getTypedHi()
	 */
	public T getTypedHi () { return hi; }


}

