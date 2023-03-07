
package net.myorb.math.expressions.commands;

import net.myorb.math.expressions.evaluationstates.Arrays;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.ValueManager;

import java.util.ArrayList;
import java.util.List;

/**
 * formatter for displays of tables of data
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class Tabulation <T>
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
			List < String > titles
		)
	{
		List < ValueManager.DimensionedValue <T> > table =
			tableOf ( (ValueManager.ValueList) array );
		format (table);
	}
	@SuppressWarnings("unchecked") List < ValueManager.DimensionedValue <T> >
				tableOf (ValueManager.ValueList values)
	{
		List < ValueManager.DimensionedValue <T> > table = new ArrayList <> ();
		for ( ValueManager.GenericValue  v  :  values.getValues () )
		{ table.add ( (ValueManager.DimensionedValue <T>) v ); }
		return table;
	}
	void format (List < ValueManager.DimensionedValue <T> > table)
	{
		StringBuffer buffer;
		for (int row = 0; row < table.get (0).getValues ().size (); row++)
		{
			buffer = new StringBuffer ();
			for (int col = 0; col < table.size (); col++)
			{
				buffer.append ("\t").append (mgr.format (table.get (col).getValues ().get (row)));
			}
			System.out.println (buffer);
		}
	}


}

