
package net.myorb.math.expressions.gui.editor;

import net.myorb.gui.components.DisplayFrame;

/**
 * GUI components for Snip edit display
 * @author Michael Druckman
 */
public class SnipToolDisplays extends SnipToolProcessing
{


	/**
	 * construct tab panel with left side index
	 */
	static void buildPanel ()
	{
		connectDrop (buildTabbedPanel ());
	}


	/**
	 * add a text panel with an index count as name
	 */
	static void add ()
	{
		addTab (Integer.toString (tabCount++));
	}
	static int tabCount = 1;


	/**
	 * @return DisplayFrame holding tabs and menu bar
	 */
	static DisplayFrame buildFrame ()
	{
		frame = new DisplayFrame
			(getTabbedPanel (), "Snip Editor");
		setMenuBar (frame);
		return frame;
	}
	static DisplayFrame frame = null;


}

