
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


	public static MultiSegmentUtilities.SegmentManager getSegmentManager
				(MouseSampleTrigger <?> trigger)
	{
		return MultiSegmentUtilities.getSegmentUtilities (trigger);
	}

	public static MultiSegmentUtilities.SegmentManager getSegmentManager
		(MouseSampleTrigger <?> trigger, String expression)
	{
		MultiSegmentUtilities.SegmentManager mgr =
			getSegmentManager (trigger);
		mgr.setExprs (new String[]{expression});
		return mgr;
	}

	public static void show (String title, JComponent display)
	{
		DisplayGraphPrimitives.showFrame
			(title, display);
	}


}
