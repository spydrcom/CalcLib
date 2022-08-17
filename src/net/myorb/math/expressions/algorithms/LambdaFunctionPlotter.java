
package net.myorb.math.expressions.algorithms;

import net.myorb.math.expressions.ValueManager;
import net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend;
import net.myorb.math.expressions.evaluationstates.Arrays;

//import net.myorb.charting.DisplayGraphTypes.RealSeries;

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
		//System.out.println ("domainValues"+domainValues);

		ValueManager.ValueList range = valueManager.newValueList ();

		List<T> listT0 = new ArrayList<T>();
		List<T> listT1 = new ArrayList<T>();

		for (T d : domainValues)
		{

			listT0.add(d);

			listT1.add(environment.getSpaceManager().negate(d));

		}

		ValueManager.DimensionedValue<T> dim = valueManager.newDimensionedValue (listT0);
		range.getValues ().add (dim);

		dim = valueManager.newDimensionedValue (listT1);
		range.getValues ().add (dim);

		return range;
	}


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
	 * @param ID symbol for the x-axis
	 * @return a legend description object for lambda plots
	 */
	public PlotLegend.SampleDisplay getLambdaLegend (String ID)
	{
		return new PlotLegend.SampleDisplay ()
		{
			public void display (String x, String[] samples) {}
			public void setVariable (String variable) {}
			public String getVariable () { return ID; }
			public String[] getPlotExpressions ()
			{ return new String[]{LAMBDA+"0",LAMBDA+"1"}; }
			public void showLegend () {}
		};
	}
	public static final String LAMBDA = "\u03BB";


	// xpr = [OPR   (, IDN   J0, OPR   (, IDN   x, OPR   ), OPR   ,, IDN   I0, OPR   (, IDN   x, OPR   ), OPR   ,, IDN   K0, OPR   (, IDN   x, OPR   ), OPR   )]


}
