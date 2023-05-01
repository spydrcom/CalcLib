
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.charting.ContourPlotProperties;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.Histogram;

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
		this.angle = new double [pointsPerAxis * pointsPerAxis];

		super.compute (descriptor, pointsPerAxis, points, range, histogram);

		DisplayGraphTypes.VectorField.Locations
			vectorPoints = new DisplayGraphTypes.VectorField.Locations ();
		collectVectorPoints (pointsPerAxis, vectorPoints);
	}


	/**
	 * collect necessary data for plot of a vector field
	 * @param axisSize the pixel count specified as edge size
	 * @param vectorPoints the point list for display
	 */
	void collectVectorPoints
		(int axisSize, DisplayGraphTypes.VectorField.Locations vectorPoints)
	{ computeDisplayFactors (axisSize); describeVectorField (vectorPoints); }


	/**
	 * compute pixel location for direction displays given vector count
	 * @param axisSize the pixel count specified as edge size
	 */
	void computeDisplayFactors (int axisSize)
	{
		this.separation = axisSize / vectorCount;
		this.blockSize	= axisSize * separation;
		this.halfBlock	= blockSize / 2;
	}
	protected int separation, blockSize, halfBlock;


	/**
	 * field data collection
	 * - data collection done for graphical interface
	 * @param plotPoints the collection of points to be added to the plot
	 */
	void describeVectorField (DisplayGraphTypes.VectorField.Locations plotPoints)
	{
		try
		{
			for
				(
					int next = halfBlock;
						next < angle.length;
						next += blockSize
				)
			{
				DisplayGraphTypes.VectorField.Locations
					row = new DisplayGraphTypes.VectorField.Locations ();

				for
					(
						int P = next + this.separation / 2,
							n = vectorCount; n > 0; n--
					)
				{
					row.add
					(
						new DisplayGraphTypes.VectorField
							(
								points[P], (Integer)range[P], angle[P]
							)
					);
					P += separation;
				}

				if (TRACE) System.out.println ("next=" + next + " : " + row);
				plotPoints.addAll (row);
			}
		}
		catch (Exception e) { e.printStackTrace (); }
	}
	boolean TRACE = true;


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

