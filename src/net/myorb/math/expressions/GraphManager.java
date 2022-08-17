
package net.myorb.math.expressions;

// commands
import net.myorb.math.expressions.commands.Utilities;
import net.myorb.math.expressions.commands.CommandSequence;

// lambda specific functionality
import net.myorb.math.expressions.algorithms.LambdaFunctionPlotter;

// evaluation states
import net.myorb.math.expressions.evaluationstates.ExtendedArrayFeatures;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.math.expressions.evaluationstates.ArrayDescriptor;
import net.myorb.math.expressions.evaluationstates.Arrays;

// charting
import net.myorb.math.expressions.charting.DisplayGraph;
import net.myorb.math.expressions.charting.DisplayGraph3D;
import net.myorb.math.expressions.charting.ExpressionGraphing;
import net.myorb.math.expressions.charting.HighDefinitionPlots;

import net.myorb.charting.DisplayGraphLibraryInterface.Portions;
import net.myorb.charting.DisplayGraphLibraryInterface.CategorizedPortion;
import net.myorb.charting.DisplayGraphLibraryInterface.CategorizedPortions;
import net.myorb.charting.DisplayGraphLibraryInterface.Portion;
import net.myorb.charting.DisplayGraphProperties;

// data
import net.myorb.data.abstractions.CommonCommandParser;
import net.myorb.data.abstractions.DataSequence;

// JRE
import java.util.HashSet;
import java.util.List;

/**
 * support for charting and other expression graphing
 * @param <T> type of menu items
 * @author Michael Druckman
 */
public class GraphManager<T> extends ExpressionGraphing<T>
{


	/**
	 * the types of plots
	 */

	public enum Types
	{
		ARRAY,
		COMPLEX,
		FUNCTION,
		POLY_DERIVATION,
		POLY_EVALUATION
	}


	/**
	 * a real series taken directly from a list of T data items
	 */
	public class ExtendedRealSeries extends RealSeries
	{
		public ExtendedRealSeries (List<T> items)
		{ this.addAll (conversion.convertToSeries (items)); }
		private static final long serialVersionUID = -8390580795236803241L;
	}


	public GraphManager (Environment<T> environment) { super (environment); }


	/**
	 * plot UDF over range
	 * @param functionName name of function to plot
	 * @param descriptor the descriptor for the domain
	 */
	public void singleFunctionPlot (String functionName, ArrayDescriptor<T> descriptor)
	{
		SymbolMap.Named functionSymbol = environment.getSymbolMap ().lookup (functionName);
		String parameterNotation = ConventionalNotations.determineNotationFor (descriptor.getVariable ());
		singlePlotOfValues (functionSymbol, parameterNotation, descriptor);
	}


	/*
	 * lambda plot (PLOTL) processing
	 */


	/**
	 * get the Lambda Processor and
	 *  provide environment access to the code layer
	 * @return access to the LambdaExpressions processor
	 */
	public LambdaFunctionPlotter <T> getLambdaProcessor ()
	{
		LambdaFunctionPlotter <T> processor =
			environment.getLambdaExpressionProcessor ();
		environment.provideAccessTo (processor);
		return processor;
	}


	/**
	 * plot the lambda functions
	 * @param descriptor the descriptor for the domain
	 * @param domainValueSequence the DataSequence holding domain values
	 * @param lambda the lambda processor for the plots
	 */
	public void doMultiLambdaPlot
		(
			Arrays.Descriptor <T> descriptor,
			DataSequence <T> domainValueSequence,
			LambdaFunctionPlotter <T> lambda
		)
	{
		this.multiLambdaPlot
		(
			descriptor,
			new ExtendedRealSeries (domainValueSequence),
			lambda.computeLambdaRange (domainValueSequence),
			lambda.getSimpleLegend (descriptor)
		);
	}


	/**
	 * plot lambda functions over range
	 * @param descriptor the descriptor for the domain
	 */
	public void lambdaFunctionPlots (Arrays.Descriptor <T> descriptor)
	{
		this.doMultiLambdaPlot
		(
			descriptor,
			domain (descriptor, environment.getSpaceManager ()),
			getLambdaProcessor ()
		);
	}


	/**
	 * plot lambda functions over range from command text
	 * @param sequence the text of the command
	 */
	public void lambdaFunctionPlots (CommandSequence sequence)
	{
		ExtendedArrayFeatures <T> arrays = new ExtendedArrayFeatures <T> ();
		this.lambdaFunctionPlots (arrays.getArrayDescriptor (sequence, 1, environment));
	}


	/*
	 * matrix tabulation displays
	 */


	/**
	 * Display tabular plot with data taken from a matrix
	 * @param symbolName name of matrix with plot data
	 * @param sequence parameters of plot
	 */
	public void tabularFunctionPlot
		(String symbolName, CommandSequence sequence)
	{
		SymbolMap.Named matrix =
			environment.getSymbolMap ().lookup (symbolName);
		System.out.println (matrix);

		throw new RuntimeException
		(
			"Matrix plot not implemented"
		);
	}


	/**
	 * Display tabular plot with data taken from a file
	 * @param filepath path to source of plot data
	 */
	public void tabularPlotFromFile (String filepath)
	{
		DisplayGraph3D.tabularPlotFromFile (filepath);
	}


	/*
	 * simple function plot drivers
	 */


	/**
	 * plot UDF over range from command text
	 * @param sequence the text of the command
	 */
	public void singleFunctionPlot (CommandSequence sequence)
	{
		StringBuffer fullName = new StringBuffer ();
		int pos = Utilities.getFunctionName (0, sequence, fullName);
		ExtendedArrayFeatures<T> arrays = new ExtendedArrayFeatures<T> ();

		singleFunctionPlot
		(
			fullName.toString (), arrays.getArrayDescriptor (sequence, pos, environment)
		);
	}


	/**
	 * plot UDF over complex range
	 * @param functionName name of function to plot
	 * @param descriptor the descriptor for the domain
	 */
	public void singleComplexFunctionPlot (String functionName, ArrayDescriptor<T> descriptor)
	{
		SymbolMap.Named functionSymbol = environment.getSymbolMap ().lookup (functionName);
		String parameterNotation = ConventionalNotations.determineNotationFor (descriptor.getVariable ());
		singlePlotOfComplexValues (functionSymbol, parameterNotation, descriptor);
	}


	/**
	 * plot UDF over complex range from command text
	 * @param sequence the text of the command
	 */
	public void singleComplexFunctionPlot (CommandSequence sequence)
	{
		StringBuffer fullName = new StringBuffer ();
		int pos = Utilities.getFunctionName (0, sequence, fullName);
		ExtendedArrayFeatures<T> arrays = new ExtendedArrayFeatures<T> ();

		singleComplexFunctionPlot
		(
			fullName.toString (), arrays.getArrayDescriptor (sequence, pos, environment)
		);
	}


	/**
	 * plot UDF as 3D Contour
	 * @param functionName name of function to plot
	 * @param sequence parameters of plot
	 */
	public void singleContourFunctionPlot (String functionName, CommandSequence sequence)
	{
		DisplayGraph.Point p;
		int pos = parsePoint (sequence, 0, p = new DisplayGraph.Point ());
		SymbolMap.Named functionSymbol = environment.getSymbolMap ().lookup (functionName);
		double edge = valueOf (sequence, pos++), mul = sequence.size () > pos ? valueOf (sequence, pos) : 1;
		new HighDefinitionPlots<T> (environment).contourPlotOf3DValues (functionSymbol, p, edge, mul);
	}


	/**
	 * plot UDF as 3D mesh plot
	 * @param functionName name of function to plot
	 * @param sequence parameters of plot
	 */
	public void single3DFunctionPlot (String functionName, CommandSequence sequence)
	{
		DisplayGraph.Point p;
		int pos = parsePoint (sequence, 0, p = new DisplayGraph.Point ());
		double edgex = valueOf (sequence, pos++), edgey = valueOf (sequence, pos);
		SymbolMap.Named functionSymbol = environment.getSymbolMap ().lookup (functionName);
		new HighDefinitionPlots<T> (environment).singlePlotOf3DValues (functionSymbol, p, edgex, edgey);
	}
	private double valueOf (CommandSequence sequence, int at)
	{ return sequence.get (at).getTokenValue ().doubleValue (); }


	/**
	 * @param type the type of plot to be done
	 * @param tos the top of stack value (computed array of points)
	 */
	public void plot (Types type, ValueManager.GenericValue tos)
	{
		switch (type)
		{
			case ARRAY:				plotValues (tos);			break;
			case COMPLEX:			plotStructuredData (tos);	break;
			case POLY_DERIVATION:	processPolynomialDerivation (tos);	break;
			case POLY_EVALUATION:	processPolynomialEvaluation (tos);	break;
			default:
		}
	}


	/**
	 * construct a chart
	 * @param styleName the style for the chart
	 * @param sequence the command tokens
	 */
	public void chartFor (String styleName, CommandSequence sequence)
	{
		DisplayGraph.getChartLibrary ().traditionalChart (styleName, portionsFor (sequence));;
	}


	/**
	 * determine type of portions to allocate
	 * @param sequence the command tokens
	 * @return a full portions object
	 */
	public Portions portionsFor (CommandSequence sequence)
	{
		try
		{
			SymbolMap.Named firstSymbol = getSymbolFor (sequence.get (0));
			ValueManager.GenericValue value = getValueFor (firstSymbol);
			if (valueManager.isDimensioned (value))
			{
				return categorizedPortionsFor (sequence);
			}
			return simplePortionsFor (sequence);
		}
		catch (Exception e)
		{
			throw new RuntimeException ("Error building chart", e);
		}
	}


	/**
	 * categorized portions
	 * @param sequence the command tokens
	 * @return categorized portions
	 */
	public Portions categorizedPortionsFor (CommandSequence sequence)
	{
		String[] categories = getCategories (); int categoryCount = categories.length;
		CategorizedAllocations allocations = new CategorizedAllocations (categories, getCategoryTitle ());
		for (CommonCommandParser.TokenDescriptor t : sequence)
		{
			SymbolMap.Named symbol = getSymbolFor (t);
			allocations.add (symbol.getName (), getValuesFor (symbol, categoryCount));
		}
		return allocations;
	}
	public double[] getValuesFor (SymbolMap.Named symbol, int categoryCount)
	{
		ValueManager.RawValueList<T> list; ValueManager.GenericValue value;
		if ( ! valueManager.isDimensioned (value = getValueFor (symbol)) )
		{ throw new RuntimeException ("Symbol not dimensioned: " + symbol.getName ()); }
		if ((list = valueManager.toDiscreteValues (value)).size () != categoryCount)
		{ throw new RuntimeException ("Symbol has wrong length: " + symbol.getName ()); }
		return list.toDoubleFloatArray (environment.getSpaceManager ());
	}
	String[] getCategories () { return DisplayGraphProperties.getTextListProperty ("CATEGORIES").toArray (new String[]{}); }
	String getCategoryTitle () { return DisplayGraphProperties.getTextProperty ("CATEGORY_LABEL"); }


	/**
	 * simple portions only
	 * @param sequence the command tokens
	 * @return simple portions, no Z axis
	 */
	public Portions simplePortionsFor (CommandSequence sequence)
	{
		Allocations portions = new Allocations ();
		for (CommonCommandParser.TokenDescriptor t : sequence)
		{
			SymbolMap.Named symbol = getSymbolFor (t);
			portions.add (symbol.getName (), getDiscreteValueFor (symbol));
		}
		return portions;
	}


	/**
	 * @param t a token descriptor from command line
	 * @return the represented symbol
	 */
	public SymbolMap.Named getSymbolFor (CommonCommandParser.TokenDescriptor t)
	{
		String name = t.getTokenImage ();
		SymbolMap.Named symbol = environment.getSymbolMap ().lookup (name);
		if (symbol == null) throw new RuntimeException ("Unrecognized symbol: " + name);
		return symbol;
	}


	/**
	 * @param symbol a named symbol
	 * @return the double float value
	 */
	public double getDiscreteValueFor (SymbolMap.Named symbol)
	{
		return environment.getSpaceManager ().convertToDouble
		(
			environment.getValueManager ().toDiscrete (getValueFor (symbol))
		);
	}


	/**
	 * @param symbol a named symbol
	 * @return the generic value
	 */
	public ValueManager.GenericValue getValueFor (SymbolMap.Named symbol)
	{
		return ((SymbolMap.VariableLookup) symbol).getValue ();
	}


}


/**
 * simple portion set for pie charts and bar charts
 */
class Allocations extends HashSet<Portion>
	implements Portions
{

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphLibraryInterface.Portions#getNames()
	 */
	public String[] getNames ()
	{
		String names [] = new String [this.size ()];
		int n = 0; for (Portion p : this) names[n++] = p.getName ();
		return names;
	}

	public void add (String name, double value) { add (new Allocation (name, value)); }
	private static final long serialVersionUID = 9168369822587153607L;
}


/**
* a portion description that can be used by simple bar/pie charts...
* but can also be use as the portion for categorized bar charts
*/
class Allocation implements CategorizedPortion
{

	public Allocation (String name, double value)
	{ this.name = name; this.values = new double [] {value}; }
	
	public Allocation (String name, double [] values) { this.name = name; this.values = values; }

	public double[] getPortionsByCategory () { return values; }
	public double getPortion () { return values [0]; }
	public String getName () { return name; }
	String name; double [] values;

}


/**
* the extended version of portions for z-axis bar charts only
*/
class CategorizedAllocations extends Allocations
		implements CategorizedPortions
{

	String categories [], categoryTitle;
	public String [] getCategories () { return categories; }
	public String getCategoryTitle () { return categoryTitle; }

	public void add (String name, double [] values) { add (new Allocation (name, values)); }

	public CategorizedAllocations (String [] categories, String categoryTitle)
	{ this.categories = categories; this.categoryTitle = categoryTitle; }

	private static final long serialVersionUID = 8500906784307558522L;
}

