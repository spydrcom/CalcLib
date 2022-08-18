
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.ValueManager;

import net.myorb.data.abstractions.DataSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * a low-level mechanism for collection of plot points
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public abstract class PlotMatrix <T>
{


	public PlotMatrix ()
	{ this.plots = new ArrayList < List <T> > (); }
	protected ValueManager <T> valueManager = new ValueManager <T> ();
	protected List < List <T> > plots;


	/**
	 * construct the matrix,
	 * - a plot per row, a range per column
	 * @param count the number of plot lines to produce
	 */
	public void setPlotCount (int count)
	{
		for (int i = 0; i < count; i++)
		{ plots.add (new ArrayList <T> ()); }
	}


	/**
	 * convert matrix to value list range representation
	 * @return the produced value list
	 */
	public ValueManager.ValueList getValueList ()
	{
		ValueManager.ValueList range = valueManager.newValueList ();
		ValueManager.GenericValueList valueList = range.getValues ();
		for (int i = 0; i < plots.size (); i++)
		{ add (plots.get (i), valueList); }
		return range;
	}
	void add (List<T> plot, ValueManager.GenericValueList to)
	{ to.add (valueManager.newDimensionedValue (plot)); }


	/**
	 * populate the matrix
	 * @param domainValues the sequence of the domain values
	 */
	public void transform (DataSequence <T> domainValues)
	{
		for (T x : domainValues)
		{
			for (int i = 0; i < plots.size (); i++)
			{
				// evaluate each function for each domain value
				plots.get (i).add (eval (i, x));
			}
		}
	}


	/**
	 * evaluation of the identified plot for the domain value
	 * @param forPlotIndex the index of the plot to evaluate
	 * @param using the domain value for this plot cell
	 * @return evaluation of this plot for specified
	 */
	public abstract T eval (int forPlotIndex, T using);


}

