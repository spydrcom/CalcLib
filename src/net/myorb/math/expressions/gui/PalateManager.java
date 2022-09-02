
package net.myorb.math.expressions.gui;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.DisplayFrame;

import net.myorb.charting.PlotLegend;

import java.awt.Component;

/**
 * manage the availability of the palate tool
 * @author Michael Druckman
 */
public class PalateManager
{


	/**
	 * construct on first reference or force to screen
	 */
	public static void showPalateTool ()
	{
		if (palateToolDisplay == null)
		{
			showFrame
			(
				PlotLegend.getPalateTool ()
					.getPalatePanel ().toComponent ()
			);
		}
		else
		{
			palateToolDisplay.forceToScreen ();
		}
	}


	/**
	 * @param component the display component for the tool
	 */
	public static void showFrame (Component component)
	{
		palateToolDisplay =
			new DisplayFrame (component, "Current Plot Palate");
		component.setPreferredSize (SimpleScreenIO.wXh (300, 500));
		palateToolDisplay.showOrHide ();
	}
	static DisplayFrame palateToolDisplay = null;


}

