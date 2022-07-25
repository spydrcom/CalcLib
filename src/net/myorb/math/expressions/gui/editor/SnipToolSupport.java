
package net.myorb.math.expressions.gui.editor;

import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.gui.DisplayEnvironment;
import net.myorb.math.expressions.gui.EnvironmentCore;
import net.myorb.math.expressions.gui.DisplayConsole;

import net.myorb.gui.components.DisplayTablePrimitives;
import net.myorb.gui.components.SimpleScreenIO;

import javax.swing.JTable;
import javax.swing.JComponent;

import java.awt.Component;
import java.io.File;

/**
 * helper class for Snip tool features
 * @author Michael Druckman
 */
public class SnipToolSupport extends SimpleScreenIO
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
		try {
			JTable table = getScriptTable (environment);
			int row = getScriptTable (environment).getSelectedRow ();
			if (row >= 0) return table.getValueAt (row, 0).toString ();
		} catch (Exception e) {}
		return null;
	}


	/**
	 * @param forWidget related Component
	 * @return the name provided by screen request
	 * @throws Exception for errors in the input request
	 */
	public static String requestName (Component forWidget) throws Exception
	{
		return requestTextInput (forWidget, "Name for Tab", "", "NA");
	}


	/**
	 * @param fullName files name with file type extended
	 * @return name with type removed
	 */
	public static String shortNameFor (String fullName)
	{		
		int dot = fullName.lastIndexOf ('.');
		if (dot > 0) return fullName.substring (0, dot);
		return fullName;
	}


	/**
	 * dump text string to console
	 * @param text content to dump
	 */
	public static void dump (String text)
	{
		for (int i = 0; i < text.length (); i++)
		{
			System.out.println (Integer.toHexString (text.charAt (i)));
		}
	}


	/**
	 * refer to the script file specified
	 * @param named the simple file name
	 * @return the file descriptor
	 */
	public static File getScriptFileAccess (String named)
	{
		return new File ("scripts/" + named + ".txt");
	}


}

