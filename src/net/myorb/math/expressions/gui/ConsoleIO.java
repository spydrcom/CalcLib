
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.EvaluationControlI;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.TextEditor.TextProcessor;

import net.myorb.gui.components.Console;
import net.myorb.gui.components.GuiToolkit;
import net.myorb.gui.components.MenuManager;
import net.myorb.gui.components.StreamDisplay;
import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.text.JTextComponent;
import javax.swing.JTextArea;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;

/**
 * extended version of GUI console.
 *  IO class extends DisplayIO using Console class
 * @author Michael Druckman
 */
public class ConsoleIO extends DisplayIO
{

	public static void main (String[] args)
	{
		showConsole ("CALCLIB", 700);
	}

	/**
	 * the console frame for the GUI
	 * @param title the title for the frame
	 * @param size the console panel preferred size
	 * @return the constructed command processor
	 */
	public static CommandHandler showConsole (String title, int size)
	{
		ExtendedConsole console =
			new ExtendedConsole
			(
				Color.white,
				size, size, 60
			);
		console.assembleComponents (size);
		console.showInDisplayFrame (title);
		return console.getCommandHandler ();
	}

	/**
	 * link to the evaluation control layer
	 * @param title the title for the frame to be displayed
	 * @param size  the console panel preferred size
	 * @param environment the execution environment 
	 * @param control the evaluation control object
	 * @return the map of GUI resources
	 */
	public static DisplayConsole.StreamProperties connectConsole
		(
			String title, int size,
			Environment <?> environment,
			EvaluationControlI <?> control
		)
	{
		CommandHandler handler = showConsole (title, size);
		if (!LEGACY) GuiToolkit.setMenuBar (getMenuBar (handler));
		return handler.connect (environment, control);
	}

}

/**
 * swing description of GUI console
 */
class ExtendedConsole extends Console
	implements TextProcessor
{

	public ExtendedConsole
		(
			Color color,
			int width, int height,
			int columns
		)
	{
		super (color, width, height, columns);
		w = width; h = height;
	}
	int w, h;

	/**
	 * construct the console components
	 * @param size the size of the main panel
	 */
	void assembleComponents (int size)
	{
		constructPanel (size);
		constructPrompt ();
		addScrollPane ();
	}
	public static void excludeTabs () { INCLUDE_TABS = false; }
	static boolean INCLUDE_TABS = true;

	/**
	 * display console in a frame
	 * @param title the title for the frame
	 */
	void showInDisplayFrame (String title)
	{
		SimpleScreenIO.Frame f =
			show ("CALCLIB", w, h);
		f.setIcon ("images/logo.gif");
	}

	/**
	 * the core panel for the display
	 * @param size edge preferred size
	 */
	void constructPanel (int size)
	{
		PANEL_SIZE = new Dimension
			(size + DisplayIO.MARGIN, size + DisplayIO.MARGIN);
		panel.setFont (new Font ("Arial Unicode MS", java.awt.Font.PLAIN, 14));
		panel.setSize (PANEL_SIZE);

		JTextArea c = (JTextArea) textArea;
		panel.addMouseListener
		(
			MenuManager.getMenu
			(
				getMenuActions (c, "workspace", this)
			)
		);
		NotationMenu.addPopupMenuTo
		(c, (text) -> appendLine (text, c));
	}
	Dimension PANEL_SIZE;

	/**
	 * @param text the text being appended
	 * @param to the component the text adds to
	 */
	public void appendLine (String text, JTextArea to)
	{
		to.append (text);
		this.append (text);
	}

	/**
	 * @param console the text area of the console
	 * @param title the title to apply to the save command
	 * @param processor the text processor action
	 * @return the action list for the menu
	 */
	public MenuManager.ActionList getMenuActions
	(JTextArea console, String title, TextProcessor processor)
	{
		MenuManager.ActionList items =
			new MenuManager.ActionList ();
		items.add
		(
			new UseSelectedCommand
			(
				console,
				(text) -> appendLine (text, console)
			)
		);
		items.add (new SaveCommand (console, title));
		items.add (new ClearCommand (console));
		return items;
	}

	/**
	 * user input prompt component
	 */
	void constructPrompt ()
	{
		JTextComponent prompt;
		handler = new CommandHandler (prompt = getTextArea ());
		(coreMap = handler.getMap ()).put (EnvironmentCore.CoreCommandLine, prompt);
	}
	EnvironmentCore.CoreMap coreMap;

	/**
	 * @return the handler for the established prompt
	 */
	CommandHandler getCommandHandler ()
	{
		return handler;
	}
	CommandHandler handler;

	/**
	 * scroll pane for display area
	 */
	void addScrollPane ()
	{
		SimpleScreenIO.Scrolling s =
			getScrollingPanel ();
		StreamDisplay.setProperties
		(
			coreMap,
			EnvironmentCore.CoreMainConsole,
			(JTextArea) textArea, s
		);
		coreMap.put (EnvironmentCore.CoreDisplayArea, s);
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.Console#process(java.lang.String)
	 */
	public void process (String line)
	{
		System.out.println (line);
		handler.execute (line);
	}

}
