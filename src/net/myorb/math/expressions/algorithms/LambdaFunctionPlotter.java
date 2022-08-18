
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend;
import net.myorb.math.expressions.evaluationstates.Arrays;
import net.myorb.math.expressions.ValueManager;

import net.myorb.data.abstractions.DataSequence;

import net.myorb.math.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * Plotter functionality for Lambda functions
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class LambdaFunctionPlotter <T> extends LambdaExpressions <T>
{


	/**
	 * produce a plot matrix of lambda functions
	 * @param domainValues the values of the domain
	 * @return the description of the range
	 */
	public ValueManager.ValueList
		computeLambdaRange (DataSequence <T> domainValues)
	{
		// the list of function matching the criteria
		List < Function <T> > functions = getSimpleFunctionList ();

		// construct the matrix, a plot per row, a range per column
		List < List <T> > plots = new ArrayList < List <T> > ();
		for (int i = 0; i < functions.size (); i++)
		{ plots.add (new ArrayList <T> ()); }

		// evaluate each function for each domain value

		for (T x : domainValues)
		{
			for (int i = 0; i < functions.size (); i++)
			{
				T functionResult = functions.get (i).eval (x);
				plots.get (i).add (functionResult);
			}
		}

		// convert matrix to value list range representation
		ValueManager.ValueList range = valueManager.newValueList ();
		ValueManager.GenericValueList valueList = range.getValues ();
		for (int i = 0; i < functions.size (); i++)
		{ add (plots.get (i), valueList); }

		return range;
	}
	void add (List<T> plot, ValueManager.GenericValueList to)
	{ to.add (valueManager.newDimensionedValue (plot)); }


	/**
	 * @return the list of names for the lambda plot legend
	 */
	public String[] getLambdaList ()
	{
		List <Integer> unary = unaryFunctionProfiles ();
		String [] legendNames = new String [unary.size ()];
		for (int n = 0; n < legendNames.length; n++)
		{
			String ID = Integer.toString (unary.get (n));
			legendNames[n] = LAMBDA + ID;
		}
		return legendNames;
	}
	public static final String LAMBDA = "\u03BB";


	/**
	 * construct the legend to display for the plot
	 * @param descriptor the descriptor for the domain
	 * @return a simple legend object for the plot
	 */
	public SimpleLegend <T> getSimpleLegend (Arrays.Descriptor <T> descriptor)
	{
		return SimpleLegend.buildLegendFor
			(
				new SimpleLegend.LegendProperties ()
				{

					/* (non-Javadoc)
					 * @see net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend.LegendProperties#getPlotSymbols()
					 */
					public String [] getPlotSymbols ()
					{
						return getLambdaList ();
					}

					/* (non-Javadoc)
					 * @see net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend.LegendProperties#getVariable()
					 */
					public String getVariable ()
					{
						return descriptor.getVariable ();
					}

				}
			);
	}


}

