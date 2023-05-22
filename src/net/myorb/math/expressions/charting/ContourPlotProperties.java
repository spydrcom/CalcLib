
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.charting.colormappings.LegacyAlternativeAlgorithmColorScheme;
import net.myorb.math.expressions.charting.colormappings.TemperatureModelColorScheme;
import net.myorb.sitstat.tasks.CommonTaskProcessing;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.ColorSelection;
import net.myorb.charting.Histogram;

import net.myorb.sitstat.RealizationTracking;
import net.myorb.sitstat.ActivityProperties;
import net.myorb.sitstat.Activity;

import net.myorb.data.abstractions.CommonDataStructures;

/**
 * collect properties required to construct contour plot
 * @author Michael Druckman
 */
public class ContourPlotProperties extends CommonDataStructures.SymbolicMap <Object>
	implements DisplayGraphTypes.ContourPlotDescriptor, RealizationTracking
{


	public static String POLAR_IDENTITY = "POLAR";
	public static String EQUATION_IDENTITY = "EQN";


	/**
	 * @param item name of the value
	 * @param defaultValue a default if not found
	 * @return the value found or else the default
	 */
	public double getOrDefault (String item, double defaultValue)
	{
		if ( ! this.containsKey (item) ) return defaultValue;
		return Double.parseDouble (get (item).toString ());
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#identifyTransform()
	 */
	public String identifyTransform ()
	{
		Object identity = get (TRANSFORM_IDENTITY);
		if (identity == null) throw new RuntimeException ("No identity for transform");
		return identity.toString ();
	}
	public void setTransformIdentity (String identity) { put (TRANSFORM_IDENTITY, identity); }
	public static String TRANSFORM_IDENTITY = "TransformIdentity";


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphTypes.TransformRealization#setPlotComputer(net.myorb.charting.DisplayGraphTypes.PlotComputer)
	 */
	public void setPlotComputer
	(DisplayGraph3D.PlotComputer computer) { this.computer = computer; }
	public DisplayGraph3D.PlotComputer getPlotComputer () { return computer; }
	private DisplayGraph3D.PlotComputer computer;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.ViewSpace#getLowCorner()
	 */
	public DisplayGraphTypes.Point getLowCorner ()
	{
		return new DisplayGraphTypes.Point
			(
				Double.parseDouble (get (X_AXIS).toString ()),
				Double.parseDouble (get (Y_AXIS).toString ())
			);
	}
	public void setLowCorner (DisplayGraphTypes.Point point)
	{
		put (X_AXIS, point.x); put (Y_AXIS, point.y);
//		System.out.println ("new LC: " + getLowCorner ());
	}
	public static String X_AXIS = "Xaxis", Y_AXIS = "Yaxis";

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.ViewSpace#getEdgeSize()
	 */
	public float getEdgeSize ()
	{ return Float.parseFloat (get (EDGE_SIZE).toString ()); }
	public void setEdgeSize (float size) { put (EDGE_SIZE, size); }
	public static String EDGE_SIZE = "EdgeSize";

	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphTypes.ViewSpace#getAltEdgeSize()
	 */
	public float getAltEdgeSize ()
	{ return Float.parseFloat (get (ALT_EDGE_SIZE).toString ()); }
	public void setAltEdgeSize (float size) { put (ALT_EDGE_SIZE, size); }
	public static String ALT_EDGE_SIZE = "AltEdgeSize";

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Tracking.PlotParameters#getPointsPerAxis()
	 */
	public int getPointsPerAxis () { return Integer.parseInt (get (POINTS_PER_AXIS).toString ()); }
	public void setPointsPerAxis (int points)  { put (POINTS_PER_AXIS, points); }
	public static String POINTS_PER_AXIS = "PointsPerAxis";

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.Tracking.PlotParameters#getPointsSize()
	 */
	public int getPointsSize () { return Integer.parseInt (get (POINTS_SIZE).toString ()); }
	public void setPointsSize (int size)  { put (POINTS_SIZE, size); }
	public static String POINTS_SIZE = "PointsSize";

	public String getProfile () { return get (PROFILE).toString (); }
	public void setProfile (String profile)  { put (PROFILE, profile); }
	public boolean hasProfile () { return containsKey (PROFILE); }
	public static String PROFILE = "EquationProfile";
	public static String BODY = "EquationBody";

	public String getTitle () { return get (TITLE).toString (); }
	public void setTitle (String title)  { put (TITLE, title); }
	public static String TITLE = "Title";

	public ContourPlotProperties setScale (int axisSize, int pointSize)
	{
		setPointsPerAxis (axisSize);
		setPointsSize (pointSize);
		return this;
	}

	public int getMaxResult () { return Integer.parseInt (get (MAX_RESULT).toString ()); }
	public void setMaxResult (int size)  { put (MAX_RESULT, size); }
	public static String MAX_RESULT = "MaxResult";

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#getMultiplier()
	 */
	public double getMultiplier ()
	{ return getOrDefault (MULTIPLIER, DEFAULT_MULTIPLIER_VALUE); }
	public void setMultiplier (double multiplier)  { put (MULTIPLIER, multiplier); }
	public static double DEFAULT_MULTIPLIER_VALUE = 1000;
	public static String MULTIPLIER = "Multiplier";

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#setPlotNumber(int)
	 */
	public void setPlotNumber (int plotNumber) { put (PLOT_NUMBER, plotNumber); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#getPlotNumber()
	 */
	public int getPlotNumber ()
	{
		if (!containsKey (PLOT_NUMBER)) return 0;
		return Integer.parseInt (get (PLOT_NUMBER).toString ());
	}
	public static String PLOT_NUMBER = "PlotNumber";

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#getPlotParent()
	 */
	public int getPlotParent () { return Integer.parseInt (get (PLOT_PARENT).toString ()); }
	public void setPlotParent (int parent) { put (PLOT_PARENT, parent); }
	public static String PLOT_PARENT = "PlotParent";

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.ContourColorScheme#getColorSelector()
	 */
	public ColorSelection getColorSelector () { return colorSchemeFactory.newColorSelection (); }
	public static void setColorSelectionFactory (ColorSelection.Factory factory) { colorSchemeFactory = factory; }
	private static ColorSelection.Factory colorSchemeFactory = LegacyAlternativeAlgorithmColorScheme.getColorSchemeFactory ();


	// methods for equation evaluation

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#evaluate(double, double)
	 */
	public int evaluate (double x, double y) { return equation.evaluate (x, y); }
	public double evaluateReal (double x, double y) { return equation.evaluateReal (x, y); }
	public double evaluateAngle (double x, double y) { return equation.evaluateReal (x, y); }
	public Object evaluateGeneric (double x, double y) { return equation.evaluateGeneric (x, y); }


	// constructors 

	public ContourPlotProperties (int rootIdentifier)
	{
		setPlotComputer (PlotComputers.getBruteForcePlotComputer (this));
		setPlotParent (rootIdentifier);
	}

	public ContourPlotProperties (ContourPlotProperties p)
	{
		setPlotComputer (PlotComputers.getBruteForcePlotComputer (this));
		putAll (p); setPlotParent (0); setEquation (p);
	}


	/**
	 * describe an equation from plot description
	 * @param equation a plot descriptor for the equation
	 */
	public void setEquation
	(DisplayGraphTypes.ContourPlotDescriptor equation) { this.equation = equation; }
	private DisplayGraphTypes.ContourPlotDescriptor equation = null;
	public boolean isEquationSet () { return equation != null; }


	/**
	 * link described plot as parent
	 * @param descriptor the descriptor of the parent
	 */
	public void linkParent (DisplayGraphTypes.ContourPlotDescriptor descriptor)
	{ setPlotParent (descriptor.getPlotNumber ()); }


	/**
	 * build tag for fractal plot
	 * @param name type name of fractal
	 * @param lowCorner the low corner point
	 * @param edgeSize the units along one edge
	 * @return a name tag for the plot
	 */
	public static String standardTag (String name, DisplayGraphTypes.Point lowCorner, float edgeSize)
	{
		return name + " : " + lowCorner + " - " + edgeSize;
	}
	public String standardTag ()
	{
		return standardTag (identifyTransform (), getLowCorner (), getEdgeSize ());
	}


	/**
	 * provide allocation of buffers specific to application
	 * - this needs override for layers depending on alternate buffers
	 * @param size the number of elements to allocate
	 */
	public void allocateExtendedBuffer (int size) {}


	// Legend processing

	/**
	 * use meta-data from plot histogram to construct widgets for Legend
	 * @param histogram the histogram used to collect meta-data for a plot
	 */
	public void buildLegendWidgetsFor (Histogram histogram)
	{
		double M = getMultiplier ();
		DisplayGraphTypes.LegendEntries entries =
				DisplayGraphTypes.legendEntriesFor (10, histogram);
		TemperatureModelColorScheme selector = new TemperatureModelColorScheme ();
		this.legend = DisplayGraphTypes.legendWidgetsFor (entries, M, selector);
	}
	protected DisplayGraphTypes.LegendWidgets legend;

	/**
	 * connect pop-up to Component for showing Legend
	 * - ALT RightMouseClick is the convention for the Legend pop-up
	 * @param C the component to offer the pop up for the legend
	 */
	public void attachLegend (java.awt.Component C) { LegendDisplay.attachLegend (C, legend); }

	/**
	 * use computed histogram meta-data to produce Legend display
	 */
	public void showLegend () { LegendDisplay.show (legend); }


	// computation for timing estimates

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.RealizationTracking#getActivityDescriptor()
	 */
	public Activity getActivityDescriptor ()
	{
		if (taskDescription == null)
		{
			this.taskDescription = new ActivityProperties ();
		}
		return this.taskDescription;
	}
	protected Activity taskDescription = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.RealizationTracking#setRemaining(int)
	 */
	public void setRemaining (int remaining)
	{
		this.taskMonitorProcessing = new CommonTaskProcessing (remaining);
		this.taskMonitorProcessing.copyFrom (taskDescription);
	}
	protected CommonTaskProcessing taskMonitorProcessing = null;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.RealizationTracking#reduceRemaining(int)
	 */
	public void reduceRemaining (int portion)
	{
		this.taskMonitorProcessing.reduceRemaining (portion);
	}

	/* (non-Javadoc)
	 * @see net.myorb.sitstat.RealizationTracking#setOriginalSize(int)
	 */
	public void setOriginalSize (int remaining)
	{
		this.taskMonitorProcessing.setOriginalSize (remaining);
	}


	private static final long serialVersionUID = -5812958866188128427L;

}

