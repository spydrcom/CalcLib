
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.ValueManager.ValueList;

import net.myorb.data.abstractions.DataSequence;

import net.myorb.math.Function;

import java.util.List;

/**
 * plot point collection for multiple functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class PlotMatrixForFunctionList <T> extends PlotMatrix <T>
{


	/**
	 * identify a function per plot index
	 * @param functions the list of functions
	 */
	public void setFunctions (List < Function <T> > functions)
	{
		this.setPlotCount (functions.size ());
		this.functions = functions;
	}
	protected List < Function <T> > functions;


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.PlotMatrix#eval(int, java.lang.Object)
	 */
	public T eval (int forPlotIndex, T using)
	{
		return functions.get (forPlotIndex).eval (using);
	}


	/**
	 * compute a plot matrix for the described plots
	 * @param forDomainValues the sequence of the domain values
	 * @param usingFunctions a list of functions to plot
	 * @return the value list of the matrix
	 */
	public ValueList evaluate
		(
			DataSequence <T> forDomainValues,
			List < Function <T> > usingFunctions
		)
	{
		setFunctions (usingFunctions);
		transform (forDomainValues);
		return getValueList ();
	}


}

