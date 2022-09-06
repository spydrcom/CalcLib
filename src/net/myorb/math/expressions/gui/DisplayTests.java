
package net.myorb.math.expressions.gui;

import net.myorb.math.*;
import net.myorb.math.expressions.charting.DisplayGraph;
import net.myorb.math.expressions.charting.MultiFunctionPlot;

import java.awt.image.BufferedImage;
import java.awt.Color;

/**
 * unit tests for GUI components
 * @author Michael Druckman
 */
public class DisplayTests extends DisplayGraph
{


	static RealFunction f1 ()
	{
		return new RealFunction ()
		{
			public Double eval (Double x) { return -x*x + x - 1; }
			public SpaceManager<Double> getSpaceDescription () { return null; }
		};
	}


	static RealFunction f2 ()
	{
		return new RealFunction ()
		{
			public Double eval (Double x) { return -2*x + 1; }
			public SpaceManager<Double> getSpaceDescription () { return null; }
		};
	}


	public static void test4 ()
	{
		MultiFunctionPlot.PlotDescriptors desc =
				MultiFunctionPlot.newPlotDescriptor
			(700, "TEST4: -x^2 + x - 1", domain (-1, 4, 0.1));
		MultiFunctionPlot.addFunctionPlot (desc, "GREEN", f1 ());
		MultiFunctionPlot.addFunctionPlot (desc, "RED", f2 ());
		MultiFunctionPlot.plot (desc);
	}


	public static void test3 ()
	{
		Point scalingFactors = new Point (0, 0);
		RealSeries xAxis = domain (-1, 4, 0.1);

		Point.Series funcPlot1 = plot (xAxis, f1 (), scalingFactors);
		Point.Series funcPlot2 = plot (xAxis, f2 (), scalingFactors);

		BufferedImage image = chartBufferedImage (700);
		plot (Color.red, funcPlot1, scalingFactors, image);
	    plot (Color.green, funcPlot2, scalingFactors, image);
	    showImage (image, "-x^2 + x - 1", null);
	}


	/**
	 * -x^2 + x - 1
	 */
	public static void test1 ()
	{
		BufferedImage image = chartBufferedImage (700);

	    Point.Series fpoints = new Point.Series ();
	    Point.Series dpoints = new Point.Series ();
		for (double x = -1; x < 3.5; x += 0.1)
	    {
	    	Point p = new Point ();
	    	p.x = x; p.y = -x*x + x - 1;
	    	fpoints.add (p);

	    	p = new Point ();
	    	p.x = x; p.y = -2*x + 1;
	    	dpoints.add (p);
	    }

	    Point scaleFactors = scale (fpoints);
	    adjustScale (dpoints, scaleFactors);

	    plot (Color.getColor ("RED"), fpoints, scaleFactors, image);
	    plot (Color.getColor ("GREEN"), dpoints, scaleFactors, image);

	    showImage (image, "-x^2 + x - 1", null);
	}


	/**
	 * - 5040 + 29952*x - 24553*x^2 + 3821*x^3 + 759*x^4 - 197*x^5 + 10*x^6
	 */
	public static void test2 ()
	{
		BufferedImage image = chartBufferedImage (700);

		Point.Series fpoints = new Point.Series ();
		Point.Series dpoints = new Point.Series ();

	    for (double x = -2; x < 8; x += 0.1)
		//for (double x = -5.5; x < 12.5; x += 0.1)
	    {
	    	Point p = new Point ();
	    	p.x = x; p.y = - 5040 + 29952*x - 24553*x*x + 3821*x*x*x + 759*x*x*x*x - 197*x*x*x*x*x + 10*x*x*x*x*x*x;
	    	fpoints.add (p);

	    	p = new Point ();
	    	p.x = x; p.y = 29952 - 24553*2*x + 3*3821*x*x + 4*759*x*x*x - 5*197*x*x*x*x + 6*10*x*x*x*x*x;
	    	dpoints.add (p);
	    }

	    Point scaleFactors = scale (fpoints);
	    adjustScale (dpoints, scaleFactors);

	    plot (Color.red, fpoints, scaleFactors, image);
	    plot (Color.green, dpoints, scaleFactors, image);

	    showImage (image, "- 5040 + 29952*x - 24553*x^2 + 3821*x^3 + 759*x^4 - 197*x^5 + 10*x^6", null);
	}


	public static void test5 ()
	{
		
	}


	/**
	 * unit tests
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		test1 ();
		test4 ();
	}


}

