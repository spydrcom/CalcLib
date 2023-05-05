
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.charting.ContourPlotProperties;
import net.myorb.math.expressions.charting.Plot3DVectorField;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.Histogram;

/**
 * a PlotComputer that captures both magnitude and direction
 * @author Michael Druckman
 */
public class VectorFieldPlotComputer extends BruteForcePlotComputer
{


	public VectorFieldPlotComputer
	(ContourPlotProperties proprties, int vectorCount, Plot3DVectorField <?> plotter)
	{ super (proprties); this.vectorCount = vectorCount; this.plotter = plotter; }
	protected Plot3DVectorField <?> plotter;
	protected int vectorCount;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.multidimensional.BruteForcePlotComputer#compute(net.myorb.charting.DisplayGraphTypes.ContourPlotDescriptor, int, net.myorb.charting.DisplayGraphTypes.Point[], java.lang.Object[], net.myorb.charting.Histogram)
	 */
	public void compute
		(
			DisplayGraphTypes.ContourPlotDescriptor descriptor, int pointsPerAxis,
			DisplayGraphTypes.Point [] points, Object [] range, Histogram histogram
		)
	{
		this.angle = new double [ pointsPerAxis * pointsPerAxis ];
		super.compute (descriptor, pointsPerAxis, points, range, histogram);
		( (Plot3DVectorField <?>) descriptor ).setVectorPoints
				( fieldDescription (pointsPerAxis) );
	}
	Plot3DVectorField.VectorFieldPoints fieldDescription (int pointsPerAxis)
	{
		Plot3DVectorField.VectorFieldPoints
			vectorPoints = Plot3DVectorField.pointsList ();
		// collect necessary data for plot of a vector field
		computeDisplayFactors (pointsPerAxis);
		describeVectorField (vectorPoints);
		return vectorPoints;
	}


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
	boolean TRACE = false;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotMatrixTraversal#processPoint(double, double)
	 */
	public void processPoint (double x, double y)
	{
		ValueManager.GenericValue
			functionResult = plotter.evaluate2DCall (x, y);
		int n = plotter.evaluateMagnitude (functionResult);
		angle [k] = plotter.evaluateAngle (functionResult);
		points[k] = new DisplayGraphTypes.Point (x, y);
		histogram.increase (n); range[k++] = n;
	}
	protected double[] angle;


}

