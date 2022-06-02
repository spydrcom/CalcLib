
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.EvaluationControlI;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.gui.DisplayConsole;
import net.myorb.math.expressions.gui.Functionality;
import net.myorb.math.expressions.gui.ToolBarMenu;
import net.myorb.math.expressions.gui.DisplayIO;
import net.myorb.math.expressions.gui.ConsoleIO;

import net.myorb.gui.components.GuiToolkit;

import javax.swing.JMenuBar;

/**
 * evaluation control for system that uses JXR symbol configuration.
 *  JXR control of GUI configuration also supported.
 * @param <T> manager for data type
 * @author Michael Druckman
 */
public class ConfiguredScriptedEvaluationControl <T>
	extends ConfiguredEvaluationControl <T>
{


	public ConfiguredScriptedEvaluationControl
		(Environment <T> environment)
	{
		super (environment);
	}


	/**
	 * specify display component to use
	 * @param useConsole T for console and F for legacy debug version
	 */
	public void chooseConsoleDisplay
		(boolean useConsole)
	{
		USE_CONSOLE_COMPONENT = useConsole;
	}
	static boolean USE_CONSOLE_COMPONENT = false;


	/**
	 * construct and show console
	 * @return command control resulting from console construction
	 */
	public DisplayIO.CommandControl getCommandControl ()
	{
		if (USE_CONSOLE_COMPONENT)
			return ConsoleIO.showConsole (toString (), SCREEN_SIZE);
		return DisplayIO.showConsole (toString (), SCREEN_SIZE);
	}


	/**
	 * direct refactor of GUI connect from original design
	 */
	public void connectGui ()
	{
		DisplayIO.CommandControl
			control = getCommandControl ();
		menuSetup (control); mapConsole (control);
	}


	/**
	 * direct refactor of menu construction from original design
	 * @param control the evaluation control object
	 */
	public void menuSetup (DisplayIO.CommandProcessor control)
	{
		setMenuBar (DisplayIO.getMenuBar (control));
		System.out.println ("Menu Script Complete");
	}


	/**
	 * get the path to the JXR menu bar script
	 * @return the identified script path for the menu
	 */
	public String getMenuBarScript ()
	{
		System.out.println ();
		String path = DisplayIO.getMenuBarScript ();
		System.out.println ("Menu Script Path: " + path);
		return path;
	}


	/**
	 * get the action listener objects for the menu bar
	 * @param control the evaluation control object
	 * @return the action tree for the menu items
	 */
	public Functionality.ActionManager getActionManager
			(DisplayIO.CommandControl control)
	{
		Functionality.ActionManager manager =
			ToolBarMenu.getActionManager (control);
		manager.setAppParent (GuiToolkit.getDesktop ());
		return manager;
	}


	/**
	 * connect menu to MDI
	 * @param menu the menu bar to attach to the MDI
	 */
	public void setMenuBar (JMenuBar menu)
	{
		GuiToolkit.setMenuBar (menu);
	}


	/**
	 * connect console and map environment
	 * @param control the evaluation control object
	 */
	public void mapConsole (DisplayIO.CommandControl control)
	{
		guiSymbolMap =
			connectConsole (this, control, environment);
		mapEnvironment ();
	}


	/**
	 * connect command and evaluation controls
	 * @param control the evaluation control object
	 * @param handler a command handler connected to a master console
	 * @param environment the object controlling the evaluation environment
	 * @return the hash of stream properties
	 */
	public static DisplayConsole.StreamProperties connectConsole
		(
			EvaluationControlI <?> control,
			DisplayIO.CommandControl handler,
			Environment <?> environment
		)
	{
		return handler.connect (environment, control);
	}


}

