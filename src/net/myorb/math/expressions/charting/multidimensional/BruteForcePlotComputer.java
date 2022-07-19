
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.charting.ContourPlotProperties;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.Histogram;

/**
 * a PlotComputer that calls function for every point
 * @author Michael Druckman
 */
public class BruteForcePlotComputer extends PlotMatrixTraversal
{


	public BruteForcePlotComputer (ContourPlotProperties proprties)
	{
		super (proprties);
		//proprties.getActivityDescriptor ().setProducer ("BruteForcePlotComputer");
		//proprties.getActivityDescriptor ().setDescription ("Serial Contour Plot");
	}


	/**
	 * compute iteration count for each domain point
	 * @param descriptor the descriptor for the transform
	 * @param pointsPerAxis the resolution of the transform
	 * @param points the domain points collection as evaluated
	 * @param range collection of mapped values for the domain
	 * @param histogram data collected about the range
	 */
	public void compute
		(
			DisplayGraphTypes.ContourPlotDescriptor descriptor, int pointsPerAxis,
			DisplayGraphTypes.Point[] points, Object[] range, Histogram histogram
		)
	{
		this.descriptor = descriptor;
		this.pointsPerAxis = pointsPerAxis;
		proprties.setEdgeSize (descriptor.getEdgeSize ());
		proprties.setAltEdgeSize (descriptor.getEdgeSize ());
		proprties.setLowCorner (descriptor.getLowCorner ());
		//System.out.println ("BruteForcePlotComputer invoked");
		this.points = points; this.range = range;
		this.histogram = histogram;
		traverseMatrix ();
	}
	protected int m = 100;


	/**
	 * @param x coordinates to domain point (x-axis)
	 * @param y coordinates to domain point (y-axis)
	 * @return function evaluation at coordinates
	 */
	protected int eval (double x, double y)
	{
		try { return descriptor.evaluate (x, y); }
		catch (Exception e) { return 0; }
	}
	protected DisplayGraphTypes.ContourPlotDescriptor descriptor;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotMatrixTraversal#processPoint(double, double)
	 */
	public void processPoint (double x, double y)
	{
		int n = eval (x, y);
		histogram.increase (n); range[k] = n;
		points[k++] = new DisplayGraphTypes.Point (x, y);
	}
	protected DisplayGraphTypes.Point[] points;
	protected Histogram histogram;
	protected Object[] range;
	protected int k = 0;


}

