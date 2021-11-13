
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.TokenParser;
import net.myorb.math.expressions.VectorPlotEnabled;
import net.myorb.math.expressions.charting.PlotComputers;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Subroutine;

import net.myorb.math.computational.SurfaceAnalyzer;

import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.rinearn.SurfacePlotter;

/**
 * plot preparation for 3D mesh/membrane/contour type plots
 * @param <T> data type for plot
 * @author Michael Druckman
 */
public class HighDefinitionPlots<T>
{


	public HighDefinitionPlots
	(Environment<T> environment) { this.environment = environment; }
	protected Environment<T> environment;


	/**
	 * populate contour descriptor
	 * @param plot the plot object to be used
	 * @param functionName name of the function symbol to be plotted
	 * @param lowCorner (x,y) coordinates of the low left corner of the plot
	 * @param edgeX the length of the edge of the X axis
	 * @param edgeY the length of the edge of the Y axis
	 * @param multiplier normalize for integer range
	 */
	public void prepareAndShow
		(
			Plot3D<T> plot, String functionName,
			Point lowCorner, double edgeX, double edgeY,
			int multiplier
		)
	{
		plot.setLowCorner (lowCorner);
		plot.setEdgeSize ((float) edgeX);
		plot.setAltEdgeSize ((float) edgeY);
		plot.setMultiplier (multiplier);
		plot.show (functionName);
	}


	/**
	 * @param plot the plot object to be used
	 * @param function the function symbol to be plotted
	 * @param functionName name of the function symbol to be plotted
	 * @param lowCorner (x,y) coordinates of the low left corner of the plot
	 * @param edgeX the length of the edge of the X axis
	 * @param edgeY the length of the edge of the Y axis
	 * @param multiplier normalize for integer range
	 */
	public void prepareAndShow
		(
			Plot3D<T> plot,
			Subroutine<T> function, String functionName,
			Point lowCorner, double edgeX, double edgeY,
			int multiplier
		)
	{
		plot.put ("EquationProfile", function.formatFullProfile (functionName, false));
		plot.put ("EquationBody", TokenParser.toString (function.getFunctionTokens ()));
		prepareAndShow (plot, functionName, lowCorner, edgeX, edgeY, multiplier);
	}


	/**
	 * @param function the function symbol to be plotted
	 * @param functionName name of the function symbol to be plotted
	 * @param lowCorner (x,y) coordinates of the low left corner of the plot
	 * @param edge the length of the edge of each axis
	 * @param multiplier normalize for integer range
	 */
	public void prepareAndShow
		(
			Subroutine<T> function, String functionName,
			Point lowCorner, double edge, double multiplier
		)
	{
		prepareAndShow
		(
			new Plot3DContour<T> (function), function, functionName,
			lowCorner, edge, edge, (int) multiplier
		);
	}


	/**
	 * mesh plot for function as 3D surface.
	 *  Subroutine call per function evaluation.
	 * @param function the function symbol to be plotted
	 * @param functionName name of the function symbol to be plotted
	 * @param lowCorner (x,y) coordinates of the low left corner of the plot
	 * @param edgeX the length of the edge of the X axis
	 * @param edgeY the length of the edge of the Y axis
	 */
	public void produceMeshPlot
		(
			Subroutine<T> function, String functionName,
			Point lowCorner, double edgeX, double edgeY
		)
	{
		prepareAndShow
		(
			new Plot3DMesh<T> (function), function, functionName,
			lowCorner, edgeX, edgeY, 1
		);
	}


	/*
	 * preparation algorithms for vector-enabled transforms
	 */


	/**
	 * configure a plot for a given transform and plot type
	 * @param functionSymbol symbol that will ace as transform
	 * @param plot the plot algorithm wrapper for the transform to use
	 * @return the transform in a plot wrapper
	 */
	public Plot3D<T> transformForPlot
		(SymbolMap.Named functionSymbol, Plot3D<T> plot)
	{
		MultiDimensionalVectored<T> transform =
			new MultiDimensionalVectored<T> (functionSymbol, environment);
		transform.setResolution (plot.getPlotEdgeSize ());
		plot.setEquation (transform);
		return plot;
	}


	/**
	 * produce requested plots of a surface
	 * @param surfaceDescription a multi-dimensional model of the data
	 * @param plotter a plotter object for the described surface
	 */
	public void produceSurfacePlot
		(
			PlotComputers.TransformResultsCollection surfaceDescription,
			SurfacePlotter plotter
		)
	{
		SurfaceAnalyzer analyzer = new SurfaceAnalyzer (surfaceDescription);

		/*
		 * currently - plot all three complex perspectives + contour
		 * 			 - real - a plot of the real part of resulting computation
		 *  		 - imaginary - a plot of the imaginary part of resulting computation
		 *  		 - magnitude - a plot of real^2+imag^2 of resulting computation
		 *  		 - contour - a 2D contour plot of the magnitude computations
		 * additionally produce a text the result of the smallest magnitudes seen
		 */

		//TODO: enable selection of just required plot(s) / report(s)

		plotter.plot ("real", surfaceDescription.getX (), surfaceDescription.getY (), surfaceDescription.getZ (0));
		plotter.plot ("imaginary", surfaceDescription.getX (), surfaceDescription.getY (), surfaceDescription.getZ (1));

		plotter.plot
		(
			"magnitude", surfaceDescription.getX (), surfaceDescription.getY (), analyzer.computeSurfaceMagnitude ()
		);
		analyzer.report ();
	}


	/**
	 * for functions implementing VectorPlotEnabled
	 * @param functionSymbol the function symbol to be plotted
	 * @param lowCorner (x,y) coordinates of the low left corner of the plot
	 * @param edgeX the length of the edge of the X axis
	 * @param edgeY the length of the edge of the Y axis
	 */
	public void produceVectorEnabledPlot
		(
			SymbolMap.Named functionSymbol,
			Point lowCorner, double edgeX, double edgeY
		)
	{
		Plot3D<T> plot = transformForPlot (functionSymbol, new Plot3DMesh<T> ());
		MultiDimensionalVectored<T> transform = plot.getMultiDimensionalVectored ();
		plot.setLowCorner (lowCorner); plot.setEdgeSize ((float) edgeX); plot.setAltEdgeSize ((float) edgeY);
		executeTransform (plot, functionSymbol.getName (), transform.getEnvironment ());
	}

	/**
	 * @param plot description of the plot from the request
	 * @param name the name of the function to be used as a title
	 * @param environment the system environment object
	 */
	void executeTransform (Plot3D<T> plot, String name, Environment<T> environment)
	{
		PlotComputers.TransformResultsCollection surfaceDescription =
			PlotComputers.getVectoredTransformProcessing (plot, environment).executeTransform ();
		SurfacePlotter plotter = new SurfacePlotter (name, plot);
		produceSurfacePlot (surfaceDescription, plotter);
	}


	/*
	 * select plot mechanism based on type of transform
	 */


	/**
	 * @param functionSymbol the function symbol to be plotted
	 * @param lowCorner (x,y) coordinates of the low left corner of the plot
	 * @param edgeX the length of the edge of the X axis
	 * @param edgeY the length of the edge of the Y axis
	 */
	public void singlePlotOf3DValues
		(
			SymbolMap.Named functionSymbol,
			Point lowCorner, double edgeX,
			double edgeY
		)
	{
		if (functionSymbol instanceof VectorPlotEnabled)
		{
			produceVectorEnabledPlot
			(
				functionSymbol,
				lowCorner, edgeX, edgeY
			);
		}
		else
		{
			produceMeshPlot
			(
				Subroutine.cast (functionSymbol),
				functionSymbol.getName (),
				lowCorner, edgeX, edgeY
			);
		}
	}


	/**
	 * @param functionSymbol the function symbol to be plotted
	 * @param lowCorner (x,y) coordinates of the low left corner of the plot
	 * @param edge the length of the edges of the plot along each of the axis
	 * @param multiplier a multiplier for the function values
	 */
	public void contourPlotOf3DValues
		(
			SymbolMap.Named functionSymbol,
			Point lowCorner, double edge,
			double multiplier
		)
	{
		String name = functionSymbol.getName ();
		if (functionSymbol instanceof VectorPlotEnabled)
		{
			prepareAndShow
			(
				transformForPlot (functionSymbol, new Plot3DContour<T> ()),
				name, lowCorner, edge, edge, (int) multiplier
			);
		}
		else
		{
			prepareAndShow
			(
				Subroutine.cast (functionSymbol),
				name, lowCorner, edge, multiplier
			);
		}
	}


}

