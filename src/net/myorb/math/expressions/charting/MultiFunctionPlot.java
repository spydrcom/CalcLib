
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.ExpressionSpaceManager;

// IOLIB abstractions
import net.myorb.data.abstractions.Function;

// JRE
import javax.swing.JComponent;
import java.awt.image.BufferedImage;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * describe a chart plotting multiple functions
 * @author Michael Druckman
 */
public class MultiFunctionPlot extends DisplayGraph
{


	/**
	 * describe the function to plot
	 */
	public interface FunctionPlot
	{
		/**
		 * the color to use
		 * @return name of the color to use for plot
		 */
		String getColor ();

		/**
		 * get the function
		 * @return the function descriptor for the function
		 */
		Function<Double> getFunction ();
		RealFunction getRealFunction ();
	}


	/**
	 * describe all of the element of the chart
	 */
	public interface PlotDescriptors
	{
		/**
		 * the bit side of the edge of a square plotting area
		 * @return the bits in one edge of the chart area
		 */
		int getSize ();

		/**
		 * get a title for the frame
		 * @return the text of the title
		 */
		String getTitle ();

		/**
		 * get a list of x-axis values
		 * @return the list of values to use as the domain
		 */
		DisplayGraph.RealSeries getDomain ();

		/**
		 * get the list of function plot descriptors
		 * @return the list of functions
		 */
		List<FunctionPlot> getFunctionPlots ();

		/**
		 * add a plot to the list
		 * @param plot an implementer of FunctionPlot
		 */
		void addPlot (FunctionPlot plot);
		
		/**
		 * set the derivative inclusion flag
		 * @param flag the value for the flag
		 */
		void setDerivative (boolean flag);

		/**
		 * get the value of the derivative flag
		 * @return TRUE = the function derivative plot is to be included
		 */
		boolean addDerivative ();
	}


	/**
	 * built plot from descriptor object
	 * @param descriptors the descriptors of the plots
	 */
	public static void plot (PlotDescriptors descriptors)
	{
		Point scalingFactors = new Point (0, 0);
		RealSeries xAxis = descriptors.getDomain ();
		List<FunctionPlot> plotList = descriptors.getFunctionPlots ();
		DisplayGraph.PlotCollection plots = new DisplayGraph.PlotCollection ();
		BufferedImage image = chartBufferedImage (descriptors.getSize ());

		for (FunctionPlot p : plotList)
		{
			RealFunction f = p.getRealFunction ();
			if (f != null) plots.add (plot (xAxis, f, scalingFactors));
		}

		if (descriptors.addDerivative ())
			plots.add (computeDerivative (plots.get (plots.size () - 1), scalingFactors));
		add2Percent (scalingFactors);

		for (int i = 0; i < plots.size(); i++)
		{
			Color color =
				getColor (plotList.get (i).getColor ());
			plot (color, plots.get (i), scalingFactors, image);
		}

		MouseEventHandler mouse = new MouseEventHandler
			(scalingFactors.x, image.getWidth (), plotList.get (0).getRealFunction ());
		JComponent c = showImage (image, descriptors.getTitle (), mouse);
		mouse.set (c);
	}


	/**
	 * call for derivative computation and adjust scaling factors
	 * @param function the function plot points to use for computation
	 * @param scalingFactors the data collected for scaling
	 * @return the derivative plot points
	 */
	static Point.Series computeDerivative (DisplayGraph.Point.Series function, Point scalingFactors)
	{
		Point.Series derivative = ExpressionGraphing.computeDerivative (function);
		DisplayGraph.adjustScale (derivative, scalingFactors);
		return derivative;
	}


	/**
	 * bind descriptor properties
	 * @param edgeSize pixels in each edge of plot area
	 * @param title the title for the display frame
	 * @param domain the x-axis values
	 * @return the descriptor object
	 */
	public static PlotDescriptors newPlotDescriptor
		(int edgeSize, String title, DisplayGraph.RealSeries domain)
	{
		return new PlotDescriptorStorage (edgeSize, title, domain);
	}


	/**
	 * bind descriptor properties and add to descriptor list
	 * @param toPlotDescriptors the descriptor list collecting plots
	 * @param color the color to use for this plot
	 * @param function the function to plot
	 */
	public static void addFunctionPlot
		(PlotDescriptors toPlotDescriptors, String color, DisplayGraph.RealFunction function)
	{
		toPlotDescriptors.addPlot (new FunctionPlotStorage (color, function));
	}
	public static void addFunctionPlot
		(PlotDescriptors toPlotDescriptors, String color, Function<Double> function)
	{
		toPlotDescriptors.addPlot (new FunctionPlotStorage (color, function));
	}


	/**
	 * bind descriptor properties
	 * @param color the color to use for this plot
	 * @param function the function to plot
	 * @return the descriptor object
	 */
	public static FunctionPlot newFunctionPlot
		(String color, Function<Double> function)
	{
		return new FunctionPlotStorage (color, function);
	}


}


/**
 * storage for plot descriptor
 */
class PlotDescriptorStorage implements MultiFunctionPlot.PlotDescriptors
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiFunctionPlot.PlotDescriptors#getSize()
	 */
	public int getSize () { return edgeSize; }
	int edgeSize;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiFunctionPlot.PlotDescriptors#getTitle()
	 */
	public String getTitle () { return title; }
	String title;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiFunctionPlot.PlotDescriptors#getDomain()
	 */
	public DisplayGraph.RealSeries getDomain () { return domain; }
	DisplayGraph.RealSeries domain;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiFunctionPlot.PlotDescriptors#getFunctionPlots()
	 */
	public List<MultiFunctionPlot.FunctionPlot> getFunctionPlots () { return plots; }
	List<MultiFunctionPlot.FunctionPlot> plots = new ArrayList<MultiFunctionPlot.FunctionPlot>();

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiFunctionPlot.PlotDescriptors#addPlot(net.myorb.math.expressions.charting.MultiFunctionPlot.FunctionPlot)
	 */
	public void addPlot (MultiFunctionPlot.FunctionPlot plot)
	{
		plots.add (plot);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiFunctionPlot.PlotDescriptors#addDerivative()
	 */
	public boolean addDerivative () { return includeDerivative; }
	public void setDerivative (boolean flag) { includeDerivative = flag; }
	boolean includeDerivative;

	/**
	 * bind descriptor properties
	 * @param edgeSize pixels in each edge of plot area
	 * @param title the title for the display frame
	 * @param domain the x-axis values
	 */
	PlotDescriptorStorage (int edgeSize, String title, DisplayGraph.RealSeries domain)
	{
		this.includeDerivative = false;
		this.edgeSize = edgeSize;
		this.domain = domain;
		this.title = title;
	}

}


/**
 * stortage for function plot
 */
class FunctionPlotStorage implements MultiFunctionPlot.FunctionPlot
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiFunctionPlot.FunctionPlot#getColor()
	 */
	public String getColor () { return color; }
	String color;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiFunctionPlot.FunctionPlot#getFunction()
	 */
	public Function<Double> getFunction () { return function; }
	Function<Double> function;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.MultiFunctionPlot.FunctionPlot#getRealFunction()
	 */
	public DisplayGraph.RealFunction getRealFunction ()
	{
		if (function instanceof DisplayGraph.RealFunction) return (DisplayGraph.RealFunction)function;
		ExpressionSpaceManager<Double> mgr = ((ExpressionSpaceManager<Double>)function.getSpaceDescription());
		return mgr.getDataConversions ().toRealFunction (function);
	}

	/**
	 * based on color and function
	 * @param color the color to use for this plot
	 * @param function the function to plot
	 */
	FunctionPlotStorage (String color, Function<Double> function)
	{
		this.function = function;
		this.color = color;
	}

}


