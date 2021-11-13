
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.charting.colormappings.LegacyAlternativeAlgorithmColorScheme;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.ColorSelection;

import java.util.HashMap;

/**
 * collect properties required to construct contour plot
 */
public class ContourPlotProperties extends HashMap<String,Object>
	implements DisplayGraphTypes.ContourPlotDescriptor,
		PlotComputers.RealizationTracking
{

	public static String POLAR_IDENTITY = "POLAR";
	public static String EQUATION_IDENTITY = "EQN";

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
	{ put (X_AXIS, point.x); put (Y_AXIS, point.y); }
	public static String X_AXIS = "X", Y_AXIS = "Y";

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
	public double getMultiplier () { return Double.parseDouble (get (MULTIPLIER).toString ()); }
	public void setMultiplier (double multiplier)  { put (MULTIPLIER, multiplier); }
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

	/**
	 * link described plot as parent
	 * @param descriptor the descriptor of the parent
	 */
	public void linkParent (DisplayGraphTypes.ContourPlotDescriptor descriptor)
	{ setPlotParent (descriptor.getPlotNumber ()); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.ContourColorScheme#getColorSelector()
	 */
	public ColorSelection getColorSelector () { return colorSchemeFactory.newColorSelection (); }
	public static void setColorSelectionFactory (ColorSelection.Factory factory) { colorSchemeFactory = factory; }
	private static ColorSelection.Factory colorSchemeFactory = LegacyAlternativeAlgorithmColorScheme.getColorSchemeFactory ();


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphTypes.Transform3D#evaluate(double, double)
	 */
	public int evaluate (double x, double y)
	{
		return equation.evaluate (x, y);
	}
	public double evaluateReal (double x, double y)
	{
		return equation.evaluateReal (x, y);
	}
	public Object evaluateGeneric (double x, double y)
	{
		return equation.evaluateGeneric (x, y);
	}
	public void setEquation
	(DisplayGraphTypes.ContourPlotDescriptor equation) { this.equation = equation; }
	private DisplayGraphTypes.ContourPlotDescriptor equation = null;
	public boolean isEquationSet () { return equation != null; }


	/**
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


	public ContourPlotProperties
	(int rootIdentifier)
	{
		setPlotComputer (PlotComputers.getBruteForcePlotComputer (this));
		setPlotParent (rootIdentifier);
	}

	public ContourPlotProperties (ContourPlotProperties p)
	{
		setPlotComputer (PlotComputers.getBruteForcePlotComputer (this));
		putAll (p); setPlotParent (0); setEquation (p);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.RealizationTracking#setRemaining(int)
	 */
	public void setRemaining (int remaining)
	{
		this.remaining = remaining;
		this.originalSize = remaining;
		this.nextMilestone = 0;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotComputers.RealizationTracking#reduceRemaining(int)
	 */
	public void reduceRemaining (int portion)
	{
		this.remaining -= portion;
		long progress = originalSize - remaining;
		long percent = (progress * 100) / originalSize;
		if (percent < nextMilestone) return;

		System.out.println ("Progress: " + percent + "%");
		nextMilestone += 10;
	}
	protected int remaining, originalSize, nextMilestone;


	private static final long serialVersionUID = -5812958866188128427L;
}

