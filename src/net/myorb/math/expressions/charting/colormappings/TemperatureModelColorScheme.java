
package net.myorb.math.expressions.charting.colormappings;

import net.myorb.gui.graphics.ColorAssignment;

import net.myorb.charting.ColorSelection;

import net.myorb.charting.Histogram;

/**
 * color selection using temperature color model
 * - colors selected by algorithm centered on hue ranges
 * @author Michael Druckman
 */
public class TemperatureModelColorScheme extends ColorSelection
{


	/* (non-Javadoc)
	 * @see net.myorb.charting.ColorSelection#initializeFor(net.myorb.charting.Histogram)
	 */
	public void initializeFor (Histogram histogram)
	{
		this.highest = histogram.getHighest ();
		this.lowest = histogram.getLowest ();
	}
	protected long highest, lowest;


	/**
	 * translate value relative to range
	 * @param value the value to be translated
	 * @return the Color for the value
	 */
	public java.awt.Color colorFor (int value)
	{
		return ColorAssignment.getTemperatureColorFrom (value, lowest, highest);
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.ColorSelection#mapToColors(java.lang.Object[])
	 */
	public void mapToColors (Object[] range)
	{
		for (int i = 0; i < range.length; i++)
		{
			Integer value = (Integer) range [i];
			range [i] = colorFor (value);
		}
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.ColorSelection#translateFor(java.lang.Long, java.lang.Long)
	 */
	public void translateFor (Long value, Long references)
	{
		int intValue = value.intValue ();
		this.put ( intValue, colorFor (intValue) );
	}


	/**
	 * @return a factory for Temperature Color Model Algorithm color selectors
	 */
	public static Factory getColorSchemeFactory ()
	{
		return new Factory ()
		{
			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			public String toString () { return "Temperature Color Model Algorithm"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection.Factory#newColorSelection()
			 */
			public ColorSelection newColorSelection () { return new TemperatureModelColorScheme (); }
		};
	}


	private static final long serialVersionUID = 1085090167117634879L;

}
