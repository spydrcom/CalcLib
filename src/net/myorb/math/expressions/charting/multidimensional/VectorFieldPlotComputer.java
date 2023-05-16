
package net.myorb.math.expressions.charting.multidimensional;

import net.myorb.math.expressions.charting.Plot3DVectorField;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.Histogram;

/**
 * a PlotComputer that captures both magnitude and direction
 * @author Michael Druckman
 */
public class VectorFieldPlotComputer extends BruteForcePlotComputer
{


	public VectorFieldPlotComputer (Plot3DVectorField <?> proprties)
	{
		super (proprties);
		this.vectorCount = proprties.getVectorCount ();
		this.vectorFieldProprties = proprties;
	}
	protected Plot3DVectorField <?> vectorFieldProprties;
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
		super.compute (descriptor, pointsPerAxis, points, range, histogram);
		this.vectorFieldProprties.buildLegendWidgetsFor (histogram);
		this.evaluateFieldDescription (pointsPerAxis);
	}


	/**
	 * identify points that are to be plotted as direction indicators
	 * @param pointsPerAxis the pixel count specified as edge size
	 */
	void evaluateFieldDescription (int pointsPerAxis)
	{
		Plot3DVectorField.VectorFieldPoints
			vectorPoints = Plot3DVectorField.pointsList ();
		// collect all necessary data for plot of a vector field
		computeDisplayFactors (pointsPerAxis); describeVectorField (vectorPoints);
		( (Plot3DVectorField <?>) descriptor ).setVectorPoints (vectorPoints);
	}


	/**
	 * compute pixel location for direction displays given vector count
	 * @param axisSize the pixel count specified as edge size
	 */
	void computeDisplayFactors (int axisSize)
	{
		this.separation = axisSize / this.vectorCount;
		this.blockSize	= axisSize * this.separation;
		this.halfBlock	= this.blockSize / 2;
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
					int next  = this.halfBlock ;
						next  < this.vectorFieldProprties.getPixelBufferSize () ;
						next += this.blockSize
				)
			{
				DisplayGraphTypes.VectorField.Locations
					row = new DisplayGraphTypes.VectorField.Locations ();

				for
					(
						int P = next + this.separation / 2,
							n = this.vectorCount ; n > 0 ; n--
					)
				{
					row.add
					(
						new DisplayGraphTypes.VectorField
							(
								this.points [P], (Integer) this.range [P],
								this.vectorFieldProprties.getAngleFrom (P)
							)
					);
					P += this.separation;
				}

				if (TRACE) System.out.println ("next=" + next + " : " + row);
				plotPoints.addAll (row);
			}
		}
		catch (Exception e) { e.printStackTrace (); }
	}
	boolean TRACE = false;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.multidimensional.BruteForcePlotComputer#executeContourEvaluation(double, double)
	 */
	public int executeContourEvaluation (double x, double y)
	{
		return this.vectorFieldProprties.executeContourEvaluation
				( x, y, this.getNextEvaluationIndex () );
	}


}

