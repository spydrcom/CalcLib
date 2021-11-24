
package net.myorb.math.expressions.gui;

import net.myorb.math.expressions.ScriptManager;

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


	public static String[][] objectListTable (Object [] list)
	{
		int i = 0;
		String [] [] objectTable = new String [list.length] [1];
		for (Object o : list) { objectTable [i++] [0] = o.toString (); }
		return objectTable;
	}


	public static void changeListData
	(Object tableAccess, Object [] list)
	{
		Object[][] newData = objectListTable (list);
		DisplayTable.changeData (tableAccess, newData);
	}


	public static String[][] fileListTable (File [] list)
	{
		int i = 0;
		String [] [] fileTable = new String [list.length] [1];
		for (File f : list) { fileTable [i++] [0] = f.getName (); }
		return fileTable;
	}


	public static void changeFileData
	(Object tableAccess, String directoryName)
	{
		Object[][] newData =
			fileListTable (directoryOf (directoryName).listFiles ());
		DisplayTable.changeData (tableAccess, newData);
	}


	/**
	 * produce table of files
	 * @param list the list of files to include
	 * @param header the table column title for the filenames
	 * @return the tabulation display for the file list
	 */
	public static JComponent tabulateFiles (File [] list, String header)
	{
		String [] [] fileTable = fileListTable (list);
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
	 * @param properties the core symbol map
	 * @param menuBar the application menu bar being built
	 * @return the file tabulation display component
	 */
	public static JComponent fileSplit (DisplayConsole.StreamProperties properties, JMenuBar menuBar)
	{
		JComponent script, data;
		JComponent c = DisplayEnvironment.vSplit
				(script = scriptFiles (), data = dataFiles ());
		processActions (properties, script, data, menuBar);

		if (properties != null)
		{
			properties.put (DisplayEnvironment.DataFiles, data);
			properties.put (DisplayEnvironment.ScriptFiles, script);
			properties.put (DisplayEnvironment.FileSplit, c);
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
			DisplayConsole.StreamProperties properties,
			JComponent script, JComponent data,
			JMenuBar menuBar
		)
	{
		DisplayIO.CommandProcessor
			p = EnvironmentCore.getCommandProcessor (properties);

		ActionList dataItems = ToolBar.getDataTableMenuItems (p, script);
		ActionList scriptItems = ToolBar.getScriptTableMenuItems (p, script);

		DisplayTable.getTableInScroll (script).addMouseListener (getMenu (scriptItems));
		DisplayTable.getTableInScroll (data).addMouseListener (getMenu (dataItems));

		if (menuBar != null)
		{
			addToMenuBar (dataItems, "Data", menuBar);
			addToMenuBar (scriptItems, "Scripts", menuBar);
		}

		getScriptManager (properties).connectFileDrop (script);
	}


	public static ScriptManager<?> getScriptManager (DisplayConsole.StreamProperties properties)
	{
		return EnvironmentCore.getExecutionEnvironment (properties)
			.getControl ().getEngine ().getScriptManager ();
	}


	/**
	 * show files in frame
	 * @param properties the core symbol map
	 */
	public static void showFiles (DisplayConsole.StreamProperties properties)
	{
		new DisplayFrame (fileSplit (properties, null), "Files").show ();
	}


	public static void showScriptCache (DisplayConsole.StreamProperties properties)
	{
		Object[] cache = getScriptManager (properties).getScriptNames ().toArray ();
		changeListData (properties.get (DisplayEnvironment.ScriptFiles), cache);
	}


	public static void showScriptDirectory (DisplayConsole.StreamProperties properties)
	{
		changeFileData (properties.get (DisplayEnvironment.ScriptFiles), "Scripts");
	}


	public static void displayActiveScripts (DisplayConsole.StreamProperties properties)
	{
		getScriptManager (properties).displayScriptCache ();
	}


}

