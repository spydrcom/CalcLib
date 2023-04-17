
package net.myorb.math.expressions.commands;

import net.myorb.math.expressions.evaluationstates.Arrays;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;

import net.myorb.gui.components.SimpleScreenIO.WidgetFrame;
import net.myorb.gui.components.RenderingDisplay;
import net.myorb.data.abstractions.HtmlTable;

import net.myorb.data.abstractions.CommonDataStructures;

/**
 * formatter for displays of tables of data
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Tabulation <T> extends CommonDataStructures
{


	public Tabulation (Environment<T> environment)
	{ this.environment = environment; this.mgr = environment.getSpaceManager (); }
	Environment<T> environment; ExpressionSpaceManager <T> mgr;


	/**
	 * the top of stack value should be a list of generic discrete values.
	 *  the list is a set of function values to be displayed as described in meta-data.
	 *  the meta-data is found associated with the top of value stack when pushed by the array operator
	 * @param array the value popped from the top of value stack
	 */
	public void tabulateValues (ValueManager.GenericValue array)
	{
		Arrays.Descriptor <T> domainDescriptor = environment.getArrayMetadataFor (array);
		tabulateValues (domainDescriptor, array, domainDescriptor.columnTitles ());
	}


	/**
	 * display a tabulation of computed data
	 * @param domainDescriptor the descriptor of the line item parameters
	 * @param array the data computed as array of arrays
	 * @param titles the titles of the columns
	 */
	public void tabulateValues
		(
			Arrays.Descriptor <T> domainDescriptor,
			ValueManager.GenericValue array,
			TextItems titles
		)
	{
		ValueManager.TableOfValues <T> table =
			tableOf ( (ValueManager.ValueList) array );
		format (domainDescriptor.formatTitle (), titles, table);
	}
	@SuppressWarnings("unchecked") ValueManager.TableOfValues <T>
			tableOf (ValueManager.ValueList values)
	{
		ValueManager.TableOfValues <T> table =
				new ValueManager.TableOfValues <T> ();
		for ( ValueManager.GenericValue  v  :  values.getValues () )
		{ table.add ( (ValueManager.DimensionedValue <T>) v ); }
		return table;
	}


	/**
	 * construct and show HTML mark-up of data
	 * @param documentTitle a title for the display
	 * @param titles the titles given to individual columns
	 * @param table the table data as list of lists
	 */
	public void format
		(
			String documentTitle, TextItems titles,
			ValueManager.TableOfValues <T> table
		)
	{
		String [] rowCells = new String [titles.size ()];
		HtmlTable html = new HtmlTable (); html.setTitle (documentTitle);
		html.setTableHeader (documentTitle); html.setColumnHeaders (titles.toArray (new String[]{}));
		
		for (int row = 0; row < table.get (0).getValues ().size (); row++)
		{
			for (int col = 0; col < table.size (); col++)
			{ rowCells[col] = mgr.format (table.get (col).getValues ().get (row)); }
			html.addRow (rowCells);
		}

		showTable (html, documentTitle);
	}


	/**
	 * build a GUI to display table
	 * @param html mark-up object describing table
	 * @param title the title to place in the header
	 */
	public static void showTable
		(HtmlTable html, String title)
	{
		RenderingDisplay display;
		(display = RenderingDisplay.newRenderingDisplayPanel ()).addComponent (html);
		new WidgetFrame (display.getRenderingPanel (), title)
		.showOrHide (1200, 500);
	}


}

