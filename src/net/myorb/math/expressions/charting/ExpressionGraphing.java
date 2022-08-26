
package net.myorb.math.expressions.charting;

// computation
import net.myorb.math.Polynomial;
import net.myorb.math.computational.PolynomialEvaluation;
import net.myorb.math.complexnumbers.FunctionDerivativesTable;

// expressions
import net.myorb.math.expressions.*;
import net.myorb.math.expressions.symbols.DefinedFunction;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.ArrayDescriptor;
import net.myorb.math.expressions.evaluationstates.Arrays;

// xtn libraries
import net.myorb.data.abstractions.Function;
import net.myorb.charting.PlotLegend;

// JRE
import java.awt.Color;
import java.util.List;

/**
 * implementation of the graph command
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class ExpressionGraphing<T> extends DisplayGraph
{


	/**
	 * space manager is used to convert values to float
	 * @param environment the common environment object for the engine
	 */
	public ExpressionGraphing (Environment<T> environment)
	{
		this.conversion = environment.getConversionManager ();
		this.valueManager = environment.getValueManager ();
		this.environment = environment;
	}
	protected DataConversions<T> conversion = null;
	protected ValueManager<T> valueManager = null;
	protected boolean addDerivative = false;
	protected Environment<T> environment;


	/*
	 * simple plots
	 */


	/**
	 * wrap function for working with double float domain values
	 * @param functionSymbol the symbol for the function being plotted
	 * @param parameter the name of the parameter to the function
	 * @param domainDescription the properties of the domain
	 */
	public void singlePlotOfValues
		(
			SymbolMap.Named functionSymbol, String parameter,
			TypedRangeDescription.TypedRangeProperties<T> domainDescription
		)
	{
		DisplayGraph.RealFunction functionDescription =
			conversion.toRealFunction (DefinedFunction.verifyDefinedFunction (functionSymbol));
		singlePlotOfValues (functionSymbol.getName (), functionDescription, parameter, domainDescription);
	}


	/**
	 * build call to chart library
	 * @param functionName the name of the function
	 * @param f the function wrapped for operation on double float
	 * @param parameter the name of the parameter to the function
	 * @param domainDescription descriptor of domain
	 */
	public void singlePlotOfValues
		(
			String functionName, DisplayGraph.RealFunction f, String parameter,
			TypedRangeDescription.TypedRangeProperties<T> domainDescription
		)
	{
		getChartLibrary ().singlePlotWithAxis
		(
			Color.BLUE,
			getPlotList (f, domainDescription, environment.getSpaceManager ()), 
			ConventionalNotations.determineNotationFor (functionName), parameter, f
		);
	}


	/*
	 * multi-dimensional plots
	 */


	/**
	 * wrap function for working with complex domain values
	 * @param functionSymbol the symbol for the function being plotted
	 * @param domainDescription the properties of the domain
	 */
	public void singlePlotOfComplexValues
		(
			SymbolMap.Named functionSymbol,
			ArrayDescriptor<T> domainDescription
		)
	{
		plotMultiDimensionalRange
		(
			functionSymbol.getName (),
			ExpressionAnalysis.forceEnabled (functionSymbol),
			domainDescription
		);
	}


	/**
	 * Multi-Dimensional function range plot
	 * @param functionName the name of the function
	 * @param transform an object that implements the vector plot contract
	 * @param domainDescription descriptor of domain
	 */
	public void plotMultiDimensionalRange
		(
			String functionName, VectorPlotEnabled <T> transform,
			ArrayDescriptor <T> domainDescription
		)
	{
		new MultiDimensionalUtilities <T>
			(
				( MultiDimensionalUtilities.ContextProperties )
					environment.getSpaceManager (),
				environment
			)
		.multiDimensionalFunctionPlot (functionName, transform, domainDescription);
	}


	/*
	 * domain processing
	 */


	/**
	 * the top of stack value should be a list of generic discrete values.
	 *  the list is a set of function values to be plotted as described in meta-data.
	 *  the meta-data is found associated with the top of value stack when pushed by the array operator
	 * @param array the value popped from the top of value stack
	 */
	public void plotValues (ValueManager.GenericValue array)
	{
		Arrays.Descriptor<T> domainDescriptor =
				environment.getArrayMetadataFor (array);
		plotValues (domainDescriptor, array, domainDescriptor.formatTitle ());
	}


	/**
	 * @param domainDescriptor the array descriptor for the domain
	 * @param values the values of the functions at the domain points
	 * @param titled a title for the plot
	 */
	public void plotValues
		(
			Arrays.Descriptor<T> domainDescriptor,
			ValueManager.GenericValue values,
			String titled
		)
	{
		RealSeries domainValues = domain (domainDescriptor, conversion);
		plotValues (titled, domainDescriptor, domainValues, values);
	}


	/*
	 * domain to range transition
	 */


	/**
	 * plot multiple lambda functions
	 * @param domainDescriptor descriptor of domain array
	 * @param domainValues list of values of domain
	 * @param rangeValues list of values of range
	 * @param legend lambda specific legend
	 */
	@Deprecated public void multiLambdaPlot
		(
			Arrays.Descriptor<T> domainDescriptor, RealSeries domainValues,
			ValueManager.ValueList rangeValues, SimpleLegend <T> legend
		)
	{
		this.multiPlot (domainValues, rangeValues, "Lambda Functions", legend);
	}


	/**
	 * choose chart generation interface
	 * @param title a title for the chart
	 * @param domainDescriptor descriptor of domain array
	 * @param domainValues list of values of domain
	 * @param rangeValues list of values of range
	 */
	public void plotValues
		(
			String title,
			Arrays.Descriptor<T> domainDescriptor, RealSeries domainValues,
			ValueManager.GenericValue rangeValues
		)
	{
		if (rangeValues instanceof ValueManager.ValueList)
		{
			MouseSampleTrigger<T> trigger = makeTrigger (domainDescriptor, environment);
			multiPlot (domainValues, (ValueManager.ValueList)rangeValues, title, trigger);
		}
		else
		{
			plotOverDomain
			(
				title, domainDescriptor.getExpressionText (), domainValues,
				rangeValues, domainDescriptor.genMacro (environment, true)
			);
		}
	}


	/*
	 * meta-data processing
	 */


	/**
	 * @param array the data for the plot with meta-data
	 */
	public void plotStructuredData (ValueManager.GenericValue array)
	{
		Arrays.Descriptor<T> domainDescriptor = environment.getArrayMetadataFor (array);
		plotStructuredData (domainDescriptor.formatTitle (), domainDescriptor, array);
	}


	/**
	 * prepare for mouse events for legend
	 * @param arrayDescriptor descriptor of domain array
	 * @param environment the common environment object for the engine
	 * @param <T> data type used in all expressions
	 * @return a mouse trigger for the legend
	 */
	public static <T> MouseSampleTrigger<T> makeTrigger
	(Arrays.Descriptor<T> arrayDescriptor, Environment<T> environment)
	{
		MouseSampleTrigger<T> trigger = new MouseSampleTrigger<T>();
		trigger.setMacro (arrayDescriptor.genMacro (environment, true));
		trigger.setDisplay (legendFor (arrayDescriptor));
		return trigger;
	}


	/**
	 * populate properties of legend display
	 * @param arrayDescriptor descriptor of domain array
	 * @param <T> data type used in all expressions
	 * @return a sample display for the legend
	 */
	public static <T> PlotLegend.SampleDisplay legendFor (Arrays.Descriptor<T> arrayDescriptor)
	{
		PlotLegend.SampleDisplay legendDisplay = PlotLegend.constructLegend
				(TokenParser.toPrettyText (arrayDescriptor.getExpression ()));
		legendDisplay.setVariable (arrayDescriptor.getVariable ());
		return legendDisplay;
	}


	/*
	 * SeriesBuilder mechanism
	 */


	/**
	 * convert value structure to plot functions
	 */
	interface SeriesBuilder
	{
		/**
		 * procedure parameter allowing different types of functions
		 * @param value a value manager generic value.  must be DimensionedValue
		 * @return a series of plot points
		 */
		Point.Series getFunction (ValueManager.GenericValue value);
	}


	/**
	 * @param plots a value manager ValueList of data points
	 * @param title a title for the plot frame taken from the domain descriptor
	 * @param trigger screen input for mouse over and zoom control
	 * @param builder implementation of SeriesBuilder
	 */
	public void multiPlot
		(
			ValueManager.ValueList plots, String title,
			MouseSampleTrigger<T> trigger, SeriesBuilder builder
		)
	{
		int c = 0;
		Colors colors = new Colors ();
		PlotCollection plotList = new PlotCollection ();
		List<ValueManager.GenericValue> plotArrays =  plots.getValues ();
	
		for (ValueManager.GenericValue v : plotArrays)
		{
			if (v instanceof ValueManager.DimensionedValue)
			{
				Point.Series function = builder.getFunction (v);
				plotList.add (function); colors.add (PlotLegend.COLORS[c++]);
				if (c == PlotLegend.COLORS.length) break;
			}
			else throw new RuntimeException ("Dimensioned value expected in expression");
		}
	
		getChartLibrary ().multiPlotWithAxis (colors, plotList, title, trigger);
	}


	/**
	 * process multiple parallel plots based on a single domain
	 * @param domain the domain values for the basis of the plots
	 * @param plots the value list holding the plot values
	 * @param title a title for the plot frame
	 * @param trigger screen input
	 */
	public void multiPlot (RealSeries domain, ValueManager.ValueList plots, String title, MouseSampleTrigger<T> trigger)
	{
		multiPlot
		(
			plots, title, trigger,
			new SeriesBuilder ()
			{
				public Point.Series getFunction (ValueManager.GenericValue value)
				{
					return pointsFor (domain, new RealSeries (conversion.convertToSeries (value)));
				}
			}
		);
	}


	/**
	 * structured data plot
	 * @param title a title for the plot frame
	 * @param domainDescriptor description of the domain
	 * @param plots the data points collected for plot
	 */
	public void plotStructuredData
	(String title, Arrays.Descriptor<T> domainDescriptor, ValueManager.GenericValue plots)
	{
		multiPlot
		(
			(ValueManager.ValueList) plots, title, null,
			new SeriesBuilder ()
			{
				public Point.Series getFunction (ValueManager.GenericValue value)
				{
					RealSeries x = new RealSeries (), y = new RealSeries ();
					conversion.convertToStructure (valueManager.toDiscreteValues (value), x, y);
					return pointsFor (x, y);
				}
			}
		);
	}


	/*
	 * plot drivers
	 */


	/**
	 * verify properties for the plot
	 * @param title a title for the plot frame
	 * @param expression the notation describing the plot
	 * @param domain the domain values for the basis of the plots
	 * @param rangeValues the array of points making the plot range
	 * @param f the function being plotted
	 */
	public void plotOverDomain
		(
			String title, String expression, RealSeries domain,
			ValueManager.GenericValue rangeValues, Function<T> f
		)
	{
		if (!(rangeValues instanceof ValueManager.DimensionedValue))
			throw new RuntimeException ("Dimensioned value expected in expression");
		plotOverDimensionedDomain (title, expression, domain, valueManager.getDimensionedValue (rangeValues), f);
	}


	/**
	 * convert abstract data to double float
	 * @param title a title for the plot frame
	 * @param expression the notation describing the plot
	 * @param domain the domain values for the basis of the plots
	 * @param dimensioned the array of plot points
	 * @param f the function being plotted
	 */
	public void plotOverDimensionedDomain
		(
			String title, String expression, RealSeries domain,
			ValueManager.DimensionedValue<T> dimensioned, Function<T> f
		)
	{
		Point.Series function =
			pointsFor (domain, new RealSeries (conversion.convertToSeries (dimensioned.getValues ())));
		plotPoints (function, title, expression, f);
	}


	/**
	 * prepare to display graph
	 * @param plotPoints the points to plot for the function
	 * @param title for display on the frame of the plot
	 * @param expression the notation being plotted
	 * @param f function implementation
	 */
	public void plotPoints
		(
			Point.Series plotPoints,
			String title, String expression,
			Function<T> f
		)
	{
		if (plotPoints.size() == 0)
			throw new RuntimeException ("Empty plot");
		Colors colors = makeColorList (Color.WHITE);
		PlotCollection plotList = makePlotCollection (plotPoints);

		if (addDerivative) { colors.add (Color.GREEN); plotList.add (computeDerivative (plotPoints)); }
		getChartLibrary ().multiPlotWithAxis (colors, plotList, title, expression, conversion.toRealFunction (f));
	}


	/**
	 * compute a derivative plot from a function plot
	 * @param function the plot of the function to be derived
	 * @return the points of the derivative plot
	 */
	public static Point.Series computeDerivative (List<Point> function)
	{
		double difference, midPoint, slope;
		Point previous = function.get (0), next;
		Point.Series points = new Point.Series ();
		for (int i = 1; i < function.size (); i++)
		{
			next = function.get (i);
			difference = next.x - previous.x;
			midPoint = (next.x + previous.x) / 2;
			slope = (next.y - previous.y) / difference;
			points.add (new Point (midPoint, slope));
			previous = next;
		}
		return points;
	}


	/**
	 * processing for a polynomial evaluation request
	 * @param values a set of values to use as polynomial coefficients
	 */
	public void processPolynomialEvaluation (List<T> values)
	{
		Polynomial.Coefficients<Double>
			coefficients = conversion.convert (values);
		PolynomialEvaluation.process (coefficients);
	}


	/**
	 * the top of stack value should be a list of generic discrete.
	 *  the list will be interpreted as a set of polynomial coefficients
	 * @param value the value popped from the top of value stack
	 */
	public void processPolynomialEvaluation (ValueManager.GenericValue value)
	{ processPolynomialEvaluation (valueManager.toDiscreteValues (value)); }


	/**
	 * the top of stack value should be a list of generic discrete values.
	 *  the list will be interpreted as a set of polynomial coefficients
	 * @param value the value popped from the top of value stack
	 */
	public void processPolynomialDerivation (ValueManager.GenericValue value)
	{
		FunctionDerivativesTable.showTable (conversion.convert (valueManager.toDiscreteValues (value)));
	}


}


