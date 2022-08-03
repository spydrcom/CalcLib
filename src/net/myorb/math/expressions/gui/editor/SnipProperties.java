
package net.myorb.math.expressions.gui.editor;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.gui.editor.CalcLibSnipToolEditor;

import net.myorb.math.expressions.gui.DisplayEnvironment;
import net.myorb.math.expressions.gui.EnvironmentCore;
import net.myorb.math.expressions.gui.DisplayConsole;

import net.myorb.gui.components.DisplayTablePrimitives;
import net.myorb.gui.components.SimpleScreenIO;

import net.myorb.gui.editor.SnipToolScanner;
import net.myorb.gui.editor.SnipToolPropertyAccess;

import net.myorb.gui.editor.model.SnipToolDocument;
import net.myorb.gui.editor.model.SnipToolContext;
import net.myorb.gui.editor.model.SnipToolKit;

import javax.swing.JComponent;
import javax.swing.JTable;

import java.util.Collection;

/**
 * CalcLib properties for Snip tool features
 * @author Michael Druckman
 */
public class SnipProperties implements SnipToolPropertyAccess
{



	static int FONT_SIZE = 12; static String FONT_FAMILY = "Courier";

	static final boolean USE_RAW_TEXT_EDITOR = false;


	/**
	 * JXR source for Snip Tool frame menu bar
	 */
	public static final String CONFIGURATION_PATH = "cfg/gui/SnipToolMenu.xml";


	/**
	 * @param environment access to CalcLib data structures
	 */
	public SnipProperties (Environment<?> environment)
	{
		this.environment = environment;
	}
	protected Environment<?> environment;


	/**
	 * @return a collection of the names of commands
	 */
	public Collection<String> getCommands ()
	{
		return environment.getCommandDictionary ().keySet ();
	}


	/**
	 * @return a collection of the names of symbols
	 */
	public Collection<String> getSymbols ()
	{
		return environment.getSymbolMap ().keySet ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#getDisplay()
	 */
	public Object getDisplay ()
	{
		return CalcLibProperties.getDisplay (environment);
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#getSelectedFileName()
	 */
	public String getSelectedFileName ()
	{
		return CalcLibProperties.getSelectedScript (environment);
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#execute(java.lang.String)
	 */
	public void execute (String source)
	{
		environment.getControl ().execute (source);
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#getConfigurationPath()
	 */
	public String getConfigurationPath ()
	{
		return CONFIGURATION_PATH;
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#getDirectoryName()
	 */
	public String getDirectoryName ()
	{
		return "scripts/";
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#newEditor()
	 */
	public SimpleScreenIO.SnipEditor newEditor ()
	{
		if (USE_RAW_TEXT_EDITOR)
		{ return new SimpleScreenIO.SnipEditor (); }
		return new CalcLibSnipToolEditor (this);
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#newDocument()
	 */
	public SnipToolDocument newDocument () { return new SnipToolDocument (); }


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#newKit()
	 */
	public SnipToolKit newKit () { return new SnipToolKit (this); }


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#newContext()
	 */
	public SnipToolContext newContext ()
	{
		if (context == null)
		{ context = new SnipToolContext (this); }
		return context;
	}
	protected SnipToolContext context = null;


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#getScanner()
	 */
	public SnipToolScanner getScanner ()
	{
		return scanner = new CalcLibSnipScanner (this);
	}
	protected CalcLibSnipScanner scanner;


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#getDefaultStyleCode()
	 */
	public int getDefaultStyleCode ()
	{
		return scanner.getDefaultStyleCode ();
	}


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#getFontSize()
	 */
	public int getFontSize () { return FONT_SIZE; }


	/* (non-Javadoc)
	 * @see net.myorb.gui.editor.SnipToolPropertyAccess#getFontFamily()
	 */
	public String getFontFamily () { return FONT_FAMILY; }


}


/**
 * translation of CalcLib environment to SnipTool properties
 */
class CalcLibProperties
{


	/**
	 * get access to a display object
	 * @param called name of display required
	 * @param using the environment access object
	 * @return the object found
	 */
	public static Object getDisplay
		(String called, Environment<?> using)
	{
		DisplayConsole.StreamProperties
			guiMap = using.getControl ().getGuiMap ();
		return guiMap.get (called);
	}


	/**
	 * locate source environment component
	 * @param environment access to display components
	 * @return the text component with source of text to edit
	 */
	public static Object getDisplay (Environment<?> environment)
	{
		return getDisplay (EnvironmentCore.CoreDisplayComponent, environment);
	}


	/**
	 * get the script file list
	 * @param environment access to display components
	 * @return the table component holding script list
	 */
	public static JTable getScriptTable (Environment<?> environment)
	{
		JComponent c = (JComponent) getDisplay
				(DisplayEnvironment.ScriptFiles, environment);
		return DisplayTablePrimitives.getTableInScroll (c);
	}


	/**
	 * get the script file name from table
	 * @param environment access to display components
	 * @return NULL if not selected, otherwise file name
	 */
	public static String getSelectedScript (Environment<?> environment)
	{
		try { return getSelectedScript (getScriptTable (environment)); }
		catch (Exception e) {}
		return null;
	}
	public static String getSelectedScript (JTable table)
	{
		int row = table.getSelectedRow ();
		if (row >= 0) return table.getValueAt (row, 0).toString ();
		return null;
	}


}

