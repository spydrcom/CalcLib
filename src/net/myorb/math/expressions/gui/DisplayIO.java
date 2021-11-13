
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.GreekSymbols;
import net.myorb.math.expressions.EvaluationControlI;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.gui.components.DisplayFrame;
import net.myorb.gui.components.MenuManager;
import net.myorb.gui.components.GuiToolkit;

import javax.swing.KeyStroke;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JMenu;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;

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
	 * produce a menu bar for the application
	 * @param handler the application command processor
	 * @return the application menu bar
	 */
	public static JMenuBar getMenuBar (CommandHandler handler)
	{
		Component c = GuiToolkit.getDesktop ();
		//return ToolBarMenu.getMasterMenuBar (handler);
		return ToolBarGenericMenu.getMasterMenuBar (handler, c);
	}


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
		Dimension PANEL_SIZE = new Dimension
			(size + DisplayIO.MARGIN, size + DisplayIO.MARGIN);
		panel.setPreferredSize (PANEL_SIZE);
	}
	JPanel panel;

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
	implements ActionListener, DisplayIO.CommandProcessor, TextEditor.TextProcessor
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
		PrintStream out = environment.getOutStream ();
		out.println (command); control.execute (command);
		out.println (); out.flush ();
	}

	/**
	 * @return access to command execution processor
	 */
	public TextEditor.TextProcessor getExecutionProcessor ()
	{
		return new TextEditor.TextProcessor ()
		{
			@Override public void process (String text)
			{
				execute (text);
			}
		};
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
	CommandHandler (JTextField textField)
	{
		this.commandLineInput = textField; this.coreMap = new EnvironmentCore.CoreMap ();
		this.coreMap.put (EnvironmentCore.CoreCommandProcessor, this);
	}
	JTextField commandLineInput;

}


/**
 * Greek notations menu
 */
class NotationMenu
{

	/**
	 * add pop-up with names of Greek symbols
	 * @param field the text input field for the popup
	 */
	static void addPopupMenuTo (JTextField field)
	{
		Map<String,String> map = GreekSymbols.getEnglishToGreekMap ();
		field.addMouseListener (MenuManager.getMenu (menus (map, field)));
	}

	static JMenu[] menus (Map<String,String> map, JTextField t)
	{
		List<String> symbols = getSymbolList (map.keySet ());
		List<ActionListener> menuActionsU = new ArrayList<ActionListener>();
		List<ActionListener> menuActionsL = new ArrayList<ActionListener>();
		List<ActionListener> menuActions = null;

		for (String s : symbols)
		{
			menuActions = Character.isUpperCase (s.charAt (0))? menuActionsU: menuActionsL;
			menuActions.add (new GreekMenuAction (s, map.get(s), t));
		}

		return new JMenu[]
		{
			MenuManager.getMenuOf (menuActionsU, "Upper Case"),
			MenuManager.getMenuOf (menuActionsL, "Lower Case")
		};
	}

	static List<String> getSymbolList (Collection<String> items)
	{
		List<String> symbols = new ArrayList<String>();
		symbols.addAll (items);
		symbols.sort (null);
		return symbols;
	}

	static class GreekMenuAction implements ActionListener, MenuManager.HotKeyAvailable
	{
		GreekMenuAction (String english, String greek, JTextField t)
		{
			String special = GreekSymbols.getSpecialCaseFor (english);
			char initial = special == null? english.charAt (0): special.charAt (0);
			this.keystroke = KeyStroke.getKeyStroke (Character.toLowerCase (initial));
			this.title = english + " (" + greek + ")";
			this.english = " " + english + " ";
			this.t = t;
		}
		public String toString () { return title; }
		public KeyStroke getHotKey() { return keystroke; }
		public void actionPerformed (ActionEvent event)
		{ t.setText (t.getText () + english); }
		String title, english;
		KeyStroke keystroke;
		JTextField t;
	}

}
