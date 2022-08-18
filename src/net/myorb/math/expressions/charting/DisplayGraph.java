
package net.myorb.math.expressions.charting;

import net.myorb.charting.Histogram;
import net.myorb.charting.PlotLegend;

import javax.swing.JComponent;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * chart drawing primitives
 * @author Michael Druckman
 */
public class DisplayGraph extends DisplayGraphAtomic
{


	/**
	 * a trigger type that just supports legend data
	 * @param <T> data type used in plots
	 */
	public static class SimpleLegend <T> extends MouseSampleTrigger <T>
	{

		/**
		 * properties that are required to define a simple legend
		 */
		public interface LegendProperties
		{

			/**
			 * @return list of function names for y-axis f(ID)
			 */
			String [] getPlotSymbols ();

			/**
			 * @return x-axis variable defining the domain
			 */
			String getVariable ();

		}

		/**
		 * provide a legend object for a plot
		 * @param properties the properties to build from
		 * @return the simple legend object built
		 * @param <T> data type used in plots
		 */
		public static <T> SimpleLegend <T> buildLegendFor
				(LegendProperties properties)
		{
			SimpleLegend <T> legend = new SimpleLegend <T> ();
			legend.setDisplay (getPlotLegend (properties));
			return legend;
		}

		/**
		 * implementation of the SampleDisplay interface
		 * @param properties the properties to build a legend from
		 * @return the sample display implementation
		 */
		public static PlotLegend.SampleDisplay getPlotLegend
				(LegendProperties properties)
		{
			return new PlotLegend.SampleDisplay ()
			{
				public String getVariable ()
				{ return properties.getVariable (); }
				public String [] getPlotExpressions ()
				{ return properties.getPlotSymbols (); }
				public void display (String x, String [] samples) {}
				public void setVariable (String variable) {}
				public void showLegend () {}
			};
		}

		private static final long serialVersionUID = -3457937988434277359L;
	}


	/*
	 * scaling
	 */


	/**
	 * submit points to consider in scaling for the display
	 * @param points the list of point for inclusion in the scaling computations
	 * @param scaleFactors the scale factors held as a point
	 */
	public static void adjustScale (Point.Series points, Point scaleFactors)
	{
		double d;
		for (Point p : points)
		{
			if (p.outOfRange) continue;
			if (scaleFactors.x < (d = abs (p.x))) scaleFactors.x = d;
			if (scaleFactors.y < (d = abs (p.y))) scaleFactors.y = d;
		}
	}


	/**
	 * run the initial set of points giving an minitial scale factor point
	 * @param points the list of point for inclusion in the scaling computations
	 * @return an initial scale factor point
	 */
	public static Point scale (Point.Series points)
	{
		Point maximums = new Point (0, 0);
		adjustScale (points, maximums);
		return maximums;
	}
	static double abs (double x) { return x < 0? -x : x; }


	/**
	 * scale a set of points
	 * transforming them to coordinates within the display scale
	 * @param points the list of point for inclusion in the scaling computations
	 * @param scaleFactors a scale factor point to use transforming the display data
	 * @param edge the pixel size of an edge of the chart
	 * @return a transformed set of points
	 */
	public static Point.Series scale4Quad
	(Point.Series points, Point scaleFactors, int edge)
	{
		double zeroOffset = ((double)edge) / 2;
		Point.Series scaled = new Point.Series ();
		for (Point p : points)
		{
			Point adj = new Point ();
			adj.x = zeroOffset * p.x / scaleFactors.x + zeroOffset;
			adj.y = zeroOffset - zeroOffset * p.y / scaleFactors.y;
			scaled.add (adj);
		}
		return scaled;
	}
	public static Point.Series scale1Quad
	(Point.Series points, Point scaleFactors, int edge)
	{
		Point.Series scaled = new Point.Series ();
		for (Point p : points)
		{
			Point adj = new Point ();
			adj.x = edge * p.x / scaleFactors.x;
			adj.y = edge - edge * p.y / scaleFactors.y;
			scaled.add (adj);
		}
		return scaled;
	}


	/**
	 * scale points relative to off-axis area
	 * @param points the X/Y domain coordinates
	 * @param lowCorner the X/Y values of the low left corner
	 * @param edgeSize the size of each edge of the area
	 * @param width the width of the display area
	 * @return the scaled list of points
	 */
	public static Point.Series scaleOffAxis
		(
			Point.Series points, Point lowCorner, double edgeSize, int width
		)
	{
		Point scaledPoint;
		double pixelsPer = width / edgeSize;
		Point.Series scaled = new Point.Series ();
		for (Point p : points)
		{
			scale
			(p, lowCorner, width, pixelsPer, scaledPoint = new Point ());
			scaled.add (scaledPoint);
		}
		return scaled;
	}
	public static void scale
	(Point from, Point lowCorner, int width, double pixelsPer, Point to)
	{
		to.x = pixelsPer * (from.x - lowCorner.x);
		to.y = width - pixelsPer * (from.y - lowCorner.y);
	}


	/*
	 * plotting
	 */


	/**
	 * plot a set of points in the chart image
	 * @param color the color to use for this plot
	 * @param points the points to be added to the display
	 * @param scaleFactors the scale factors to apply to plot points
	 * @param image the image being updated
	 */
	public static void plot
	(Color color, Point.Series points, Point scaleFactors, BufferedImage image)
	{
		int edge = image.getWidth ();
		boolean skipping = false, newSegment = false;
		Graphics2D g = image.createGraphics ();  g.setColor (color);
		Point.Series scaled = scale4Quad (points, scaleFactors, edge);
	    int n = 0, remaining = scaled.size ();
	    Point current, next;

	    while (true)
	    {
	    	current = scaled.get (n++);
	    	if (!current.outOfRange) break;
	    	if (--remaining == 0) break;
	    }

    	if (remaining < 3) throw new RuntimeException ("Too few points in range");

	    for (int i = n; i < scaled.size (); i++)
	    {
	    	next = scaled.get (i);

	    	if (skipping)
	    	{
	    		if (!next.outOfRange)
	    		{
	    			newSegment = true;
		    		skipping = false;
	    		}
	    	}
	    	else if (next.outOfRange) skipping = true;

	    	if (!skipping)
	    	{
	    		if (!newSegment)
	    		{
			    	g.drawLine
			    	(
			    		(int)current.x, (int)current.y,
			    		   (int)next.x, (int)next.y
			    	);
	    			//System.out.println (current + " : " + next);
	    		}  else newSegment = false;
		    	current = next;
	    	}
	    }

	    g.dispose ();
	}


	/**
	 * produce scatter plot
	 * @param color the color to draw
	 * @param points the list of points
	 * @param scaleFactors the scaling factors
	 * @param image the image object
	 */
	public static void pointPlot
	(Color color, Point.Series points, Point scaleFactors, BufferedImage image)
	{
		add2Percent (scaleFactors);
		Point.Series scaled = scale4Quad (points, scaleFactors, image.getWidth ());

		Graphics2D g = image.createGraphics ();
	    g.setColor (color);
	    
	    for (int i = 0; i < scaled.size (); i++)
	    {
	    	markPoint (scaled.get (i), g);
	    }

	    g.dispose ();
	}
	public static void markPoint (Point p, Graphics2D g)
	{
    	g.drawLine
    	(
    		(int)p.x-4, (int)p.y,
    		(int)p.x+4, (int)p.y
    	);
    	g.drawLine
    	(
       		(int)p.x, (int)p.y-4,
       		(int)p.x, (int)p.y+4
    	);
	}


	/**
	 * produce bar chart
	 * @param color the color for the bars
	 * @param points the data points to be plotted
	 * @param scaleFactors the scaling to be applied
	 * @param image the display image object
	 */
	public static void barPlot
	(Color color, Point.Series points, Point scaleFactors, BufferedImage image)
	{
		Graphics2D g = image.createGraphics ();
		int size = image.getWidth ();

		g.setColor (Color.gray);
	    g.fillRect (0, 0, size, size);

	    add2Percent (scaleFactors);
		Point.Series scaled = scale1Quad (points, scaleFactors, image.getWidth ());

	    g.setColor (color);
	    
	    for (int i = 0; i < scaled.size (); i++)
	    {
	    	drawBar (scaled.get (i), g);
	    }

	    g.dispose ();
	}
	public static void drawBar (Point p, Graphics2D g)
	{
    	g.drawLine
    	(
    		(int)p.x, (int)p.y,
    		(int)p.x, (int)0
    	);
	}


	/**
	 * build a plot
	 * @param domain the x-axis values list
	 * @param transform the function to be plotted
	 * @param scalingFactors the collection of scaling factors
	 * @return list of points in the plot
	 */
	public static Point.Series plot
	(RealSeries domain, RealFunction transform, Point scalingFactors)
	{
		double value, y;
		Point.Series points = new Point.Series ();
		for (double x : domain)
		{
			y = transform.eval (x);
			if ((value = abs (x)) > scalingFactors.x) scalingFactors.x = value;
			if ((value = abs (y)) > scalingFactors.y) scalingFactors.y = value;
			points.add (new Point (x, y));
		}
		return points;
	}


	/**
	 * simple single function plot
	 * @param image the image buffer for the plot
	 * @param color the color to use for the function plot
	 * @param xAxis the x-axis values list for the domain
	 * @param transform the function to plot
	 */
	public static void plot
		(
			BufferedImage image, Color color,
			RealSeries xAxis, RealFunction transform
		)
	{
		Point scalingFactors = new Point (0, 0);
		Point.Series funcPlot = plot (xAxis, transform, scalingFactors);
		plot (color, funcPlot, scalingFactors, image);
	}


	/**
	 * simple version of a single line plot
	 * @param color the color to use for the line
	 * @param funcPlot the function points to plot
	 * @param title the title for the frame
	 */
	public static void plot
		(
			String color, Point.Series funcPlot, String title
		)
	{
		Point scalingFactors = scale (funcPlot);
		BufferedImage image = chartBufferedImage (DEFAULT_PLOT_SIZE);
		plot (getColor (color), funcPlot, scalingFactors, image);
		showImage (image, title, null);
	}


	/**
	 * prepare scatter plot
	 * @param color the color to draw
	 * @param funcPlot the list of points
	 * @param title a title for the frame
	 */
	public static void pointPlot
		(
			String color, Point.Series funcPlot, String title
		)
	{
		Point scalingFactors = scale (funcPlot);
		BufferedImage image = chartBufferedImage (DEFAULT_PLOT_SIZE);
		pointPlot (getColor (color), funcPlot, scalingFactors, image);
		showImage (image, title, null);
	}


	/**
	 * plot data points with
	 *  the computed regression curve
	 * @param dataColor the color for the data points
	 * @param dataPoints the data points used to compute regression
	 * @param funcColor the color for the regression plot
	 * @param funcPlot the regression cureve to plot
	 * @param title title for the frame
	 */
	public static void regressionPlot
		(
			String dataColor, Point.Series dataPoints,
			String funcColor, Point.Series funcPlot,
			String title
		)
	{
		Point scalingFactors;
		adjustScale (dataPoints, scalingFactors = scale (funcPlot));
		BufferedImage image = chartBufferedImage (DEFAULT_PLOT_SIZE);
		pointPlot (getColor (dataColor), dataPoints, scalingFactors, image);
		plot (getColor (funcColor), funcPlot, scalingFactors, image);
		showImage (image, title, null);
	}


	/**
	 * format a bar chart
	 * @param color the color for the bars
	 * @param funcPlot the data points to plot
	 * @param title title for the frame
	 */
	public static void barChart
		(
			String color, Point.Series funcPlot, String title
		)
	{
		Point scalingFactors = scale (funcPlot);
		BufferedImage image = createBufferedImage (DEFAULT_PLOT_SIZE-MARGIN);
		barPlot (getColor (color), funcPlot, scalingFactors, image);
		showImage (image, title, null);
	}


	/**
	 * bar chart of equally spaced data points
	 * @param data the levels at equally spaced points
	 * @param title title for the frame
	 */
	public static void histogramBarChart (Histogram data, String title)
	{
		getChartLibrary ().barChart (data.forDisplay (MAX_BARS), title);
	}
	public static final int MAX_BARS = 40;


	/**
	 * treat histogram as a plot
	 * @param data the levels at equally spaced points
	 * @param title title for the frame
	 */
	public static void histogramPlot (Histogram data, String title)
	{
		Colors colors = new Colors (); colors.add (Color.RED);
		PlotCollection plotList = new PlotCollection (); plotList.add (data.forDisplay (0));
		getChartLibrary ().singlePlotWithAxis (Color.RED, plotList, "Display Spectrum", "Value", null);
//		pointPlot ("RED", data.forDisplay (0), title);
//		plot ("RED", data.forDisplay (0), title);
	}


	/**
	 * simple version of a single line plot
	 * @param color the color to use for the line
	 * @param domain the list of domain value to be graphed
	 * @param funcPlot the function points to plot
	 * @param title the title for the frame
	 */
	public static void plot
		(
			String color, RealSeries domain, RealSeries funcPlot, String title
		)
	{
		Point.Series values = pointsFor (domain, funcPlot);
		plot (color, values, title);
	}


	/**
	 * simple version of a multi-line plot.
	 *  background is selected by caller in image parameter
	 * @param image a buffered image holding selected background
	 * @param colors the color to use for each line of the plot
	 * @param funcPlot the function points to plot for each
	 * @param scalingFactors maxX and maxY to scale plot
	 */
	public static void prepareMultiPlot
		(
			BufferedImage image, Colors colors,
			PlotCollection funcPlot, Point scalingFactors
		)
	{
		for (Point.Series plot : funcPlot)
		{
			adjustScale (plot, scalingFactors);
		}

		add2Percent (scalingFactors);

		for (int i = 0; i < colors.size(); i++)
		{
			plot (colors.get (i), funcPlot.get (i), scalingFactors, image);
		}
	}


	/**
	 * simple version of a multi-line plot.
	 *  background is selected by caller in image parameter
	 * @param image a buffered image holding selected background
	 * @param colors the color to use for each line of the plot
	 * @param funcPlot the function points to plot for each
	 * @param title the title for the frame
	 * @param f function implementation
	 */
	public static void plot
		(
			BufferedImage image, Colors colors,
			PlotCollection funcPlot, String title,
			RealFunction f
		)
	{
		Point scalingFactors = new Point (0, 0);
		prepareMultiPlot (image, colors, funcPlot, scalingFactors);
		MouseMotionHandler mouse = new MouseMotionHandler (scalingFactors.x, image.getWidth (), f);
		JComponent c = showImage (image, title, mouse);
		mouse.set (c);
	}
	public static void plot
		(
			BufferedImage image, Colors colors,
			PlotCollection funcPlot, String title, String expression,
			RealFunction f
		)
	{
		Point scalingFactors = new Point (0, 0);
		prepareMultiPlot (image, colors, funcPlot, scalingFactors);
		MouseMotionHandler mouse = new MouseMotionHandler (scalingFactors.x, image.getWidth (), f);
		JComponent c = showImage (image, title, mouse);
		mouse.set (c);
	}


	/**
	 * multi-overlay plot with legend interface
	 * @param image a buffered image holding selected background
	 * @param colors the color to use for each line of the plot
	 * @param funcPlot the function points to plot for each
	 * @param title the title for the frame
	 * @param mouse mouse events listener
	 */
	@SuppressWarnings("rawtypes")
	public static void plotMulti
		(
			BufferedImage image, Colors colors,
			PlotCollection funcPlot, String title,
			MouseSampleTrigger mouse
		)
	{
		Point scalingFactors = new Point (0, 0);
		prepareMultiPlot (image, colors, funcPlot, scalingFactors);
		JComponent plot = showImage (image, title, mouse);

		if (mouse != null)
		{
			mouse.setScale (scalingFactors.x, image.getWidth ());
			mouse.set (plot);
		}
	}


	/**
	 * simple version of a multi-line plot.
	 *  background is completely black for contrast
	 * @param colors the color to use for each line of the plot
	 * @param funcPlot the function points to plot for each
	 * @param title the title for the frame
	 */
	public static void plot
		(
			Colors colors, PlotCollection funcPlot, String title
		)
	{
		plot (createBufferedImage (DEFAULT_PLOT_SIZE), colors, funcPlot, title, null);
	}


	/**
	 * simple version of a multi-line plot.
	 *  background has axis lines intersecting at x,y = 0,0
	 * @param colors the color to use for each line of the plot
	 * @param funcPlot the function points to plot for each
	 * @param title the title for the frame
	 * @param f function implementation
	 */
	public static void plotWithAxis
		(
			Colors colors, PlotCollection funcPlot, String title, RealFunction f
		)
	{
		plot (chartBufferedImage (DEFAULT_PLOT_SIZE), colors, funcPlot, title, f);
	}
	public static void plotWithAxis
		(
			Colors colors, PlotCollection funcPlot, String title, String expression, RealFunction f
		)
	{
		plot (chartBufferedImage (DEFAULT_PLOT_SIZE), colors, funcPlot, title, expression, f);
	}


	/**
	 * multi-overlay plot with legend interface
	 * @param colors the color to use for each line of the plot
	 * @param funcPlot the function points to plot for each
	 * @param title the title for the frame
	 * @param trigger mouse event listener
	 */
	@SuppressWarnings("rawtypes")
	public static void plotWithAxis
		(
			Colors colors, PlotCollection funcPlot, String title, MouseSampleTrigger trigger
		)
	{
		plotMulti (chartBufferedImage (DEFAULT_PLOT_SIZE), colors, funcPlot, title, trigger);
		if (trigger != null) trigger.getDisplay ().showLegend ();
	}


}

