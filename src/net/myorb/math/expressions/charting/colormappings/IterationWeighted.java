
package net.myorb.math.expressions.charting.colormappings;

import net.myorb.charting.ColorSelection;
import net.myorb.charting.Histogram;

/**
 * color selection weighted by count of total plot points
 * @author Michael Druckman
 */
public class IterationWeighted extends ColorSelection
{

	/**
	 * @param name the color scheme name to use
	 */
	public static void addToColorList (String name)
	{
		ContourColorSchemeRequest.addScheme (name, getColorSchemeFactory ());
		System.out.println ("IterationWeighted added to list");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection#initializeFor(net.myorb.math.expressions.charting.Histogram)
	 */
	public void initializeFor (Histogram histogram)
	{
		agg = histogram.sumOfAll ();
	}
	float sum = 0, agg;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection#translateFor(java.lang.Long, java.lang.Long)
	 */
	public void translateFor (Long value, Long references)
	{
		setColor (value, value * (sum += references) / agg, 0.1f);
	}

	/**
	 * @return a factory for IterationWeighted color selectors
	 */
	public static Factory getColorSchemeFactory ()
	{
		return new Factory ()
		{
			public String toString ()
			{ return "Iteration Weighted"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection.Factory#newColorSelection()
			 */
			public ColorSelection newColorSelection()
			{ return new IterationWeighted (); }
		};
	}

	private static final long serialVersionUID = 9039273651704800209L;
}
