
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend;
import net.myorb.math.expressions.evaluationstates.Arrays;
import net.myorb.math.expressions.ValueManager;

import net.myorb.math.Function;

import net.myorb.data.abstractions.DataSequence;

import net.myorb.charting.PlotLegend;

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
	 * build a legend for lambda plots
	 * @param descriptor the descriptor for the domain
	 * @return a simple legend for the display
	 */
	public SimpleLegend <T> getSimpleLegend (Arrays.Descriptor <T> descriptor)
	{
		SimpleLegend <T> legend = new SimpleLegend <T> ();
		legend.setDisplay (getLambdaLegend (descriptor.getVariable ()));
		return legend;
	}


	/**
	 * allocate legend display
	 * @param ID symbol for the x-axis
	 * @return a legend description object for lambda plots
	 */
	public PlotLegend.SampleDisplay getLambdaLegend (String ID)
	{
		return new PlotLegend.SampleDisplay ()
		{
			public String getVariable () { return ID; }								// x-axis variable
			public String [] getPlotExpressions () { return getLambdaList (); }		// list of functions
			public void display (String x, String [] samples) {}					// - for y-axis f(ID)
			public void setVariable (String variable) {}
			public void showLegend () {}
		};
	}


	// xpr = [OPR   (, IDN   J0, OPR   (, IDN   x, OPR   ), OPR   ,, IDN   I0, OPR   (, IDN   x, OPR   ), OPR   ,, IDN   K0, OPR   (, IDN   x, OPR   ), OPR   )]


}

