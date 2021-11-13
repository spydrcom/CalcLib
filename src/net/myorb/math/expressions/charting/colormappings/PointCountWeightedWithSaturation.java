
package net.myorb.math.expressions.charting.colormappings;

import net.myorb.charting.ColorSelection;
import net.myorb.charting.Histogram;

/**
 * color selection weighted by count of points of similar value.
 *  saturation is depleted as reference count approaches peak giving sections of black
 *  and increased contrast as peak is approached
 * @author Michael Druckman
 */
public class PointCountWeightedWithSaturation extends ColorSelection
{

	/**
	 * @param name the color scheme name to use
	 */
	public static void addToColorList (String name)
	{
		ContourColorSchemeRequest.addScheme (name, getColorSchemeFactory ());
		System.out.println ("PointCountWeightedWithSaturation added to list");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection#initializeFor(net.myorb.math.expressions.charting.Histogram)
	 */
	public void initializeFor (Histogram histogram)
	{
		count = histogram.countOfAll ();
		max = histogram.max ();
	}
	float max, sum = 0, count;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection#translateFor(java.lang.Long, java.lang.Long)
	 */
	public void translateFor (Long value, Long references)
	{
		float v = references.floatValue ();
		setColor (value, (sum += v) / count, v / max);
	}

	/**
	 * @return a factory for PointCountWeightedWithSaturation color selectors
	 */
	public static Factory getColorSchemeFactory ()
	{
		return new Factory ()
		{
			public String toString ()
			{ return "Point Count With Ref Saturation"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection.Factory#newColorSelection()
			 */
			public ColorSelection newColorSelection()
			{ return new PointCountWeightedWithSaturation (); }
		};
	}

	private static final long serialVersionUID = 7947250655531497265L;
}
