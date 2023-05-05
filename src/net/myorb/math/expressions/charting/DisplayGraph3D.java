
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.charting.PlotComputers;

import net.myorb.charting.ColorSelection;
import net.myorb.charting.DisplayGraphTypes;
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


	// plot data from file

	/**
	 * Display tabular plot with data taken from a file
	 * @param filepath path to source of plot data
	 */
	public static void tabularPlotFromFile (String filepath)
	{
		SurfacePlotter plotter = new SurfacePlotter (filepath);
		plotter.plot (filepath);
	}


	// 3D mesh plot algorithms

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


	// vector field plot algorithms

	/**
	 * build the plot data as Vector Field
	 * @param proprties description of the transform
	 * @param description a description identifying the plot
	 */
	public static void plotVectorField
		(ContourPlotProperties proprties, String description)
	{
		// contour plot shows vector magnitudes
		BufferedImage image = buildContourImage (proprties);

		Plot3DVectorField <?> plotter = ( Plot3DVectorField <?> ) proprties;
		// the vector field version of the properties contains direction data for display
		DisplayGraphTypes.VectorField.Locations plotPoints = plotter.getVectorPoints ();
		if (TRACE) show (plotPoints); // for debugging use

		offAxisVectorFieldPlot
		(
			plotPoints,
			proprties.getPointsSize (),
			proprties.getLowCorner (),
			proprties.getEdgeSize (),
			image
		);

		// use common display and tracking layers
		showImageAsComponent (description, proprties, image);
		addTrackingFor (proprties, description);
	}

	/**
	 * add vector direction indicators to contour plot
	 * @param plotPoints Vector Field Locations collected for plot
	 * @param pointSize the computed appropriate point size for displays
	 * @param lowCorner the low corner coordinates use for establishing context
	 * @param edgeSize the number of pixels in each edge of the plot
	 * @param image the Java buffered image object to use
	 */
	public static void offAxisVectorFieldPlot
		(
			DisplayGraphTypes.VectorField.Locations plotPoints, 
			int pointSize, Point lowCorner, double edgeSize,
			BufferedImage image
		)
	{
		int width = image.getWidth ();
		Point scaledPoint = new Point ();
		DisplayGraphTypes.VectorField point;
		Graphics2D g = image.createGraphics ();
		double pixelsPer = width / edgeSize;
	
		for (int i = 0; i < plotPoints.size (); i++)
	    {
			point = plotPoints.get (i);
			// the point gets scaled into display coordinates
			scale ( point, lowCorner, width, pixelsPer, scaledPoint );
			// the point is redrawn and the direction indicator is added
	    	markDirection ( scaledPoint, point.getAngle (), pointSize+1, g );
	    }
	
	    g.dispose ();
	}

	/**
	 * @param plotPoints data to be dumped in trace
	 */
	static void show (DisplayGraphTypes.VectorField.Locations plotPoints)
	{ for (DisplayGraphTypes.VectorField point : plotPoints) System.out.println (point); }
	static boolean TRACE = false;


	// most basic contour plot algorithms

	/**
	 * build the plot data as contour
	 * @param proprties description of the transform
	 * @param description a description identifying the plot
	 * @return contour image
	 */
	public static BufferedImage plotContour
		(ContourPlotProperties proprties, String description)
	{
		BufferedImage image = buildContourImage (proprties);
		showImageAsComponent (description, proprties, image);
		addTrackingFor (proprties, description);
		return image;
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
		showImageAsComponent (image, title, mouse);
	}

	/**
	 * produce contour plot with
	 *  arbitrary distance from axis junction
	 * @param range the values of the function
	 * @param domain the points used to evaluate
	 * @param pointSize the size of each point on plot
	 * @param lowCorner X/Y for low left corner
	 * @param edgeSize length of each axis
	 * @return new image
	 */
	public static BufferedImage offAxisContourPlot
		(
			Object[] range, Point[] domain,
			int pointSize, Point lowCorner,
			double edgeSize
		)
	{
		BufferedImage image = createBufferedImage (DEFAULT_PLOT_SIZE-MARGIN);
		offAxisContourPlot (range, domain, pointSize, lowCorner, edgeSize, image);
		return image;
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


	// low level plot operations

	/**
	 * construct the contour image
	 * @param proprties description of the transform
	 * @return contour image
	 */
	public static BufferedImage buildContourImage
		  (ContourPlotProperties proprties)
	{
		int pointsPerAxis = proprties.getPointsPerAxis (),
			pointsInSquarePlot = pointsPerAxis * pointsPerAxis;
		Point  [] points = new Point  [ pointsInSquarePlot ];
		Object [] range  = new Object [ pointsInSquarePlot ];

		compute (proprties, pointsPerAxis, points, range);

		return offAxisContourPlot
		(
			range, points, proprties.getPointsSize (),
			proprties.getLowCorner (), proprties.getEdgeSize ()
		);
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
		(
			Colors colors, Point.Series points,
			int pointSize, Point scaleFactors,
			BufferedImage image
		)
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


	// graphics primitives

	/**
	 * plot direction indicator for vector points
	 * @param scaledPoint the point scaled to graphics coordinates
	 * @param angle the angle the vector should display direction at
	 * @param pointSize the chosen point size for the plot
	 * @param g the 2D graphics object
	 */
	public static void markDirection
		(
			Point scaledPoint, double angle,
			int pointSize, Graphics2D g
		)
	{
		Point adjusted = adjustedPoint
			( scaledPoint, pointSize / 2 );
		Point end = endPoint ( adjusted, angle );
		// the point is redrawn as WHITE to make it stand out
		markPoint ( scaledPoint, Color.WHITE, pointSize, g );

		g.drawLine // a line extending in the vector direction
		(
			(int) adjusted.x, (int) adjusted.y,
			(int) end.x, (int) end.y
		);
	}

	/**
	 * adjust for point render shifting center
	 * @param scaledPoint the coordinates after scaling
	 * @param adjustment the adjustment to compensate for render
	 * @return the adjusted coordinates for the point
	 */
	public static Point adjustedPoint
		(Point scaledPoint, double adjustment)
	{
		int X = (int) (scaledPoint.x + adjustment),
			Y = (int) (scaledPoint.y + adjustment);
		return new Point (X, Y);
	}

	/**
	 * compute end of vector
	 * @param P the points starting the vector
	 * @param angle the direction component of the vector
	 * @return the coordinates for the end of vector
	 */
	public static Point endPoint (Point P, double angle)
	{
		double S = VECTOR_DISPLAY_LENGTH * Math.sin (angle),
			C = VECTOR_DISPLAY_LENGTH * Math.cos (angle);
		return new Point (P.x + S, P.y + C);
	}
	static int VECTOR_DISPLAY_LENGTH = 10;

	/**
	 * generate single point with fill operation
	 * @param p the x,y point at which to mark plot
	 * @param c the color to be used for the mark
	 * @param size the size of point to be drawn
	 * @param g the object in which too draw
	 */
	public static void markPoint (Point p, Color c, int size, Graphics2D g)
	{
		g.setColor (c); g.fillOval ( (int) p.x, (int) p.y, size, size );
	}


	// wrap image into component for display

	/**
	 * build component for plot image
	 * @param title a title to display with the image
	 * @param proprties description of the transform
	 * @param image the buffered image with the plot
	 */
	public static void showImageAsComponent
		(
			String title,
			ContourPlotProperties proprties,
			BufferedImage image
		)
	{
		showImageAsComponent
		(
			image, title,

			new OffAxisHandler3D
			(
				proprties,
				proprties.getPointsPerAxis (),
				proprties.getPointsSize ()
			)
		);
	}

	/**
	 * build component for plot image
	 * @param image the buffered image with the plot
	 * @param title a title to display with the image
	 * @param mouse the mouse handler to attach
	 */
	public static void showImageAsComponent
	(BufferedImage image, String title, MouseMotionHandler mouse)
	{
		JComponent c = showImage (image, title, mouse);
		mouse.setWidth (image.getWidth ());
		mouse.set (c);
	}


	// compute function transform of parameters from plane

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
	static boolean showHistogramBarChart =  false, showHistogramPlot = false, DUMP = false;
	static String HISTOGRAM_TITLE = "Display Spectrum";


	// primitive computations

	/**
	 * compute pixels in a point given plot size
	 * @param usingAxisSize the pixels along each axis
	 * @return the computed point size
	 */
	public static int appropriatePointSize (int usingAxisSize)
	{
		return 2 + DEFAULT_PLOT_SIZE / usingAxisSize;
	}


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
	public PlotComputers.TransformResultsCollection
				executeTransform ()
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

