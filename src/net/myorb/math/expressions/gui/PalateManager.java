
package net.myorb.math.expressions.gui;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.DisplayFrame;

import net.myorb.gui.palate.PalateTool;

import javax.swing.JComponent;

/**
 * manage the availability of the palate tool
 * @author Michael Druckman
 */
public class PalateManager extends PalateTool
{


	/**
	 * construct on first reference or force to screen
	 */
	public static void showPalateTool ()
	{
		if (palateToolDisplay == null)
		{
			showFrame (new PalateManager ());
		}
		else
		{
			palateToolDisplay.forceToScreen ();
		}
	}


	/**
	 * @param component the display component for the tool
	 */
	public static void showFrame (JComponent component)
	{
		palateToolDisplay =
			new DisplayFrame (component, "Palate Tool");
		component.setPreferredSize (SimpleScreenIO.wXh (900, 500));
		palateToolDisplay.showOrHide ();
	}
	static DisplayFrame palateToolDisplay = null;


	private static final long serialVersionUID = -822309604759704569L;

}
