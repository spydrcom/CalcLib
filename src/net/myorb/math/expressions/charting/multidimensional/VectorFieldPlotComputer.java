
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.charting.ContourPlotProperties;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.Histogram;

import java.util.*;

/**
 * a PlotComputer that captures both magnitude and direction
 * @author Michael Druckman
 */
public class VectorFieldPlotComputer extends BruteForcePlotComputer
{

	public VectorFieldPlotComputer (ContourPlotProperties proprties, int vectorCount)
	{ super (proprties); this.vectorCount = vectorCount; }
	protected int vectorCount;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.multidimensional.BruteForcePlotComputer#compute(net.myorb.charting.DisplayGraphTypes.ContourPlotDescriptor, int, net.myorb.charting.DisplayGraphTypes.Point[], java.lang.Object[], net.myorb.charting.Histogram)
	 */
	public void compute
		(
			DisplayGraphTypes.ContourPlotDescriptor descriptor, int pointsPerAxis,
			DisplayGraphTypes.Point[] points, Object[] range, Histogram histogram
		)
	{
		this.axisSize = pointsPerAxis;
		this.separation = pointsPerAxis / vectorCount;
		this.angle = new double [pointsPerAxis * pointsPerAxis];
		super.compute (descriptor, pointsPerAxis, points, range, histogram);
		displayVectors ();
	}
	int separation, axisSize;

	void displayVectors ()
	{
		try
		{
			int next = 0;
			while (next < angle.length)
			{
				next += separation * axisSize;
				List <Double> row = new ArrayList <Double> ();
				for (int n = axisSize; n > 0; n-=separation)
				{
					next += separation;
					row.add ( angle [next] );
				}
				System.out.println ("next=" + next + " : " + row);
			}
		}
		catch (Exception e) { e.printStackTrace (); }
		System.out.println ("display complete");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotMatrixTraversal#processPoint(double, double)
	 */
	public void processPoint (double x, double y)
	{
		angle [k] = descriptor.evaluateAngle (x, y);
		super.processPoint (x, y);
	}
	protected double[] angle;


}
