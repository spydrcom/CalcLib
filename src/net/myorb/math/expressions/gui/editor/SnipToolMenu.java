
package net.myorb.math.expressions.gui.editor;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.gui.components.SimpleMenuBar;
import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.DisplayFrame;

import net.myorb.jxr.JxrParser;

/**
 * menu manager for Snip tool
 * @author Michael Druckman
 */
public class SnipToolMenu extends SimpleScreenIO
{


	/**
	 * JXR source for Snip Tool frame menu bar
	 */
	public static final String CONFIGURATION_PATH = "cfg/gui/SnipToolMenu.xml";


	/**
	 * provide source environment component to menu functions
	 * @param environment access to display components
	 */
	public static void setSource (Environment<?> environment)
	{
		actions.setSource (asTextComponent (SnipToolSupport.getDisplay (environment)));
	}


	/**
	 * use JXR to provide the menus
	 * @param environment access to display components
	 */
	public static void prepareSnipToolActions (Environment<?> environment)
	{
		try { JxrParser.read (CONFIGURATION_PATH, null); }
		catch (Exception e) { e.printStackTrace (); }
		actions.setEnvironment (environment);
		setSource (environment);
	}


	/**
	 * @return the action listeners for the menu items
	 */
	public static SnipToolActions getSnipActions ()
	{ return actions = new SnipToolActions (); }
	static SnipToolActions actions;


	/**
	 * @param menu the menu prepared by JXR ready for display
	 */
	public static void setMenuBar (SimpleMenuBar menu)
	{
		snipToolMenu = menu;
	}


	/**
	 * @param frame the display to have the menu
	 */
	public static void setMenuBar (DisplayFrame frame)
	{ frame.setMenuBar (snipToolMenu.getMenuBar ()); }
	static SimpleMenuBar snipToolMenu;


}

