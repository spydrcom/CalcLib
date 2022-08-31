
package net.myorb.math.expressions.charting.implementations;

import net.myorb.math.expressions.charting.DisplayGraphPrimitives;
import net.myorb.math.expressions.charting.MultiSegmentUtilities;
import net.myorb.math.expressions.charting.MouseSampleTrigger;

import javax.swing.JComponent;

/**
 * segment utilities for building charts
 * @author Michael Druckman
 */
public class ChartHelpers
{


	/*
	 * helper methods
	 */


	/**
	 * @param trigger a trigger object holding legend meta-data
	 * @return a segment manager with the data organized
	 */
	public static MultiSegmentUtilities.SegmentManager getSegmentManager
				(MouseSampleTrigger <?> trigger)
	{
		return MultiSegmentUtilities.getSegmentUtilities (trigger);
	}

	/**
	 * @param trigger a trigger object holding legend meta-data
	 * @param expression text of an expression to use as meta-data
	 * @return a segment manager with the data organized
	 */
	public static MultiSegmentUtilities.SegmentManager getSegmentManager
		(MouseSampleTrigger <?> trigger, String expression)
	{
		MultiSegmentUtilities.SegmentManager mgr =
			getSegmentManager (trigger);
		mgr.setExprs (new String[]{expression});
		return mgr;
	}

	/**
	 * @param title a title for the frame
	 * @param display a swing component with the chart
	 */
	public static void show (String title, JComponent display)
	{
		DisplayGraphPrimitives.showFrame
			(title, display);
	}


}
