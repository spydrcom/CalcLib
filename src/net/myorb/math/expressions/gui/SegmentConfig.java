
package net.myorb.math.expressions.gui;

import net.myorb.charting.DisplayGraphSegmenting;
import net.myorb.charting.DisplayGraphSegmentTools;

/**
 * collect configuration items for asymptotic segmenting algorithms.
 *  in absence of asymptotes, general range clipping is provided
 * @author Michael Druckman
 */
public class SegmentConfig extends FormComponents implements FormComponents.Form
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.FormComponents.Form#publish()
	 */
	public void publish ()
	{
		String rangeLimit = limit.getText ();

		segmenting.setSegmenting (segments.getValue ());
		if (rangeLimit.isEmpty ()) { segmenting.setRangeUnlimited (); }
		else { segmenting.setRangeLimit (Double.parseDouble (rangeLimit)); }
		segmenting.setSeriesMinimum (Integer.parseInt (minimum.getText ()));

//		System.out.println ("PUBLISH limit=" + limit.getText() + " minimum=" + minimum.getText() + " Segment=" + segments.getValue());

		if (processor != null) new Thread (processor).start ();
	}
	Runnable processor;

	/**
	 * @return panel of components to display
	 */
	Panel getPanel ()
	{
		Panel p = new Panel ();
		addLabel (p, "Range Limit");
		limit = addFieldTo (p, "");
		addLabel (p, "Minimum Segement Size");
		minimum = addFieldTo (p, Integer.toString (segmenting.getSeriesMinimum ()));
		segments = addCheckBox (p, "Segmented Breaks", true);
		p.add (getButtonPanel (this));
		return p;
	}
	Field limit, minimum;
	CheckBox segments;

	/**
	 * @param panel field added to panel
	 * @param defaultValue the default to be placed in field
	 * @return the field object
	 */
	static Field addFieldTo (Panel panel, String defaultValue)
	{
		return newSelectedDefaultField (5, defaultValue, panel);
	}

	/**
	 * @param processor the Runnable object to be invoked
	 */
	SegmentConfig (Runnable processor)
	{
		this.processor = processor;
		this.segmenting = DisplayGraphSegmentTools.getSegmentControl ();
		showFrame (getPanel (), "Segment Processing Configuration");
	}
	DisplayGraphSegmenting segmenting;

	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String... args)
	{
		new SegmentConfig (null);
	}

}
