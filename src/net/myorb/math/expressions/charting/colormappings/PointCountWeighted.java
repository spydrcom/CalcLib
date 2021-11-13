
package net.myorb.math.expressions.charting.colormappings;

import net.myorb.charting.ColorSelection;
import net.myorb.charting.Histogram;

/**
 * color selection weighted by count of points of similar value
 * @author Michael Druckman
 */
public class PointCountWeighted extends ColorSelection
{

	/**
	 * @param name the color scheme name to use
	 */
	public static void addToColorList (String name)
	{
		ContourColorSchemeRequest.addScheme (name, getColorSchemeFactory ());
		System.out.println ("PointCountWeighted added to list");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection#translateFor(java.lang.Long, java.lang.Long)
	 */
	public void translateFor (Long value, Long references) { setColor (value, (sum += references) / count, 0.1f); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection#initializeFor(net.myorb.math.expressions.charting.Histogram)
	 */
	public void initializeFor (Histogram histogram)
	{ count = histogram.countOfAll (); }
	float sum = 0; int count;

	/**
	 * @return a factory for PointCountWeighted color selectors
	 */
	public static Factory getColorSchemeFactory ()
	{
		return new Factory ()
		{
			public String toString ()
			{ return "Point Count Weighted"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection.Factory#newColorSelection()
			 */
			public ColorSelection newColorSelection()
			{ return new PointCountWeighted (); }
		};
	}

	private static final long serialVersionUID = -4027031413766047974L;
}
