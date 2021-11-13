
package net.myorb.math.expressions.charting.colormappings;

import java.awt.Color;

/**
 * attempt to provide broadest spectrum of color range to provide contrast.
 *  playing with adjustment to saturation and brilliance 
 *  based on higher angle counts seen in hue
 * @author Michael Druckman
 */
public class LegacyAlgorithmShadedColorScheme
	extends LegacyAlternativeAlgorithmColorScheme
{

	/* (non-Javadoc)
	 * @see net.myorb.charting.ColorSelection#translateFor(java.lang.Long, java.lang.Long)
	 */
	public void translateFor (Long value, Long references)
	{
		float mapTo = value * (sum += references) / aggregate;
		if (mapTo == 0) { setColor (0, 0, 0, 0); return; }
		int scale = (int) mapTo;
		
		switch (scale)
		{
			case 0: setColor (value, mapTo, 1, 0.75f); break;
			case 1: setColor (value, mapTo-1, 0.5f, 0.5f); break;
			case 2: setColor (value, mapTo-2, 0.25f, 1f); break;
			default: put (value.intValue (), Color.WHITE); break;
		}

		if (DUMPING)
		{
			System.out.print ("v=");
			System.out.print (value);
			System.out.print ("\t mapTo=");
			System.out.print (mapTo);
			System.out.print ("\t scale=");
			System.out.println (scale);
		}
	}


	private static final long serialVersionUID = 7839040420628630811L;

}
