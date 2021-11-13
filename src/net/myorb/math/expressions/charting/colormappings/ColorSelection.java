
package net.myorb.math.expressions.charting.colormappings;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.Histogram;

import java.awt.Color;

/**
 * base class for construction of color maps
 * @author Michael Druckman
 */
public abstract class ColorSelection extends DisplayGraphTypes.ColorMap
{


	/**
	 * construction object for color scheme managers
	 */
	public interface Factory
	{
		/**
		 * @return a new color scheme manager
		 */
		ColorSelection newColorSelection ();
	}


	/**
	 * @param histogram source for initialization of scheme
	 */
	public abstract void initializeFor (Histogram histogram);


	/**
	 * @param value the value to be translated
	 * @param references the number of references to this value
	 */
	public abstract void translateFor (Long value, Long references);


	/**
	 * @param histogram the map of reference counts for each function value
	 */
	public void translateFor (Histogram histogram)
	{
		initializeFor (histogram);
		for (Long value : histogram.mappedEntries ())
		{ translateFor (value, histogram.get (value)); }
	}


	/**
	 * set the color for an item count
	 * @param forItem the item value being colored
	 * @param hueValue the computed hue value for the item being set
	 * @param scale level relative to highest histogram bar
	 */
	public void setColor
	(long forItem, float hueValue, float scale)
	{ put ((int)forItem, getColor (hueValue, scale)); }


	/**
	 * assign a color to a value count
	 * @param level the histogram value to be used
	 * @param scale level relative to highest histogram bar
	 * @return a color to be displayed
	 */
	public Color getColor (float level, float scale)
	{ return Color.getHSBColor (level, 1-scale, 0.9f); }


	/**
	 * assign a display color for each result value of the transform
	 * @param range the list of result values matching the domain points list
	 * @param histogram data collected as iterations were computed
	 */
	public void substituteColors
		(
			Object[] range,
			Histogram histogram
		)
	{
		translateFor (histogram);
		mapToColors (range);
	}


	/**
	 * fill in the colors list matching the iteration counts collected
	 * @param range the representation of the value for each domain point (z-axis)
	 */
	public void mapToColors (Object[] range)
	{
		for (int i = 0; i < range.length; i++)
		{ range[i] = substitute (range[i]); }
	}


	/**
	 * identify assigned color
	 * @param v the value of the function
	 * @return the assigned color
	 */
	private Object substitute (Object v)
	{
		int i = (Integer) v;
		if (i < 0) i = -i;
		return get (i);
	}


	private static final long serialVersionUID = 1894349603800416836L;
}


