
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.charting.PlotComputers;
import net.myorb.math.expressions.charting.PlotComputers.TransformResultsCollection;

import net.myorb.charting.ColorSelection;
import net.myorb.charting.Histogram;

import net.myorb.rinearn.SurfacePlotter;
import net.myorb.sitstat.Activity;

import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.JComponent;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;

import java.util.Map;

/**
 * generic 3D plotting engine.
 *  x,y domain used to compute function range expressed as colors
 * @author Michael Druckman
 */
public class DisplayGraph3D extends DisplayGraph
{


	/**
	 * Display tabular plot with data taken from a file
	 * @param filepath path to source of plot data
	 */
	public static void tabularPlotFromFile (String filepath)
	{
		SurfacePlotter plotter = new SurfacePlotter (filepath);
		plotter.plot (filepath);
	}


	/**
	 * build the plot data as mesh
	 * @param proprties description of the transform
	 * @param description a description identifying the plot
	 */
	public static void plotMesh
	(ContourPlotProperties proprties, String description)
	{
		describePlot
		(
			proprties.getActivityDescriptor (), description
		);
		PlotComputers.TransformResultsCollection points =
				PlotComputers.getSerialCalculationProcessing (proprties).executeTransform ();
		new SurfacePlotter (description, proprties).plot (points);
	}
	static void describePlot (Activity activity, String description)
	{
		activity.setTitle (description);
		activity.setProducer ("SerialCalculationComputer");
		activity.setMechanism ("DisplayGraph3D.plotMesh");
		activity.setDescription ("3D Mesh Plot");
	}


	/**
	 * build the plot data as contour
	 * @param proprties description of the transform
	 * @param description a description identifying the plot
	 */
	public static void plotContour
		(ContourPlotProperties proprties, String description)
	{
		int pointSize = proprties.getPointsSize ();
		int pointsPerAxis = proprties.getPointsPerAxis ();

		Point[] points = new Point[pointsPerAxis * pointsPerAxis];
		Object[] range = new Object[pointsPerAxis * pointsPerAxis];

		compute (proprties, pointsPerAxis, points, range); //TODO: fix properties (Y)

		offAxisContourPlot
		(
			range, points, pointSize,
			proprties.getLowCorner (), proprties.getEdgeSize (),
			description, new OffAxisHandler3D (proprties, pointsPerAxis, pointSize)
		);

		addTrackingFor (proprties, description);
	}
	public static void addTrackingFor (ContourPlotProperties proprties, String description)
	{ Tracking.getInstance (false).add (proprties, description, new ContourPlotProperties (proprties)); }
	public static void addTrackingFor (Map<String,Object> properties, String description, String type)
	{ Tracking.getInstance (true).add (description, properties); }


	/**
	 * fill image with points
	 * @param colors the color for each point
	 * @param points the points of the X/Y axis
	 * @param pointSize the size of each point
	 * @param scaleFactors computed factors
	 * @param image the image object
	 */
	public static void countourPlot
	(Colors colors, Point.Series points, int pointSize, Point scaleFactors, BufferedImage image)
	{
		add2Percent (scaleFactors);
		Point.Series scaled = scale4Quad (points, scaleFactors, image.getWidth ());
		Graphics2D g = image.createGraphics ();
	    
	    for (int i = 0; i < scaled.size (); i++)
	    {
	    	markPoint (scaled.get (i), colors.get (i), pointSize, g);
	    }

	    g.dispose ();
	}


	/**
	 * generate single point with fill operation
	 * @param p the x,y point at which to mark plot
	 * @param c the color to be used for the mark
	 * @param size the size of point to be drawn
	 * @param g the object in which too draw
	 */
	public static void markPoint (Point p, Color c, int size, Graphics2D g)
	{
		g.setColor (c); g.fillOval ((int)p.x, (int)p.y, size, size);
	}


	/**
	 * compute pixels in a point given plot size
	 * @param usingAxisSize the pixels along each axis
	 * @return the computed point size
	 */
	public static int appropriatePointSize (int usingAxisSize)
	{
		return 2 + DEFAULT_PLOT_SIZE / usingAxisSize;
	}


	/**
	 * 3D plot with color as the third axis
	 * @param colors the color for each point
	 * @param funcPlot the points of the X/Y axis
	 * @param pointSize the size of each point
	 * @param title for the frame
	 * @param mouse screen input
	 */
	public static void countourPlot
		(
			Colors colors, Point.Series funcPlot, int pointSize,
			String title, MouseMotionHandler mouse
		)
	{
		Point scalingFactors = scale (funcPlot);
		BufferedImage image = chartBufferedImage (DEFAULT_PLOT_SIZE);
		countourPlot (colors, funcPlot, pointSize, scalingFactors, image);
		JComponent c = showImage (image, title, mouse);
		mouse.setWidth (image.getWidth ());
		mouse.set (c);
	}


	/**
	 * produce contour plot with
	 *  arbitrary distance from axis junction
	 * @param range the values of the function
	 * @param domain the points used to evaluate
	 * @param pointSize the size of each point on plot
	 * @param lowCorner X/Y for low left corner
	 * @param edgeSize length of each axis
	 * @param title for the frame
	 * @param mouse the handler
	 */
	public static void offAxisContourPlot
		(
			Object[] range, Point[] domain,
			int pointSize, Point lowCorner, double edgeSize,
			String title, MouseMotionHandler mouse
		)
	{
		BufferedImage image = createBufferedImage (DEFAULT_PLOT_SIZE-MARGIN);
		offAxisContourPlot (range, domain, pointSize, lowCorner, edgeSize, image);
		JComponent c = showImage (image, title, mouse);
		mouse.setWidth (image.getWidth ());
		mouse.set (c);
	}


	/**
	 * construct graphics and add to image
	 * @param range the values of the function
	 * @param domain the points used to evaluate
	 * @param pointSize the size of each point on plot
	 * @param lowCorner X/Y for low left corner
	 * @param edgeSize length of each axis
	 * @param image the image being built
	 */
	public static void offAxisContourPlot
		(
			Object[] range, Point[] domain, int pointSize,
			Point lowCorner, double edgeSize, BufferedImage image
		)
	{
		int width = image.getWidth ();
		Graphics2D g = image.createGraphics ();
		double pixelsPer = width / edgeSize;
		Point scaledPoint = new Point ();
	
		for (int i = 0; i < domain.length; i++)
	    {
			scale (domain[i], lowCorner, width, pixelsPer, scaledPoint);
	    	markPoint (scaledPoint, (Color)range[i], pointSize, g);
	    }
	
	    g.dispose ();
	}


	/**
	 * transform domain to range
	 * @param descriptor the descriptor for the transform
	 * @param pointsPerAxis the resolution of the transform
	 * @param points the resulting points of the domain
	 * @param range the value for each domain point
	 */
	public static void compute
		(
			ContourPlotDescriptor descriptor,
			int pointsPerAxis, Point[] points,
			Object[] range
		)
	{
		//TODO: fix
		if (DUMP) dump (descriptor);
		Histogram histogram = new Histogram ();
		descriptor.getPlotComputer ().computeRange
			(descriptor, pointsPerAxis, points, range, histogram);
		if (showHistogramBarChart) DisplayGraph.histogramBarChart (histogram, HISTOGRAM_TITLE);
		if (showHistogramPlot) DisplayGraph.histogramPlot (histogram, HISTOGRAM_TITLE);
		descriptor.getColorSelector ().substituteColors (range, histogram);
	}
	static void dump (ContourPlotDescriptor descriptor)
	{ System.out.println ("COMPUTE: " + descriptor); System.out.println (); }
	static boolean showHistogramBarChart =  false, showHistogramPlot = false, DUMP = false; //TODO: fix
	static String HISTOGRAM_TITLE = "Display Spectrum";


}


/**
 * process mouse events to effect a zoom feature
 */
class OffAxisHandler3D extends OffAxisHandler
	implements Runnable, DisplayGraph.ContourPlotDescriptor, PlotComputers.TransformProcessing
{


	public OffAxisHandler3D
	(DisplayGraph.ContourPlotDescriptor descriptor, int pointsPerAxis, int pointSize)
	{
		super (descriptor.getLowCorner (), descriptor.getEdgeSize ());
		this.setPointsPerAxis (pointsPerAxis); this.setPointsSize (pointSize);
		if (descriptor instanceof Plot3DContour) connectEquation (descriptor);
		this.setPlotComputer (descriptor.getPlotComputer ());
		this.descriptor = descriptor;
	}
	private DisplayGraph.ContourPlotDescriptor descriptor;


	/*
	 * execute contour construction under detached thread
	 */


	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		linkParent (descriptor);

		DisplayGraph3D.plotContour
		(
			this, standardTag ()
		);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.OffAxisHandler#processArea()
	 */
	public void processArea () { SimpleScreenIO.startBackgroundTask (this); }
	//public void processArea () { new Thread (this).start (); }


	/*
	 * specific to MultiDimensionalVectored - enable TransformResultsCollection and executeTransform
	 */


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.TransformProcessing#executeTransform()
	 */
	public TransformResultsCollection executeTransform ()
	{
		return getProcessor ().executeTransform ();
	}


	@SuppressWarnings("unchecked")
	PlotComputers.TransformProcessing getProcessor ()
	{
		plot3d.setEdgeSize (this.getEdgeSize ());
		plot3d.setLowCorner (this.getLowCorner ());

		return PlotComputers.getVectoredTransformProcessing
				(plot3d, plot3d.getMultiDimensionalVectored ().getEnvironment());
	}
	@SuppressWarnings("rawtypes") Plot3D plot3d;
	@SuppressWarnings("rawtypes") void connectEquation
	(DisplayGraph.ContourPlotDescriptor descriptor)
	{
		this.setMultiplier (descriptor.getMultiplier ());
		this.setPointsSize (DisplayGraph3D.appropriatePointSize (this.getPointsPerAxis ()));
		this.plot3d = (Plot3D) descriptor;
	}


	/*
	 * process descriptor wrapped transform
	 */


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Fractal.Transform#evaluate(float, float)
	 */
	public int evaluate (double x, double y) { return descriptor.evaluate (x, y); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.ContourColorScheme#getColorSelector()
	 */
	public ColorSelection getColorSelector() { return descriptor.getColorSelector (); }


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#identifyTransform()
	 */
	public String identifyTransform ()
	{
		try { return descriptor.identifyTransform (); }
		catch (Exception e) { return "UNKNOWN"; }
	}


	private static final long serialVersionUID = -516859898242774448L;

}

