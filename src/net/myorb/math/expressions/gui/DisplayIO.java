
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.GreekSymbols;
import net.myorb.math.expressions.EvaluationControlI;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.gui.TextEditor.TextProcessor;

import net.myorb.data.abstractions.ErrorHandling;

import net.myorb.gui.components.DisplayFrame;
import net.myorb.gui.components.MenuManager;
import net.myorb.gui.components.GuiToolkit;

import javax.swing.KeyStroke;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JMenu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.PrintStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * a GUI for the expression evaluation layer
 * @author Michael Druckman
 */
public class DisplayIO extends DisplayFrame
{


	public static void
	useUpdatedConfiguration () { LEGACY = false; }
	public static void useLegacyConfiguration () { LEGACY = true; }
	static boolean LEGACY = true;


	/**
	 * describe an interface to the expression layer
	 */
	public interface CommandProcessor
	{
		/**
		 * execute a command
		 * @param command the text of the command
		 */
		void execute (String command);

		/**
		 * @return environment for the processor
		 */
		Environment <?> getEnvironment ();

		/**
		 * get a map to GUI resources
		 * @return the map object
		 */
		EnvironmentCore.CoreMap getMap ();
	}

	/**
	 * command control and process functionality
	 */
	public interface CommandControl extends CommandProcessor
	{
		/**
		 * @param environment the execution environment 
		 * @param control the evaluation control object
		 * @return the application core map
		 */
		EnvironmentCore.CoreMap connect
		(
			Environment <?> environment,
			EvaluationControlI <?> control
		);
	}


	/**
	 * the console frame for the GUI
	 * @param title the title for the frame
	 * @param size the console panel preferred size
	 * @return the constructed command processor
	 */
	public static CommandHandler showConsole (String title, int size)
	{
		MasterConsole console = new MasterConsole ();

		if (LEGACY) console.setScrollSize (LEGACY_SCROLL_SIZE);
		else console.useUpdated (UPDATED_SCROLL_SIZE);

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


	/**
	 * get access to the action listeners for the master menu
	 * @param handler a command handler connected to a master console
	 * @return the action manager for the current functionality list
	 */
	public static Functionality.ActionManager getMenuBarActions (CommandHandler handler)
	{
		return ToolBarMenu.getActionManager (handler);
	}


	/**
	 * use the full master menu bar
	 * @param handler the application command processor
	 * @return the application menu bar
	 */
	public static JMenuBar getMasterMenuBar (CommandHandler handler)
	{
		return ToolBarGenericMenu.getMasterMenuBar
			(
				handler,
				GuiToolkit.getDesktop ()
			);
	}


	/**
	 * produce a menu bar for the application
	 * @param handler the application command processor
	 * @return the application menu bar
	 */
	public static JMenuBar getMenuBar (CommandProcessor handler)
	{
		return ToolBarGenericMenu.getConfiguredMenuBar
			(
				menuScript, handler,
				GuiToolkit.getDesktop ()
			);
	}


	/**
	 * identify the script that will configure the menu bar
	 * @param menuScriptPath path to the script describing the menu bar
	 */
	public static void setMenuBarScript (String menuScriptPath)
	{
		menuScript = menuScriptPath;
	}
	public static String getMenuBarScript () { return menuScript; }
	static String menuScript = "cfg/gui/MasterMenuBar.xml";


	/**
	 * unit test
	 * @param args not used
	 */
	public static void main (String [] args)
	{
		showConsole ("System Output", 700);
		DisplayTests.main (new String [] {});
	}


	static final Dimension
		LEGACY_SCROLL_SIZE = new Dimension (200, 600);
	static Dimension UPDATED_SCROLL_SIZE = new Dimension (200, 700);


}


/**
 * swing description of GUI console
 */
class MasterConsole
{

	/**
	 * alter configuration to CalcToolsGUI from debug platform
	 * @param scrollSize the dimensions of the scroll area
	 */
	public void useUpdated (Dimension scrollSize)
	{
		setScrollSize (scrollSize);
		excludeTabs ();
	}

	/**
	 * construct the console components
	 * @param size the size of the main panel
	 */
	void assembleComponents (int size)
	{
		constructLayout ();
		constructPanel (size); constructPrompt ();
		if (INCLUDE_TABS) addTabbedPane ();
		addScrollPane ();
		addPrompt ();
	}
	public static void excludeTabs () { INCLUDE_TABS = false; }
	static boolean INCLUDE_TABS = true;

	/**
	 * display console in a frame
	 * @param title the title for the frame
	 */
	void showInDisplayFrame (String title)
	{
		DisplayFrame frame = new DisplayFrame (panel, title);
		frame.setIcon ("images/logo.gif");
		frame.showAndExit ();
	}

	/**
	 * use GridBag for layout
	 */
	void constructLayout ()
	{
		gridbag = new GridBagLayout (); c = new GridBagConstraints ();
        c.gridwidth = GridBagConstraints.REMAINDER; c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
	}
	GridBagLayout gridbag;
    GridBagConstraints c;

	/**
	 * the core panel for the display
	 * @param size edge preferred size
	 */
	void constructPanel (int size)
	{
		panel = new JPanel (gridbag);
		setPreferredSize (panel, size);
	}
	JPanel panel;

	/**
	 * @param edgeSize each side of square display
	 * @return a Dimension object with sized edges
	 */
	public static Dimension getPreferredSize (int edgeSize)
	{
		return new Dimension
		(
			edgeSize + DisplayIO.MARGIN,
			edgeSize + DisplayIO.MARGIN
		);
	}

	/**
	 * @param c the component to resize
	 * @param size the edge size for the operation
	 */
	public static void setPreferredSize (JComponent c, int size)
	{
		c.setPreferredSize (getPreferredSize (size));
	}

	/**
	 * add component to panel with GridBag layout
	 * @param component the component to be added
	 */
	void addConstrainedToPanel (JComponent component)
	{
		gridbag.setConstraints (component, c);
		panel.add (component);
	}

	/**
	 * user input prompt component
	 */
	void constructPrompt ()
	{
		handler = new CommandHandler (prompt = new JTextField ());
		(coreMap = handler.getMap ()).put (EnvironmentCore.CoreCommandLine, prompt);
		prompt.addActionListener (handler); NotationMenu.addPopupMenuTo (prompt);
	}
	EnvironmentCore.CoreMap coreMap;
	JTextField prompt;

	/**
	 * @return the handler for the established prompt
	 */
	CommandHandler getCommandHandler ()
	{
		return handler;
	}
	CommandHandler handler;

	/**
	 * menu of operations in tabs
	 */
	void addTabbedPane ()
	{
		tabs = ToolBar.buildTabbedPane (handler);
		addConstrainedToPanel (tabs);
	}
	JTabbedPane tabs;

	/**
	 * scroll pane for display area
	 */
	void addScrollPane ()
	{
		c.gridheight = 50;
		scroll = DisplayConsole.displayArea
			(EnvironmentCore.CoreMainConsole, coreMap, "WorkSpace", handler);
		coreMap.put (EnvironmentCore.CoreDisplayArea, scroll);
		scroll.setPreferredSize (scrollSize);
		addConstrainedToPanel (scroll);
	}

	/**
	 * @param scrollSize the dimensions of the scroll area
	 */
	public void setScrollSize (Dimension scrollSize)
	{ this.scrollSize = scrollSize; }
	Dimension scrollSize;
	JScrollPane scroll;

	/**
	 * prompt area added to GUI
	 */
	void addPrompt ()
	{
		addConstrainedToPanel (prompt);
	}

}


/**
 * the action handler for the command text component
 */
class CommandHandler
	implements ActionListener, DisplayIO.CommandControl, TextEditor.TextProcessor
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.DisplayIO.CommandProcessor#getMap()
	 */
	public EnvironmentCore.CoreMap
		getMap () { return coreMap; }
	EnvironmentCore.CoreMap coreMap;

	/**
	 * connect to the Evaluation Control object that will execute commands
	 * @param environment the execution environment 
	 * @param control the control object
	 * @return the core map
	 */
	public EnvironmentCore.CoreMap connect
	(Environment <?> environment, EvaluationControlI <?> control)
	{
		this.environment = environment;
		this.control = control;
		return coreMap;
	}
	EvaluationControlI <?> control;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.DisplayIO.CommandProcessor#getEnvironment()
	 */
	public Environment <?>
		getEnvironment () { return environment; }
	Environment <?> environment;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.DisplayIO.CommandProcessor#execute(java.lang.String)
	 */
	public void execute (String command)
	{
		if (environment == null) return;
		execute (command, environment.getOutStream ());
	}
	public void execute (String command, PrintStream out)
	{
		ErrorHandling.process
		(
			() ->
			{
				if (echoState) out.println (command);
				control.execute (command);
				out.println ();
				out.flush ();
			},
			out
		);
	}

	/**
	 * @param state value to place on echo state
	 */
	public void setEcho (boolean state)
	{
		echoState = state;
	}
	boolean echoState = true;

	/**
	 * @return access to command execution processor
	 */
	public TextEditor.TextProcessor getExecutionProcessor ()
	{
		return (t) -> execute (t);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		execute (commandLineInput.getText ());
		commandLineInput.setText ("");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.TextEditor.TextProcessor#process(java.lang.String)
	 */
	public void process (String text)
	{
		if (TextEditor.hasEndOfLine (text))
		{ TextEditor.processMultiLine (text, getExecutionProcessor ()); }
		else commandLineInput.setText (commandLineInput.getText () + " " + text);
	}

	/**
	 * constructed to connect with the text component
	 * @param textField the text component that accepts commands
	 */
	CommandHandler (JTextComponent textField)
	{
		this.commandLineInput = textField; this.coreMap = new EnvironmentCore.CoreMap ();
		this.coreMap.put (EnvironmentCore.CoreCommandProcessor, this);
	}
	JTextComponent commandLineInput;

}


/**
 * Greek notations menu
 */
class NotationMenu
{

	/**
	 * add pop-up with names of Greek symbols
	 * @param field the text input field for the pop-up
	 */
	static void addPopupMenuTo (JTextComponent field)
	{
		addPopupMenuTo
		(
			field,
			(text) -> field.setText (field.getText () + text)
		);
	}
	static void addPopupMenuTo (JTextComponent field, TextProcessor p)
	{ field.addMouseListener (MenuManager.getMenu (getMenus (p))); }

	/**
	 * @param using the text processor for menu actions
	 * @return an array of the generated menus
	 */
	public static JMenu[]
		getMenus (TextProcessor using)
	{ return new Case (using).menus (); }

	/**
	 * @param using the text processor for menu actions
	 * @param list the list collecting menus
	 */
	public static void
		getMenus (TextProcessor using, List<JMenu> list)
	{ new Case (using).addTo (list); }

	/**
	 * lists of character data organized by case
	 */
	static class Case
	{

		List<ActionListener> listFor (String text)
		{
			return Character.isUpperCase (text.charAt (0)) ?
					upper: lower;
		}

		List<ActionListener> upper = new ArrayList<ActionListener>();
		List<ActionListener> lower = new ArrayList<ActionListener>();

		JMenu upperMenu () { return MenuManager.getMenuOf (upper, "Upper Case"); }
		JMenu lowerMenu () { return MenuManager.getMenuOf (lower, "Lower Case"); }

		/**
		 * @param thisList the list collecting menus
		 */
		void addTo (List<JMenu> thisList)
		{
			thisList.add (upperMenu ());
			thisList.add (lowerMenu ());
		}

		/**
		 * @return an array of the menu objects
		 */
		JMenu[] menus ()
		{
			return new JMenu[]{ upperMenu (), lowerMenu () };
		}

		Case (TextProcessor p)
		{
			this (GreekSymbols.getEnglishToGreekMap (), p);
		}

		Case (Map<String,String> map, TextProcessor p)
		{
			for (String s : getSymbolList (map.keySet ()))
			{ listFor (s).add (new GreekMenuAction (s, map.get(s), p)); }
		}

	}

	/**
	 * sort the item collection
	 * @param items collection of text items
	 * @return the sorted list
	 */
	static List<String> getSymbolList (Collection<String> items)
	{
		List<String> symbols = new ArrayList<String>();
		symbols.addAll (items);
		symbols.sort (null);
		return symbols;
	}

	/**
	 * Greek / English correlation
	 */
	static class GreekMenuAction implements ActionListener, MenuManager.HotKeyAvailable
	{
		GreekMenuAction (String english, String greek, JTextComponent c)
		{
			this
			(
				english, greek,
				(text) ->
				{
					c.setText (c.getText () + text);
				}
			);
		}

		GreekMenuAction (String english, String greek, TextProcessor p)
		{
			String special = GreekSymbols.getSpecialCaseFor (english);
			char initial = special == null? english.charAt (0): special.charAt (0);
			this.keystroke = KeyStroke.getKeyStroke (Character.toLowerCase (initial));
			this.title = english + " (" + greek + ")";
			this.english = " " + english + " ";
			this.proc = p;
		}

		public String toString () { return title; }
		public KeyStroke getHotKey() { return keystroke; }
		public void actionPerformed (ActionEvent event)
		{ proc.process (english); }
		String title, english;
		KeyStroke keystroke;
		TextProcessor proc;
	}

}
