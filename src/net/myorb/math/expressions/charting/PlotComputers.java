
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.charting.multidimensional.*;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.charting.DisplayGraphTypes.ContourPlotDescriptor;
import net.myorb.charting.DisplayGraphTypes.SurfaceDescription3D;
import net.myorb.charting.DisplayGraphTypes.PlotComputer;
import net.myorb.charting.DisplayGraphTypes.Point;

import net.myorb.charting.Histogram;

/**
 * algorithms for collecting plot points
 * @author Michael Druckman
 */
public class PlotComputers
{


	/**
	 * breakdown plot computation processing
	 */
	public interface RealizationTracking
	{
		/**
		 * @param remaining size of computation job
		 */
		void setRemaining (int remaining);

		/**
		 * @param portion amount of job most recently completed
		 */
		void reduceRemaining (int portion);
	}


	/**
	 * describe results of 3D transform
	 */
	public interface TransformResultsCollection
		extends SurfaceDescription3D
	{}


	/**
	 * cause results generation by transform
	 */
	public interface TransformProcessing
	{
		/**
		 * @return results of transform
		 */
		TransformResultsCollection executeTransform ();
	}


	/**
	 * @param proprties a Plot Properties object describing the plot
	 * @return a PlotComputer that calls function for every point
	 */
	public static PlotComputer getBruteForcePlotComputer (ContourPlotProperties proprties)
	{
		return new PlotComputer ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.charting.DisplayGraph3D.PlotComputer#computeRange(net.myorb.charting.DisplayGraphTypes.ContourPlotDescriptor, int, net.myorb.charting.DisplayGraphTypes.Point[], java.lang.Object[], net.myorb.charting.Histogram)
			 */
			public void computeRange
				(
					ContourPlotDescriptor descriptor, int pointsPerAxis,
					Point[] points, Object[] range,
					Histogram histogram
				)
			{
				new BruteForcePlotComputer
					(
						proprties
					)
				.compute
					(
						descriptor, pointsPerAxis,
						points, range, histogram
					);
			}
		};
	}


	/**
	 * @param proprties a Plot Properties object describing the plot
	 * @return a PlotComputer that uses vector processing for function calls
	 */
	public static PlotComputer getVectorPlotComputer (ContourPlotProperties proprties)
	{
		return new PlotComputer ()
		{
			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.charting.DisplayGraph3D.PlotComputer#computeRange(net.myorb.charting.DisplayGraphTypes.ContourPlotDescriptor, int, net.myorb.charting.DisplayGraphTypes.Point[], java.lang.Object[], net.myorb.charting.Histogram)
			 */
			public void computeRange
				(
					ContourPlotDescriptor descriptor, int pointsPerAxis,
					Point[] points, Object[] range,
					Histogram histogram
				)
			{
				TransformProcessing processor = (TransformProcessing) descriptor;
				TransformResultsCollection transformResults = processor.executeTransform ();
				map (points, range, histogram, transformResults, descriptor.getMultiplier (), pointsPerAxis);
			}
		};
	}


	/**
	 * @param points the (x,y) coordinates of each point
	 * @param range the z axis value computed for each point
	 * @param histogram a histogram collecting statistics about the range value
	 * @param transformResults the results collected from the computations
	 * @param multiplier a scaling multiplier for the z axis
	 * @param pointsPerAxis the number of points per axis
	 */
	public static void map
		(
			Point[] points, Object[] range, Histogram histogram, 
			TransformResultsCollection transformResults,
			double multiplier, int pointsPerAxis
		)
	{
		double magnitude;
		double[][] X = transformResults.getX ();
		double[][] Y = transformResults.getY ();
		double[][] Z = transformResults.getZ ();

		for (int i = 0, k = 0; i < pointsPerAxis; i++)
		{
			for (int j = 0; j < pointsPerAxis; j++)
			{
				magnitude = multiplier * Z[i][j];
				points[k] = new Point (X[i][j], Y[i][j]);
				histogram.increase ((long) magnitude);
				range[k++] = (int) magnitude;
			}
		}
	}


	/**
	 * get a processor for the vector engine
	 * @param descriptor a Plot Properties object describing the plot
	 * @param environment the common environment object for this run
	 * @param <T> the data type used by the environment
	 * @return the vector Transform Processing object
	 */
	public static <T> TransformProcessing getVectoredTransformProcessing
		(
			ContourPlotProperties descriptor,
			Environment<T> environment
		)
	{
		return new VectoredComputer<T> (descriptor, environment);
	}


	/**
	 * get a processor for Serial Calculation
	 * @param descriptor a Plot Properties object describing the plot
	 * @return the vector Transform Processing object
	 */
	public static TransformProcessing getSerialCalculationProcessing
		(
			ContourPlotProperties descriptor
		)
	{
		return new SerialCalculationComputer (descriptor);
	}


}

