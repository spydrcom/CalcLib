
package net.myorb.math.expressions.charting.colormappings;

import net.myorb.charting.ColorSelection;
import net.myorb.charting.Histogram;

/**
 * color selection weighted by value relative to range.
 *  saturation is depleted as ref count approaches maximum increasing detail
 * @author Michael Druckman
 */
public class ValueWeightedWithAggregation extends ColorSelection
{

	/**
	 * @param name the color scheme name to use
	 */
	public static void addToColorList (String name)
	{
		ContourColorSchemeRequest.addScheme (name, getColorSchemeFactory ());
		System.out.println ("ValueWeightedWithAggregation added to list");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection#initializeFor(net.myorb.math.expressions.charting.Histogram)
	 */
	public void initializeFor (Histogram histogram)
	{
		agg = histogram.sumOfAll ();
		size = histogram.size ();
	}
	float sum = 0, size, agg;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection#translateFor(java.lang.Long, java.lang.Long)
	 */
	public void translateFor (Long value, Long references)
	{
		setColor (value, value / size, references / agg);
	}

	/**
	 * @return a factory for PointCountWeightedWithSaturation color selectors
	 */
	public static Factory getColorSchemeFactory ()
	{
		return new Factory ()
		{
			public String toString ()
			{ return "Value Weighted With Aggregation"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection.Factory#newColorSelection()
			 */
			public ColorSelection newColorSelection()
			{ return new ValueWeightedWithAggregation (); }
		};
	}

	private static final long serialVersionUID = 6712901575791140531L;
}
