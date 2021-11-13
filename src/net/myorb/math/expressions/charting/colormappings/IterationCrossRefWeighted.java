
package net.myorb.math.expressions.charting.colormappings;

import net.myorb.charting.Histogram;
import net.myorb.charting.ColorSelection;

/**
 * color selection weighted by count of total plot points
 *  as a product with number of references
 * @author Michael Druckman
 */
public class IterationCrossRefWeighted extends ColorSelection
{

	/**
	 * @param name the color scheme name to use
	 */
	public static void addToColorList (String name)
	{
		ContourColorSchemeRequest.addScheme (name, getColorSchemeFactory ());
		System.out.println ("IterationCrossRefWeighted added to list");
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
		setColor (value, (sum += value * references) / agg, 0.1f);
	}

	/**
	 * @return a factory for IterationCrossRefWeighted color selectors
	 */
	public static Factory getColorSchemeFactory ()
	{
		return new Factory ()
		{
			public String toString ()
			{ return "Iteration Cross Ref Weighted"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection.Factory#newColorSelection()
			 */
			public ColorSelection newColorSelection()
			{ return new IterationCrossRefWeighted (); }
		};
	}

	private static final long serialVersionUID = -1014002846210309532L;
}
