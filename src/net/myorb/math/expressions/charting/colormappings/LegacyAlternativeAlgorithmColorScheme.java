
package net.myorb.math.expressions.charting.colormappings;

import net.myorb.charting.ColorSelection;
import net.myorb.charting.Histogram;

import java.util.List;

/**
 * attempt to provide broadest spectrum of color range to provide contrast
 * @author Michael Druckman
 */
public class LegacyAlternativeAlgorithmColorScheme extends ColorSelection
{

	/**
	 * @param name the color scheme name to use
	 */
	public static void addToColorList (String name)
	{
		ContourColorSchemeRequest.addScheme (name, getColorSchemeFactory ());
		System.out.println ("LegacyAlternativeAlgorithm added to list");
	}

	/* (non-Javadoc)
	 * @see net.myorb.charting.ColorSelection#initializeFor(net.myorb.charting.Histogram)
	 */
	public void initializeFor (Histogram histogram)
	{
		this.aggregate = histogram.sumOfAll ();
		//System.out.println ("agg = " + aggregate);
		this.histogram = histogram;
		adjust ();
	}
	protected Histogram histogram;
	protected float aggregate;


	/**
	 * remove top 5 contributed values
	 *  eliminates pole overwhelming 
	 */
	void adjust ()
	{
		List<Long>
			entries = histogram.mappedEntries ();
		int count = entries.size ();

		for (int i = 1; i <= 5; i++)
		{
			long entry = entries.get (count-i);
			float num = (float) histogram.get (entry);
			//System.out.println ("Adjust: " + entry + " * " + num);
			aggregate -= num * (float) entry;
		}
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.ColorSelection#translateFor(java.lang.Long, java.lang.Long)
	 */
	public void translateFor (Long value, Long references)
	{
		float mapTo = value * (sum += references) / aggregate;

		if (DUMPING)
		{
			System.out.print ("v=");
			System.out.print (value);
			System.out.print ("\t mapTo=");
			System.out.println (mapTo);
		}

		setColor (value, mapTo, 0.1f);
	}
	protected float sum = 0f;


	/* (non-Javadoc)
	 * @see net.myorb.charting.ColorSelection#legend()
	 */
	public void legend ()
	{
		
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.ColorSelection#dump()
	 */
	public void dump ()
	{
		if (!DUMPING) return;

		System.out.println ("color map entries");

		for (int k : this.keySet ())
		{
			System.out.print ("v="); System.out.print (k);
			String hex = Integer.toHexString (this.get (k).getRGB ());
			System.out.print ("\t hex="); System.out.println (hex);
		}

		System.out.println ("histogram entries");

		for (Long value : histogram.mappedEntries ())
		{
			System.out.print ("v="); System.out.print (value);
			System.out.print ("\t map="); System.out.println (histogram.get (value));
		}
	}
	protected boolean DUMPING = false;


	/**
	 * @return a factory for Legacy Alternative Algorithm color selectors
	 */
	public static Factory getColorSchemeFactory ()
	{
		return new Factory ()
		{
			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			public String toString ()
			{ return "Legacy Alternative Algorithm"; }

			/* (non-Javadoc)
			 * @see net.myorb.math.expressions.charting.colormappings.ColorSelection.Factory#newColorSelection()
			 */
			public ColorSelection newColorSelection ()
			{ return new LegacyAlternativeAlgorithmColorScheme (); }
		};
	}


	private static final long serialVersionUID = -2448428331097784028L;

}
