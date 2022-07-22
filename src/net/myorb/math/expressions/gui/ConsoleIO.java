
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
import javax.swing.JMenu;

import java.awt.Color;
import java.awt.Font;

import java.util.ArrayList;
import java.util.List;

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
	protected int w, h;

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
		panel.setFont
		(
			new Font
			(
				"Arial Unicode MS",
				java.awt.Font.PLAIN, 14
			)
		);

		panel.setSize
		(
			MasterConsole.getPreferredSize (size)
		);

		addMenus ((JTextArea) textArea);
	}

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
	 * @param to console display area
	 */
	public void addMenus (JTextArea to)
	{
		List<JMenu> menuList = new ArrayList<JMenu>();

		MenuManager.ActionList editorActions =
			TextEditor.getMenuActions
			(
				to, "workspace",
				(text) -> appendLine (text, to)
			);

		menuList.add
		(
			MenuManager.getMenuOf (editorActions, "Edit")
		);

		NotationMenu.getMenus ((text) -> appendLine (text, to), menuList);

		panel.addMouseListener
		(
			MenuManager.getMenu (menuList.toArray (new JMenu[1]))
		);
	}

	/**
	 * user input prompt component
	 */
	void constructPrompt ()
	{
		JTextComponent prompt;
		handler = new CommandHandler (prompt = getTextArea ());
		(coreMap = handler.getMap ()).put (EnvironmentCore.CoreCommandLine, prompt);
		handler.setEcho (false);
	}
	protected EnvironmentCore.CoreMap coreMap;

	/**
	 * @return the handler for the established prompt
	 */
	CommandHandler getCommandHandler ()
	{
		return handler;
	}
	protected CommandHandler handler;

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
		coreMap.put (EnvironmentCore.CoreDisplayComponent, textArea);
		coreMap.put (EnvironmentCore.CoreDisplayArea, s);
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.Console#process(java.lang.String)
	 */
	public void process (String line)
	{
		//System.out.println (line);
		handler.execute (line);
	}

}
