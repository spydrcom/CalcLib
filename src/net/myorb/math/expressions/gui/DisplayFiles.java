
package net.myorb.math.expressions.gui;

import net.myorb.gui.components.DisplayFrame;
import net.myorb.gui.components.DisplayTable;
import net.myorb.gui.components.MenuManager;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

import java.io.*;

/**
 * table component that displays files
 * @author Michael Druckman
 */
public class DisplayFiles extends MenuManager
{


	/**
	 * produce table of files
	 * @param list the list of files to include
	 * @param header the table column title for the filenames
	 * @return the tabulation display for the file list
	 */
	public static JComponent tabulateFiles (File [] list, String header)
	{
		int i = 0;
		String [] [] fileTable = new String [list.length] [1];
		for (File f : list) { fileTable [i++] [0] = f.getName (); }
		return DisplayTable.tabulate (fileTable, new String [] {header});
	}


	/**
	 * construct tabulation of files in system
	 * @param entitled name of directory and column header
	 * @return the tabulation display for the file list
	 */
	public static JComponent tabulationOf (String entitled)
	{ return tabulateFiles (directoryOf (entitled).listFiles (), entitled); }
	public static File directoryOf (String items) { return new File (items.toLowerCase () + "/"); }
	public static JComponent scriptFiles () { return tabulationOf ("Scripts"); }
	public static JComponent dataFiles () { return tabulationOf ("Data"); }


	/**
	 * build the file tabulation display
	 * @param components the core symbol map
	 * @param menuBar the application menu bar being built
	 * @return the file tabulation display component
	 */
	public static JComponent fileSplit (EnvironmentCore.CoreMap components, JMenuBar menuBar)
	{
		JComponent script, data;
		JComponent c = DisplayEnvironment.vSplit
				(script = scriptFiles (), data = dataFiles ());
		processActions (components, script, data, menuBar);

		if (components != null)
		{
			components.put (DisplayEnvironment.DataFiles, data);
			components.put (DisplayEnvironment.ScriptFiles, script);
			components.put (DisplayEnvironment.FileSplit, c);
		}

		return c;
	}


	/**
	 * add menu processing to file tabulation
	 * @param components the core symbol map for the application
	 * @param script the script file tabulation display component
	 * @param data the data file tabulation display component
	 * @param menuBar the application menu bar being built
	 */
	static void processActions
		(
			EnvironmentCore.CoreMap components,
			JComponent script, JComponent data,
			JMenuBar menuBar
		)
	{
		DisplayIO.CommandProcessor
			p = EnvironmentCore.getCommandProcessor (components);

		ActionList dataItems = ToolBar.getDataTableMenuItems (p, script);
		ActionList scriptItems = ToolBar.getScriptTableMenuItems (p, script);

		DisplayTable.getTableInScroll (script).addMouseListener (getMenu (scriptItems));
		DisplayTable.getTableInScroll (data).addMouseListener (getMenu (dataItems));

		if (menuBar != null)
		{
			addToMenuBar (dataItems, "Data", menuBar);
			addToMenuBar (scriptItems, "Scripts", menuBar);
		}
	}


	/**
	 * show files in frame
	 * @param components the core symbol map
	 */
	public static void showFiles (EnvironmentCore.CoreMap components)
	{
		new DisplayFrame (fileSplit (components, null), "Files").show ();
	}


}

