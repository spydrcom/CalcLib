
package net.myorb.math.expressions.charting.fractals;

import net.myorb.math.expressions.charting.DisplayGraph3D;
import net.myorb.math.expressions.charting.ContourPlotProperties;
import net.myorb.math.expressions.managers.ExpressionComplexFieldManager;
import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;
import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.SpaceManager;

import net.myorb.data.abstractions.Function;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * generic fractal plotting engine
 * @author Michael Druckman
 */
public class Fractal extends ContourPlotProperties
		implements DisplayGraph3D.ContourPlotDescriptor
{


	/*
	 * import 3D plot description type
	 */


	public interface Transform extends DisplayGraph3D.Transform3D {}
	public interface ColorScheme extends DisplayGraph3D.ContourColorScheme {}
	public interface Descriptor extends DisplayGraph3D.ContourPlotDescriptor, ColorScheme, Transform {}


	/**
	 * extended forms of Fractal that
	 *  implement Descriptor can plot with this generic call
	 * @param pointsPerAxis points to be plotted (resolution) on each axis
	 * @param pointSize the pixel size for the points of the plot
	 */
	public void plot (int pointsPerAxis, int pointSize)
	{
		DisplayGraph3D.plotContour (setScale (pointsPerAxis, pointSize), toString ());
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#identifyTransform()
	 */
	public String identifyTransform ()
	{
		return getFractalName ();
	}


	/**
	 * @return currently set divergence limit
	 */
	public float getCurrentLimit () { return currentLimit; }
	public void setCurrentLimit (float currentLimit)
	{ this.currentLimit = currentLimit; }
	protected float currentLimit = 0;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#evaluate(double, double)
	 */
	public int evaluate (double x, double y) { return computeIterationsFor (x, y); }
	public int computeIterationsFor (double x, double y) { throw new RuntimeException ("Not implemented"); }


	/**
	 * specify the view area
	 * @param x the x-axis coordinate of the low corner
	 * @param y the y-axis coordinate of the low corner
	 * @param edge the length of the edges of the square view area
	 * @return THIS object (to allow compounding)
	 */
	public Fractal setViewArea (double x, double y, double edge)
	{
		setLowCorner (new DisplayGraph3D.Point (x, y));
		setEdgeSize ((float)edge);
		return this;
	}


	/**
	 * construct fractal from name
	 * @param fractalName the name of the fractal
	 * @return object of type named
	 */
	public Fractal reconstitute (String fractalName)
	{
		return null; // must be over-ridden by extender
	}
	public Fractal reconstitute (String fractalName, DisplayGraph3D.Point lowCorner, float edge)
	{ return reconstitute (fractalName).setViewArea (lowCorner.x, lowCorner.y, edge); }

	/**
	 * use enum type to recognize fractal name
	 * @param fractal the enum object that will recognize as a fractal object
	 * @return the name for the fractal
	 */
	public String fractalNameFor (Object fractal) { return fractal.toString (); }

	/**
	 * abstract method for getting recognized name for fractal
	 * @return the name for the fractal
	 */
	public String getFractalName () { return null; }

	/**
	 * @param fractalName name of fractal to construct
	 * @param lowCorner the low corner of the view space
	 * @param edge the length of the edge of the view space
	 * @return the rebuilt fractal object, NULL if not found
	 */
	public static Fractal reconstituteFractal (String fractalName, DisplayGraph3D.Point lowCorner, float edge)
	{
		if (Julia.isMember (fractalName)) return new Julia ().reconstitute (fractalName, lowCorner, edge);
		else if (Mandelbrot.isMember (fractalName)) return new Mandelbrot ().reconstitute (fractalName, lowCorner, edge);
		else if (Newton.isMember (fractalName)) return new Newton ().reconstitute (fractalName, lowCorner, edge);
		else return null;
	}


	/**
	 * simple complex values
	 */
	public class ComplexConstant extends ComplexValue<Double>
	{
		public ComplexConstant (int r) { this (r, 0); }
		public ComplexConstant (Double r) { this (); setValue (r, 0d); }
		public ComplexConstant () { super (null); manager = floatManager; }
		public ComplexConstant (Double r, Double i) { this (); setValue (r, i); }
		public ComplexConstant (int r, int i) { this (); setValue ((double)r, (double)i); }
		public void setValue (Double r, Double i) { realpart = r; imagpart = i;}
	}


	/**
	 * core of complex function
	 */
	public abstract class ComplexFunction implements Function<ComplexValue<Double>>
	{
		public SpaceManager<ComplexValue<Double>> getSpaceManager () { return complexMgr; }
		public SpaceManager<ComplexValue<Double>> getSpaceDescription () { return complexMgr; }
		protected ExpressionComplexFieldManager complexMgr = complexManager;
		protected Fractal.ComplexConstant ONE = new ComplexConstant (1);
	}
	public class Calculus extends OrdinaryPolynomialCalculus<ComplexValue<Double>>
	{ public Calculus () { super (complexManager); } }


	/**
	 * simple list object of fractals
	 */
	public static class FractalList extends ArrayList<Fractal>
	{ private static final long serialVersionUID = -3492126805122584018L; }

	/**
	 * map name to fractal object
	 */
	public static class FractalMap extends HashMap<String,Fractal>
	{
		/**
		 * @param fractal the fractal to be added, toString is used to retrieve name
		 */
		public void addNamed (Fractal fractal) { put (fractal.toString (), fractal); }
		private static final long serialVersionUID = -1263349039308893241L;
	}


	/**
	 * view selected using low corner and edge size
	 * @param lowCorner the low corner is x-min/y-min
	 * @param edgeSize distance along each axis from low corner
	 */
	public Fractal (DisplayGraph3D.Point lowCorner, float edgeSize)
	{
		this ();
		setLowCorner (lowCorner);
		setEdgeSize (edgeSize);
	}
	public Fractal (DisplayGraph3D.Point lowCorner, float edgeSize, int maxResult)
	{ this (lowCorner, edgeSize); setMaxResult (maxResult); }


	/**
	 * default full size view
	 */
	public Fractal ()
	{
		super (-1);
		floatManager = new DoubleFloatingFieldManager ();
		complexManager = new ExpressionComplexFieldManager ();
	}
	protected final ExpressionComplexFieldManager complexManager;
	protected final DoubleFloatingFieldManager floatManager;


	private static final long serialVersionUID = 4601254217391600151L;
}

