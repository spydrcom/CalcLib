
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.charting.ContourPlotProperties;

import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.sitstat.RealizationTracking;
import net.myorb.sitstat.Activity;

/**
 * brute force traversal to calculate transform of every point of 2D matrix
 * @author Michael Druckman
 */
class PlotMatrixTraversal extends ResultCollector
{


	protected PlotMatrixTraversal (ContourPlotProperties proprties)
	{
		super (proprties);
		this.tracking = proprties;
	}
	protected RealizationTracking tracking;


	/**
	 * @param x coordinate (x-axis) of point to transform
	 * @param y coordinate (y-axis) of point to transform
	 */
	public void processPoint (double x, double y) {}


	/**
	 * @param x the x-axis coordinate for this y-axis column
	 * @param y0 the low value on the y-axis for this column
	 * @param incrementY the step between points along axis
	 */
	public void processYAxis (double x, double y0, double incrementY)
	{
		double y = y0; 
		for (y_index = 0; y_index < pointsPerAxis; y_index++)
		{
			processPoint (x, y);
			y += incrementY;
		}
	}


	/**
	 * @param x0 x-axis component of starting point in domain
	 * @param incrementX the step along the x-axis for the transform
	 * @param y0 y-axis component of starting point in domain
	 * @param incrementY the step along the y-axis
	 */
	public void traverseMatrix
		(
			double x0, double incrementX,
			double y0, double incrementY
		)
	{
		double x = x0;
		describePlot (tracking.getActivityDescriptor ());
		tracking.setRemaining (pointsPerAxis*pointsPerAxis);
		for (x_index = 0; x_index < pointsPerAxis; x_index++)
		{
			processYAxis (x, y0, incrementY);
			tracking.reduceRemaining (pointsPerAxis);
			x += incrementX;
		}
	}
	void describePlot (Activity activity)
	{
		activity.setMechanism ("PlotMatrixTraversal.traverseMatrix");
	}


	/**
	 * inner (x-axis) and outer (y-axis) loops to visit all points
	 */
	public void traverseMatrix ()
	{
		Point lowCorner =
				proprties.getLowCorner ();
		double x0 = lowCorner.x, y0 = lowCorner.y,
			incrementX = proprties.getEdgeSize () / pointsPerAxis,
			incrementY = proprties.getAltEdgeSize () / pointsPerAxis;
		traverseMatrix (x0, incrementX, y0, incrementY);
	}


	/**
	 * @param x value of x-axis coordinate
	 * @param y value of y-axis coordinate
	 */
	public void identifyDomainPoint (double x, double y)
	{
		X[x_index][y_index] = x; Y[x_index][y_index] = y;
	}
	protected int x_index, y_index;


	/**
	 * @param x value of x-axis coordinate
	 * @param y value of y-axis coordinate
	 * @param z transform value for domain point
	 */
	public void identify3DPoint (double x, double y, double z)
	{
		identifyDomainPoint (x, y); Z[x_index][y_index] = z;
	}


}

