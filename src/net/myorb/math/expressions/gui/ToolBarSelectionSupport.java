
package net.myorb.math.expressions.gui;

import net.myorb.math.Function;
import net.myorb.math.SpaceManager;
import net.myorb.math.expressions.SymbolMap;
import net.myorb.math.expressions.charting.fractals.Fractal;
import net.myorb.math.expressions.gui.DisplayIO.CommandProcessor;
import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.DifferentialEquationsManager;

import net.myorb.gui.components.SimpleScreenIO;
import net.myorb.gui.components.DisplayTable;
import net.myorb.gui.components.MenuManager;
import net.myorb.gui.components.Alerts;

import net.myorb.jxr.JxrScriptChoice;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;

import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * base for dialogs that require selection from user
 * @author Michael Druckman
 */
public class ToolBarSelectionSupport extends ToolBarPrimitives
{
	ToolBarSelectionSupport
	(String command, DisplayIO.CommandProcessor processor, String tableName, Component c)
	{ super (command, processor, c); this.tableName = tableName; }
	protected String tableName;
}


/**
 * identify component intended to use as selection source
 */
class SelectedCommand extends ToolBarSelectionSupport implements ActionListener
{

	SelectedCommand
		(
			String command, String tableName,
			DisplayIO.CommandProcessor processor,
			boolean selectionRequired, Component c
		)
	{
		super (command, processor, tableName, c);
		this.selectionRequired = selectionRequired;
	}
	boolean selectionRequired;

	/**
	 * lookup table by identified table name
	 * @return the table object named
	 */
	JTable getTable ()
	{
		Map<String,Object> map = processor.getMap ();
		JScrollPane pane = (JScrollPane)map.get (tableName);
		if (pane != null) return DisplayTable.getTableInScroll (pane);
		else return null;
	}
	
	/**
	 * get value from table
	 * @param t the table component
	 * @param col the column of the table
	 * @param required TRUE => selection is required
	 * @return value found or NULL if none
	 */
	String getValueAt (JTable t, int col, boolean required)
	{
		int row = t==null?
				-1: t.getSelectedRow ();
		if (row < 0) if (!required) return "";
		else { terminate ("Table selection required"); }
		return t.getValueAt (row, col).toString ();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String parameter = getValueAt (getTable (), 0, false);
		if (parameter != null) processor.execute (command.toUpperCase() + parameter);
	}
	
}


/**
 * common processing for function selection in GUI
 */
class FunctionSelection extends MultipleSelectionCommand implements ActionListener
{

	public FunctionSelection (CommandProcessor processor, Component c)
	{
		super ("Function-Transform", "FunctionTable", processor, true, c);
		map = EnvironmentCore.getSymbolMap (processor);
	}

	/**
	 * @return user supplied name for new function
	 */
	Object nameForFunction ()
	{
		return getSimpleTextResponse ("Specify name", "New function name");
	}

	/**
	 * get name of parameter of function
	 * @param function the function being queried
	 * @return name of the parameter
	 * @param <T> data type
	 */
	<T> String getParameter (SymbolMap.Named function)
	{
		Subroutine<T> s;
		if ((s = Subroutine.cast (function)) != null)
		{
			List<String> p = s.getParameterNames ();
			if (p.size() != 1) terminate ("Function must have one parameter");
			return p.get (0).toString ();
		} else return "x";
	}

	/**
	 * @return selection(s) within function table supplied by user
	 */
	String[] getSelectedFunctionNames ()
	{
		String[] selections;
		checkForCancel (selections = getValuesAt (getTable (), 0));;
		return selections;
	}

	/**
	 * @return symbol identified by user within functions table
	 */
	SymbolMap.Named getSelectedFunction ()
	{
		String selection;
		checkForCancel (selection = getValueAt (getTable (), 0, true));
		return lookup (selection);
	}
	SymbolMap.Named lookup (String selection)
	{ return map.lookup (selection); }
	SymbolMap map;

}


/**
 * limit to single parameter user defined functions
 * @param <T> type on which operations are to be executed
 */
class LimitedFunctionSelection<T> extends FunctionSelection
{

	public LimitedFunctionSelection
	(CommandProcessor processor, Component c)
	{ super (processor, c); }

	/**
	 * check for error in lookup or symbol type
	 * @param function the named symbol found in lookup
	 * @param lookupMessage message to be used for lookup error
	 * @param castMessage message to be used for casting error
	 * @return the subroutine object from the lookup
	 */
	public Subroutine<T> checkSubroutine
	(SymbolMap.Named function, String lookupMessage, String castMessage)
	{
		if (function == null)
		{
			if (lookupMessage == null) return null;
			throw new RuntimeException (lookupMessage);
		}
		Subroutine<T> s = Subroutine.cast (function);
		if (s == null) throw new RuntimeException (castMessage);
		return s;
	}

	/**
	 * check parameter count on subroutine symbol
	 * @param subroutine the subroutine to be checked
	 * @param errorMessage the message to put in exception if error found
	 * @return the parameter names list
	 */
	public List<String> checkParameter
	(Subroutine<T> subroutine, String errorMessage)
	{
		List<String> parameters = subroutine.getParameterNames ();
		if (parameters.size() != 1) throw new RuntimeException (errorMessage);
		return parameters;
	}

	/**
	 * get selected function and verify properties
	 */
	public void getLimitedSelectedFunction ()
	{
		SymbolMap.Named function;
		checkForCancel (function = getSelectedFunction ());
		subroutine = checkSubroutine (function, "Function lookup failed", "User defined function references only");
		parameterName = checkParameter (subroutine, "Single variable functions only").get (0);
		profile = (functionName = function.getName ()) + " (" + parameterName + ")";
	}
	protected String functionName, parameterName, profile;

	/**
	 * @return subroutine wrapped as function(x)
	 */
	public Function<T> getIdentifiedFunction ()
	{ return subroutine.toSimpleFunction (); }
	protected Subroutine<T> subroutine;

	/**
	 * locate derivative definition for selected function
	 */
	public void getLimitedSelectedDerivative ()
	{
		derivative = checkSubroutine
			(
				lookup (functionName + "'"), "Derivative for function must be available",
				"Derivative found is not user defined function"
			);
		checkParameter (derivative, "Derivative profile is not consistant");
	}
	public void getLimitedSelectedSecondDerivative ()
	{
		secondDerivative = checkSubroutine
			(
				lookup (functionName + "''"), null,
				"Second derivative found is not user defined function"
			);
		checkParameter (derivative, "Second derivative profile is not consistant");
	}

	/**
	 * @return derivative wrapped as function(x)
	 */
	public Function<T> getIdentifiedDerivative ()
	{ return derivative.toSimpleFunction (); }
	protected Subroutine<T> derivative;

	public Function<T> getIdentifiedSecondDerivative ()
	{ return secondDerivative==null? null: secondDerivative.toSimpleFunction (); }
	protected Subroutine<T> secondDerivative;

}


/**
 * provide for display of scripts in GUI
 */
class PrintCommand extends SelectedCommand implements MenuManager.HotKeyAvailable
{

	PrintCommand
		(
			DisplayIO.CommandProcessor processor, Component c
		)
	{
		super ("SCRIPTPRINT ", "ScriptFiles", processor, true, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_D, ActionEvent.ALT_MASK); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Display"; }

}


/**
 * maximum multiple iterations of a command
 */
class IterateCommand extends SelectedCommand implements MenuManager.HotKeyAvailable
{

	IterateCommand
		(
			DisplayIO.CommandProcessor processor, Component c
		)
	{
		super ("ITERATE ", "ScriptFiles", processor, true, c);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object maxIterations = getSimpleTextResponse
			("Specify Maximum Iteration Count", "Script Iterations");
		checkForCancel (maxIterations);

		String file = getValueAt (getTable (), 0, false);
		if (file != null) processor.execute (command + maxIterations + " " + file);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_I, ActionEvent.ALT_MASK); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Iterate"; }

}


/**
 * list script files in table
 */
class ScriptFilesCommand extends SelectedCommand implements MenuManager.HotKeyAvailable
{

	ScriptFilesCommand
		(
			DisplayIO.CommandProcessor processor, Component c
		)
	{
		super ("SHOWFILES ", "ScriptFiles", processor, false, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_F, ActionEvent.ALT_MASK); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Filelist"; }

}


/**
 * list active scripts in table
 */
class ActiveFilesCommand extends SelectedCommand implements MenuManager.HotKeyAvailable
{

	ActiveFilesCommand
		(
			DisplayIO.CommandProcessor processor, Component c
		)
	{
		super ("SHOWACTIVE ", "ScriptFiles", processor, false, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_A, ActionEvent.ALT_MASK); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Active"; }

}


/**
 * list cached scripts in table
 */
class CachedFilesCommand extends SelectedCommand implements MenuManager.HotKeyAvailable
{

	CachedFilesCommand
		(
			DisplayIO.CommandProcessor processor, Component c
		)
	{
		super ("SHOWCACHE ", "ScriptFiles", processor, false, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_C, ActionEvent.ALT_MASK); }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Cached"; }

}


/**
 * run script in background thread
 */
class ForkCommand extends SelectedCommand implements MenuManager.MnemonicAvailable
{

	ForkCommand
		(
			DisplayIO.CommandProcessor processor, Component c
		)
	{
		super ("BACKGROUND ", "ScriptFiles", processor, true, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'F'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Fork"; }

}


/**
 * show tracking list of contour plots
 */
class TrackingList implements ActionListener
{

	private void disableButtons ()
	{
		for (JComponent c : components) c.setEnabled (false);
	}

	public void checkType ()
	{
		if (processor.getEnvironment ().getSpaceManager ().getDataType () != dataType)
		{
			disableButtons ();
			Alerts.warn (null, "These charts only available when using " + dataType + " engine");
			throw new RuntimeException ("Engine type not consistent");
		}
	}

	public void addItem (JComponent component) { components.add (component); }
	private List<JComponent> components = new ArrayList<JComponent>();

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{ checkType (); net.myorb.math.expressions.charting.Tracking.getInstance (dataType == COMPLEX).show (); }
	public static final SpaceManager.DataType COMPLEX = SpaceManager.DataType.Complex;

	TrackingList (CommandProcessor processor, SpaceManager.DataType dataType)
	{ this.dataType = dataType; this.processor = processor; }
	private DisplayIO.CommandProcessor processor;
	private SpaceManager.DataType dataType;

}

/**
 * use JXR script to select a chart system primitive solution
 */
class SystemSelection
{

	/**
	 * @return the action listener for the Charts menu Selection item
	 */
	public static ActionListener getSystemSelectionAction ()
	{
		return (e) -> { JxrScriptChoice.runScript (SCRIPT); };
	}
	public static String SCRIPT = "cfg/gui/ChartLibChoice.xml";

	/**
	 * @return the action listener for the Charts menu Palate action
	 */
	public static ActionListener getPalateToolAction ()
	{
		return (e) -> { PalateManager.showPalateTool (); };
	}

}

/**
 * command for display of fractal plots
 */
class FractalDisplay extends SelectedCommand
{

	public FractalDisplay
		(
			String command,
			Map<String,Fractal> map,
			TrackingList buttonTrackingList,
			CommandProcessor processor, Component c
		)
	{
		super (command, "SymbolTable", processor, false, c);
		this.choices = map.keySet ().toArray (new String[1]);
		this.buttonTrackingList = buttonTrackingList;
		Arrays.sort (choices); this.map = map;
	}
	TrackingList buttonTrackingList;
	Map<String,Fractal> map;
	String[] choices;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		buttonTrackingList.checkType ();

		Object choice = null, response = null;
		String parameter = getValueAt (getTable (), 0, false);

		if (parameter == null || parameter.length() == 0)
		{
			response = chooseFromList
				("Choose from", "Specify Fractal", choices);
			choice = map.get (checkForCancel (response));
		}

		response = checkForCancel (chooseFromList
		(
			"Choose resolution", "Specify Resolution",
			new String[]{"200", "400", "500", "600", "700"}
		));

		if (choice == null)
		{ new CommandThread (processor, command + parameter + " " + response); }
		else { plotThread ((Fractal) choice, Integer.parseInt (response.toString ())); }
	}

	/**
	 * run in background
	 * @param f fractal to be displayed
	 * @param resolution the specified resolution of the plot
	 */
	void plotThread (final Fractal f, final int resolution)
	{
		SimpleScreenIO.startBackgroundTask
		( () -> f.plot (resolution, 5) );
	}

}

/**
 * execute selected script, multiple possible names, same action
 */
class ExecuteCommand extends SelectedCommand
{

	public ExecuteCommand
		(
			String command, String display, String tableName,
			CommandProcessor processor, boolean selectionRequired, Component c
		)
	{
		super(command, tableName, processor, selectionRequired, c);
		this.display = display;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String parameter = getValueAt (getTable (), 0, false);
		processor.execute (command + " " + checkForCancel (parameter));
		refresh ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return display; }
	String display;

}


/**
 * execute command has assigned hot key
 */
class FastExecuteCommand extends ExecuteCommand  implements MenuManager.HotKeyAvailable
{

	public FastExecuteCommand
		(
			String command, String tableName, CommandProcessor processor,
			boolean selectionRequired, Component c
		)
	{
		super (command, "Execute", tableName, processor, selectionRequired, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_X, ActionEvent.ALT_MASK); }

}


/**
 * execute script (CTL-B hot key)
 * generating symbols in background symbol table.
 * symbols are recognized but not displayed as part of environment GUI.
 * symbol promotion copies background symbol to front (display) table.
 */
class BackgroundSymbolsCommand extends ExecuteCommand  implements MenuManager.HotKeyAvailable
{

	public BackgroundSymbolsCommand
		(
			String command, String tableName, CommandProcessor processor,
			boolean selectionRequired, Component c
		)
	{
		super (command, "Background", tableName, processor, selectionRequired, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.HotKeyAvailable#getHotKey()
	 */
	public KeyStroke getHotKey () { return KeyStroke.getKeyStroke (KeyEvent.VK_B, ActionEvent.ALT_MASK); }

}


/**
 * create new script file
 */
class CreateCommand extends SelectedCommand implements TextEditor.TextProcessor, MenuManager.MnemonicAvailable
{

	public CreateCommand
	(String command, String tableName, CommandProcessor processor, boolean selectionRequired, Component c)
	{ super(command, tableName, processor, selectionRequired, c); }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object title = getSimpleTextResponse
			("Specify Name For Script", "New Script File");
		TextEditor.create (checkForCancel (title).toString (), this);
		refresh ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.TextEditor.TextProcessor#process(java.lang.String)
	 */
	public void process (String text)
	{
		for (String line : TextEditor.parseTextLines (text))
		{
			processor.execute (line);
		}
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'N'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "New Script"; }

}


/**
 * provide for single selection function commands
 */
class CommonFunctionCommand extends SelectedCommand
{

	public CommonFunctionCommand
	(String command, DisplayIO.CommandProcessor processor, Component c)
	{ this (command, processor, true, c); }

	public CommonFunctionCommand
	(String command, DisplayIO.CommandProcessor processor, boolean required, Component c)
	{ super (command, "FunctionTable", processor, required, c); }

	public CommonFunctionCommand
	(String command, String table, DisplayIO.CommandProcessor processor, boolean required, Component c)
	{ super (command, table, processor, required, c); }

	/**
	 * check for selection
	 */
	public void identifyFunction ()
	{
		if ((function = getValueAt (getTable (), 0, true)) == null)
		{
			if (selectionRequired)
				terminate ("No Function Selected");
			function = "";
		}
	}
	protected String function;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		identifyFunction (); initialization ();
		processor.execute (command + function);
		refresh ();
	}

	/**
	 * execute setup steps
	 */
	public void initialization () {}

}


/**
 * issue TDES (Test Differential Equation Solution) command for function
 */
class RunDiffEQSolutionTest extends CommonFunctionCommand
	implements DifferentialEquationsManager.FollowUp, MenuManager.MnemonicAvailable
{

	public RunDiffEQSolutionTest
	(String command, DisplayIO.CommandProcessor processor, Component c)
	{ super (command, processor, c); setCommandText ("TDES"); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.CommonFunctionCommand#initialization()
	 */
	public void initialization ()
	{
		try
		{ processor.getEnvironment ().getDifferentialEquationsManager ().connect (function, this); }
		catch (Exception e) {}
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.DifferentialEquationsManager.FollowUp#runFollowUp(java.util.Map)
	 */
	@Override public void runFollowUp (Map <String, String> usingParameters)
	{
		new FunctionPlot (processor, parent).plotSpecifically (function, usingParameters);
	}

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	@Override public char getMnemonic () { return 'Q'; }

}


/**
 * common publisher base for DiffEQ PREP GUI
 */
class CommonDiffEQAnalysisPrep extends CommonFunctionCommand
	implements TestPrepForm.Publisher, MenuManager.MnemonicAvailable
{

	public CommonDiffEQAnalysisPrep
	(String table, DisplayIO.CommandProcessor processor, Component c)
	{ super ("Prepare DEQ Test", table, processor, true, c); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.TestPrepForm.Publisher#publish(java.lang.String)
	 */
	@Override public void publish (String descriptor) { processor.execute (descriptor); }

	/* (non-Javadoc)
	 * @see net.myorb.gui.components.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	@Override public char getMnemonic () { return 'P'; }

}


/**
 * issue PREPARE command for function
 */
class PrepareDiffEQAnalysisCommand extends CommonDiffEQAnalysisPrep
{

	public PrepareDiffEQAnalysisCommand
	(String command, DisplayIO.CommandProcessor processor, Component c)
	{ super ("FunctionTable", processor, c); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{ identifyFunction (); new TestPrepForm (TITLE, function, this); }
	static final String TITLE = "Differential Equation Solution Test For Function";

}


/**
 * treat item as polynomial coefficient vector to be tested as DiffEQ solution
 */
class PrepPolynomialDEQTest extends CommonDiffEQAnalysisPrep
{

	public PrepPolynomialDEQTest
	(DisplayIO.CommandProcessor processor, Component parent)
	{ super ("SymbolTable", processor, parent); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		String poly = getValueAt (getTable (), 0, false);
		if (poly.isEmpty ()) terminate ("A polynomial coefficients vector must be selected");
		new TestPrepForm (TITLE, poly, this).prepPoly ();
	}
	static final String TITLE = "Differential Equation Solution Test For Polynomial";

}


/**
 * enable Expression Tree use for this function
 */
class ExpressCommand extends CommonFunctionCommand implements MenuManager.MnemonicAvailable
{

	public ExpressCommand
	(String command, DisplayIO.CommandProcessor processor, Component c)
	{
		super (command, processor, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'X'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Express"; }

}


/**
 * Save Expression As JSON
 */
class ExpressSaveCommand extends CommonFunctionCommand implements MenuManager.MnemonicAvailable
{

	public ExpressSaveCommand
	(String command, DisplayIO.CommandProcessor processor, Component c)
	{
		super (command, processor, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'J'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Save Expression As JSON"; }

}


/**
 * Load Expression From JSON
 */
class ExpressLoadCommand extends CommonFunctionCommand implements MenuManager.MnemonicAvailable
{

	public ExpressLoadCommand
	(String command, DisplayIO.CommandProcessor processor, Component c)
	{
		super (command, processor, false, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		File[] files = new File ("expressions/").listFiles ();
		Object function = chooseFromList ("Select File Name", "Function(s) to Restore", files);

		if (function != null)
		{
			String name = ((File) function).getName ();
			if ( ! name.toLowerCase ().endsWith ("zip") )
			{ name = name.substring (0, name.indexOf ('.')); }
			processor.execute (command + name);
			refresh ();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Load Expression From JSON"; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'L'; }

}


/**
 * encode spline function in Java library
 */
class EncodeCommand extends SelectedCommand implements MenuManager.MnemonicAvailable
{

	public EncodeCommand
	(String command, String tableName, DisplayIO.CommandProcessor processor, Component c)
	{
		super (command, tableName, processor, true, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		String function = getValueAt (getTable (), 0, true);
		if (function == null) terminate ("No Function Selected");
		processor.execute (command + function);
		refresh ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'E'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Encode"; }

}


/**
 * import data set as specified by selection
 */
class ImportCommand extends SelectedCommand implements MenuManager.MnemonicAvailable
{

	public ImportCommand
	(String command, String tableName, DisplayIO.CommandProcessor processor, Component c)
	{
		super (command, tableName, processor, true, c);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String parameter;
		if ((parameter = getValueAt (getTable (), 0, true)) == null) terminate ("Nothing Selected");
		String name = parameter.substring (0, parameter.lastIndexOf ("."));
		processor.execute (command + name + " " + parameter);
		refresh ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'I'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Import"; }

}


/**
 * export data to tab delimited file
 */
class ExportCommand extends SelectedCommand implements MenuManager.MnemonicAvailable
{

	public ExportCommand (DisplayIO.CommandProcessor processor, Component c) { this ("EXPORT ", processor, c); }
	public ExportCommand (String command, DisplayIO.CommandProcessor processor, Component c)
	{
		super (command, "SymbolTable", processor, true, c);
	}
	Component parent;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed (ActionEvent e)
	{
		String symbol;
		if ((symbol = getValueAt (getTable (), 0, true)) == null) terminate ("No Symbol Selected");
		processor.execute (command + symbol + " " + symbol + ".TDF");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'E'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Export"; }

}


/**
 * execute sequence of commands
 */
class SelectedSimpleAssignmentCommand extends SelectedCommand
{

	public SelectedSimpleAssignmentCommand
		(
			String[] commands, String[] simpleNames,
			String tableName, DisplayIO.CommandProcessor processor,
			Component c
		)
	{
		super("", tableName, processor, true, c);
		this.commands = commands; this.simpleNames = simpleNames;
	}
	String[] commands, simpleNames;

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String parameter = getValueAt (getTable (), 0, selectionRequired);

		if (parameter != null)
		{
			for (int i = 0; i < commands.length; i++)
			{
				processor.execute (parameter + "_" + simpleNames[i] + " = " + commands[i] + "(" + parameter + ")");
			}
			refresh ();
		}
	}

}


/**
 * rename a symbol
 */
class RenameCommand extends SelectedCommand  implements MenuManager.MnemonicAvailable
{

	public RenameCommand (CommandProcessor processor, Component c) { this ("RENAME", "SymbolTable", processor, true, c); }
	public RenameCommand (String command, String tableName, CommandProcessor processor, boolean selectionRequired, Component c)
	{ super(command, tableName, processor, selectionRequired, c); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String symbolName =
			getValueAt (getTable (), 0, true);
		if (symbolName == null) terminate ("No Symbol Selected");

		SymbolMap map =
			EnvironmentCore.getSymbolMap (processor);
		SymbolMap.Named symbol = map.lookup (symbolName);
		if (symbol == null) terminate ("Symbol Not Found");

		if (!(symbol instanceof SymbolMap.VariableLookup))
		{ terminate ("Rename not allowed"); }

		Object newName = getResponse ();
		if (newName == null) terminate ("Rename Canceled");

		map.remove (symbolName);
		((SymbolMap.VariableLookup)symbol).rename (newName.toString ());
		map.add (symbol);
		refresh ();
	}

	/**
	 * @return user response to GUI query providing new name for symbol
	 */
	Object getResponse ()
	{
		return getSimpleTextResponse ("Specify New Name", "Rename Symbol");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'R'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Rename"; }

}


/**
 * common action providing command execution on selected table item
 */
class SelectedAssignmentCommand extends SelectedCommand
{

	public SelectedAssignmentCommand
		(
			String command, String tableName,
			DisplayIO.CommandProcessor processor,
			boolean selectionRequired, Component c
		)
	{
		super(command, tableName, processor, selectionRequired, c);
	}
	
	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String parameter = getValueAt (getTable (), 0, selectionRequired);
		if (parameter != null) processor.execute (parameter + "_" + command + " = " + command + "(" + parameter + ")");
	}

}


/**
 * common processing of multiple selections within tables
 */
class MultipleSelectionCommand extends SelectedCommand
{

	public MultipleSelectionCommand
		(
			String command, String tableName,
			DisplayIO.CommandProcessor processor,
			boolean selectionRequired, Component c
		)
	{
		super(command, tableName, processor, selectionRequired, c);
	}

	/**
	 * get the row numbers of selected rows
	 * @param t the table object to get row selections from
	 * @return an array of the row numbers
	 */
	int[] getRows (JTable t)
	{
		return t==null? null: t.getSelectedRows ();
	}

	/**
	 * get the values of the selected rows
	 * @param t the table object to get row selections from
	 * @param col column number to read of each selected row
	 * @return the text of each row|col selected cell
	 */
	String[] getValuesAt (JTable t, int col)
	{
		int[] rows = getRows (t);
		if (rows == null || rows.length == 0) return null;
		String[] values = new String[rows.length];

		for (int r = 0; r < rows.length; r++)
		{ values[r] = t.getValueAt (rows[r], col).toString (); }
		return values;
	}

}


/**
 * edit the text of a script
 */
class EditCommand extends CreateCommand
{

	public EditCommand(String command, String tableName, CommandProcessor processor, boolean selectionRequired, Component c)
	{
		super(command, tableName, processor, selectionRequired, c);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String parameter = getValueAt (getTable (), 0, false);
		if (parameter != null) TextEditor.edit (parameter, this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'E'; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Edit Script"; }

}


/**
 * drop an item from a symbol table
 */
class DropCommand extends MultipleSelectionCommand implements MenuManager.MnemonicAvailable
{

	public DropCommand(String tableName, CommandProcessor processor, Component c)
	{ super ("DROP", tableName, processor, true, c); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'D'; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String[] selections;
		if ((selections = getValuesAt (getTable (), 0)) == null) return;
		SymbolMap map = EnvironmentCore.getSymbolMap (processor);
		for (String selected : selections)
		{ map.remove (selected); }
		refresh ();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return "Drop"; }

}
class DropSymbol extends DropCommand
{
	public DropSymbol (CommandProcessor processor, Component c) { super ("SymbolTable", processor, c); }
}
class DropFunction extends DropCommand
{
	public DropFunction (CommandProcessor processor, Component c) { super ("FunctionTable", processor, c); }
}


/**
 * multiple selection processing for actions that require specifically 2 selected parameters
 */
class DualSelectedAssignmentCommand extends MultipleSelectionCommand
{

	public DualSelectedAssignmentCommand
		(
			String command, String tableName,
			DisplayIO.CommandProcessor processor,
			Component c
		)
	{
		super(command, tableName, processor, true, c);
		separator = "_" + command + "_";
	}
	String separator;
	
	/**
	 * format parameters with separator
	 * @param left the left side selected parameter
	 * @param right the right side selected parameter
	 * @return formatted text
	 */
	String formatParameters (String left, String right)
	{
		return left  + separator + right;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MultipleSelectionCommand#getValuesAt(javax.swing.JTable, int)
	 */
	String[] getValuesAt (JTable t, int col)
	{
		String[]
		values = super.getValuesAt (t, col);
		if (values == null || values.length != 2)
		{ terminate ("Two symbols must be selected"); return null; }
		return values;
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SelectedCommand#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String parameters[], resultSymbol, formattedParameters;
		if ((parameters = getValuesAt (getTable (), 0)) == null) return;
		resultSymbol = formatParameters (parameters[0], parameters[1]);
		formattedParameters = "(" + parameters[0] + "," + parameters[1] + ")";
		processor.execute (resultSymbol + " = " + command + formattedParameters);
		refresh ();
	}

}


/**
 * get selected function from environment table
 */
class SimpleFunctionSelection extends FunctionSelection
{

	public SimpleFunctionSelection
	(String command, DisplayIO.CommandProcessor processor, Component parent)
	{ super (processor, parent); this.command = command; }

	public String getFunctionName ()
	{
		SymbolMap.Named symbol = getSelectedFunction ();
		return symbol.getName ();
	}
	public String toString () { return command; }

}


/**
 * alter domain of function to [-1,1]
 */
class StandardizeConstraints extends SimpleFunctionSelection implements MenuManager.MnemonicAvailable
{
	StandardizeConstraints (DisplayIO.CommandProcessor processor, Component parent)
	{ super ("Standardize Domain", processor, parent); }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String functionName = getFunctionName ();
		Object newName = checkForCancel (getSimpleTextResponse ("New Name", "Name For Standardized Version of Function", functionName+"STD"));
		processor.execute ("STDDOMAIN " + functionName + " " + newName);
		refresh ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'Z'; }
}


/**
 * set domain constraints on funtion
 */
class SetConstraints extends SimpleFunctionSelection implements MenuManager.MnemonicAvailable, SequenceForm.Publisher
{

	SetConstraints (DisplayIO.CommandProcessor processor, Component parent)
	{ super ("Set Constraints", processor, parent); }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		SequenceForm.requestConstraintsForFunction (this);
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.SequenceForm.Publisher#publish(java.util.List)
	 */
	public void publish (SequenceForm.TextItemList bounds)
	{
		String multiplier;
		String formattedBounds = " ( " + bounds.get (0) + ", " + bounds.get (1) + " )";
		if (!(multiplier = bounds.get (2)).equals ("1")) formattedBounds += " * " + multiplier;
		processor.execute ("SETDOMAIN " + getFunctionName () + formattedBounds);
		refresh ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'C'; }

}


/**
 * apply Discreet Cosine Transform to function
 */
class DCT extends SimpleFunctionSelection implements MenuManager.MnemonicAvailable
{

	DCT (DisplayIO.CommandProcessor processor, Component parent)
	{ super ("Cosine Transform", processor, parent); }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String functionName = getFunctionName ();
		Object samples = checkForCancel (getSimpleTextResponse ("Sample Count", "Samples for Transform", "40"));
		Object newName = checkForCancel (getSimpleTextResponse ("New Name", "Name For DCT Function", functionName+"DCT"));
		processor.execute ("DCT " + samples + " " + functionName + " " + newName);
		refresh ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.gui.MenuManager.MnemonicAvailable#getMnemonic()
	 */
	public char getMnemonic () { return 'T'; }

}

