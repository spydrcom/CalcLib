
package net.myorb.math.expressions.gui.editor;

import net.myorb.math.expressions.gui.DisplayConsole;
import net.myorb.math.expressions.gui.EnvironmentCore;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.SimpleMenuBar;
import net.myorb.gui.components.SnipFrame;
import net.myorb.jxr.JxrParser;

/**
 * an editor specifically for tool content
 * @author Michael Druckman
 */
public class SnipTool extends SnipFrame
{

	/**
	 * @param environment access to display components
	 */
	public SnipTool (Environment<?> environment)
	{
		super (SimpleScreenIO.asTextComponent (getDisplay (environment)));
		if (actions == null) prepareSnipToolActions (environment);
		this.setMenu (snipToolMenu.getMenuBar ());
		this.environment = environment;
		actions.setSource (source);
	}
	protected Environment<?> environment;

	/**
	 * use JXR to provide the menus
	 * @param environment access to display components
	 */
	public void prepareSnipToolActions (Environment<?> environment)
	{
		try { JxrParser.read ("cfg/gui/SnipToolMenu.xml", null); }
		catch (Exception e) { e.printStackTrace (); }
		actions.setEnvironment (environment);
		actions.connectTool (this);
	}

	/**
	 * @return the action listeners for the menu items
	 */
	public static SnipToolActions getSnipActions ()
	{
		return actions = new SnipToolActions ();
	}
	static SnipToolActions actions = null;

	/**
	 * @param menu the menu prepared by JXR ready for display
	 */
	public static void setMenuBar (SimpleMenuBar menu)
	{
		snipToolMenu = menu;
	}
	static SimpleMenuBar snipToolMenu = null;

	/**
	 * @param environment access to display components
	 * @return the text component with source of text to edit
	 */
	public static Object getDisplay (Environment<?> environment)
	{
		DisplayConsole.StreamProperties
		guiMap = environment.getControl ().getGuiMap ();
		return guiMap.get (EnvironmentCore.CoreDisplayComponent);
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#show()
	 */
	public void show ()
	{
		show ("Snip Editor", SimpleScreenIO.wXh (500, 300));
	}

	private static final long serialVersionUID = -5090628039978268126L;

}


