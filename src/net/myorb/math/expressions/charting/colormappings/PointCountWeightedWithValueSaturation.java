
package net.myorb.math.expressions.charting.colormappings;

import net.myorb.charting.ColorSelection;
import net.myorb.charting.Histogram;

/**
 * color selection weighted by count of points of similar value.
 *  saturation is depleted as value approaches maximum increasing detail
 * @author Michael Druckman
 */
public class PointCountWeightedWithValueSaturation extends ColorSelection
{

	/**
	 * @param name the color scheme name to use
	 */
	public static void addToColorList (String name)
	{
		ContourColorSchemeRequest.addScheme (name, getColorSchemeFactory ());
		System.out.println ("PointCountWeightedWithValueSaturation added to list");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection#initializeFor(net.myorb.math.expressions.charting.Histogram)
	 */
	public void initializeFor (Histogram histogram)
	{
		count = histogram.countOfAll ();
		size = histogram.size () * 2;
	}
	float sum = 0, count, size;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection#translateFor(java.lang.Long, java.lang.Long)
	 */
	public void translateFor (Long value, Long references)
	{
		float v = references.floatValue ();
		setColor (value, (sum += v) / count, value / size);
	}

	/**
	 * @return a factory for PointCountWeightedWithSaturation color selectors
	 */
	public static Factory getColorSchemeFactory ()
	{
		return new Factory ()
		{
			public String toString ()
			{ return "Point Count With Value Saturation"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection.Factory#newColorSelection()
			 */
			public ColorSelection newColorSelection()
			{ return new PointCountWeightedWithValueSaturation (); }
		};
	}

	private static final long serialVersionUID = 4541024667063833039L;
}
