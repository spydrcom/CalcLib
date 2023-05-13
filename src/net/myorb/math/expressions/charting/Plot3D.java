
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.ExpressionSpaceManager;

import net.myorb.math.expressions.PrettyPrinter;
import net.myorb.math.expressions.ValueManager;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.charting.DisplayGraphTypes;
import net.myorb.math.MultiDimensional;

import java.awt.Color;

/**
 * boiler plate for 3D plot control
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class Plot3D <T> extends ContourPlotProperties
	implements PlotComputers.TransformProcessing, Runnable,
		DisplayGraphTypes.ContourPlotDescriptor
{


	public Plot3D () { super (-4); }


	// collection of equation meta-data

	/**
	 * identify equation and default plot computer use
	 * @param equation any implementer of Multi-Dimensional
	 */
	public void setEquation (MultiDimensional.Function <T> equation)
	{
		this.setEquation (equation, PlotComputers.getBruteForcePlotComputer (this));
	}

	/**
	 * identify equation and specify plot computer
	 * @param equation any implementer of Multi-Dimensional
	 * @param computer the plot computer to use
	 */
	public void setEquation
		(
			MultiDimensional.Function <T> equation,
			DisplayGraphTypes.PlotComputer computer
		)
	{
		this.equation = equation;
		this.mgr = ( ExpressionSpaceManager <T> ) equation.getSpaceDescription ();
		this.setPlotComputer (computer);
		this.setEquation (this);
	}
	private MultiDimensional.Function <T> equation;
	protected ExpressionSpaceManager <T> mgr;


	// specific to vectored transform implementations

	/**
	 * @param equation a vectored transform
	 */
	public void setEquation (MultiDimensionalVectored <T> equation)
	{
		this.setPointsPerAxis ( equation.pointsPerAxis );
		this.setPlotComputer ( PlotComputers.getVectorPlotComputer (this) );
		this.vectorTransformProcessing = PlotComputers.getVectoredTransformProcessing
				( this, equation.getEnvironment () );
		this.vectoredEquation = equation;
		this.setEquation ( this );
	}

	/**
	 * @return vector enabled function access
	 */
	public MultiDimensionalVectored <T>
		getMultiDimensionalVectored () { return vectoredEquation; }
	private MultiDimensionalVectored <T> vectoredEquation;


	// interface implementations

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.TransformProcessing#executeTransform()
	 */
	public PlotComputers.TransformResultsCollection executeTransform ()
	{
		return vectorTransformProcessing.executeTransform ();
	}
	protected PlotComputers.TransformProcessing vectorTransformProcessing;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.ContourPlotProperties#setMultiplier(double)
	 */
	public void setMultiplier (double multiplier) 
	{ super.setMultiplier (multiplier); this.multiplier = multiplier; }
	private double multiplier;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#evaluate(double, double)
	 */
	public int evaluate (double x, double y) { return evaluateFunction (x, y).intValue (); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.ContourPlotProperties#evaluateReal(double, double)
	 */
	public double evaluateReal (double x, double y) { return evaluateFunction (x, y); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.ContourPlotProperties#evaluateAngle(double, double)
	 */
	public double evaluateAngle (double x, double y)
	{
		throw new RuntimeException ("Vector field manager needed to evaluate direction");
	}


	// support for implementations of evaluations
	// - including conversions between application representations
	// - and machine types

	/**
	 * translate function result value to contour appropriate value
	 * @param x the X-axis coordinate
	 * @param y the Y-axis coordinate
	 * @return contour value
	 */
	@SuppressWarnings("unchecked")
	public Double evaluateFunction (double x, double y)
	{ return multiplier * cvt (equation.f ( toGeneric (x), toGeneric (y) )); }

	/**
	 * application representation of domain type to machine double float
	 * @param value the application specific discrete value
	 * @return the equivalent as a double float
	 */
	protected double cvt (T value) { return this.mgr.convertToDouble (value); }
	protected T toGeneric (double value) { return this.mgr.convertFromDouble (value); }

	/**
	 * application representation discrete to machine double float
	 * @param DV an application representation of a discrete value
	 * @return the equivalent as a double float
	 */
	protected double toDouble (ValueManager.DiscreteValue <T> DV)
	{ return this.cvt ( DV.getValue () ); }


	// plot scheduled for execution in background

	/**
	 * display plot with frame titled as specified
	 * @param title the title to show on the frame
	 */
	public void show (String title)
	{
		this.title = title;
		this.getActivityDescriptor ().setTitle (title);
		SimpleScreenIO.scheduleBackgroundTask (this);
	}
	protected String title;


	// descriptive content to be burned into plot image

	/**
	 * a rendered descriptive content image
	 * @param descriptiveContent a rendered image to add to image
	 */
	public void setDescriptiveContentImage
		(SimpleScreenIO.Image descriptiveContent)
	{ this.descriptiveContent = descriptiveContent; }

	public void setDescriptiveContent (SimpleScreenIO.Widget descriptiveContent)
	{ this.setDescriptiveContentImage ( (SimpleScreenIO.Image) descriptiveContent); }
	public javax.swing.Icon getDescriptiveContentImage () { return descriptiveContent.getContent (); }
	public SimpleScreenIO.Image getDescriptiveContent () { return descriptiveContent; }
	protected SimpleScreenIO.Image descriptiveContent = null;

	/**
	 * stamp a binary image with attached Descriptive Content
	 * @param g the graphics object to stamp content on
	 */
	public void stampDescriptiveContent (java.awt.Graphics g)
	{
		if (descriptiveContent == null) return;
		SimpleScreenIO.Label L = new SimpleScreenIO.Label (Color.WHITE);
		getDescriptiveContentImage ().paintIcon (L, g, 25, 25);
	}

	/**
	 * render function for use as Descriptive Content
	 * @param s symbol to render
	 */
	public void setDescriptiveContentFor
		(Subroutine <T> s, PrettyPrinter <T> using)
	{
		try
		{ setDescriptiveContent ( using.toRenderedWidget (s) ); }
		catch (Exception e) { e.printStackTrace (); }
	}


	// key interface points needing implementations

	/**
	 * @return units along edge in plot
	 */
	public int getPlotEdgeSize ()
	{
		throw new RuntimeException ("Plot edge size not configured");
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run () {}


	private static final long serialVersionUID = 5613614604502495618L;
}

