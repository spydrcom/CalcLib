
package net.myorb.math.expressions.gui.query;

import net.myorb.gui.components.RegExTool;

import javax.swing.JTable;

/**
 * GUI component to search entries of tables
 * @author Michael Druckman
 */
public class TableSearch extends RegExTool
{


	/**
	 * name for the column
	 * @param columnName display header
	 */
	public TableSearch  (String columnName)
	{
		super (columnName);
	}


	/**
	 * show results form
	 * @param number column number
	 * @param from the table to search
	 * @param title a title for the frame
	 */
	public void showTable (int number, JTable from, String title)
	{
		useColumn (number, from); refresh ();
		show (title);
	}


	private static final long serialVersionUID = 665332542534273250L;
}

