
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.evaluationstates.Arrays;

import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.DataConversions;
import net.myorb.math.expressions.ValueManager;

import net.myorb.charting.DisplayGraphTypes;

/**
 * recognize plot matrix built on value stack
 * - rows of plot matrix are series of the arrays of computations
 * - capture rows of the matrix and convert plot by plot to series of points
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class PlotSeries <T> extends DisplayGraphTypes
{


	public PlotSeries (Environment <T> environment)
	{
		this.conversion = environment.getConversionManager ();
		this.valueManager = environment.getValueManager ();
	}
	protected DataConversions<T> conversion = null;
	protected ValueManager<T> valueManager = null;


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


	/*
	 * SeriesBuilder mechanism - producers
	 */


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
					return DisplayGraphAtomic.pointsFor (domain, new RealSeries (conversion.convertToSeries (value)));
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
					return DisplayGraphAtomic.pointsFor (x, y);
				}
			}
		);
	}


	/*
	 * SeriesBuilder mechanism - consumer
	 */


	/**
	 * produce a plot generated using SeriesBuilder producer
	 * @param plots a value manager ValueList of data points to be plotted
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
		PlotCollection plotList = new PlotCollection ();
		for (ValueManager.GenericValue v : plots.getValues ())
		{ plotList.add (builder.getFunction (checkForDimension (v))); }
		multiPlot (plotList, title, trigger);
	}


	/**
	 * produce multiple plots over axis lines with a legend
	 * @param plots the collection of plot sequences to display
	 * @param title a title for the plot frame taken from the domain descriptor
	 * @param trigger screen input for mouse over and zoom control
	 */
	public void multiPlot
		(
			PlotCollection plots, String title,
			MouseSampleTrigger <T> trigger
		)
	{
		DisplayGraphAtomic.getChartLibrary ().multiPlotWithAxis
		(
			getStandardPlotColors (plots.size ()), plots,
			ConventionalNotations.determineNotationFor (title),
			trigger
		);
	}


	/**
	 * get a standard list of plot colors
	 * @param plotCount the number of plots to be displayed
	 * @return a list of colors to be used
	 */
	public Colors getStandardPlotColors (int plotCount)
	{
		Colors colors = new Colors ();
		MultiComponentUtilities.assignStandardColors (colors, plotCount);
		return colors;
	}


	/**
	 * verify the plot matrix
	 * @param value the row of the plot matrix to check
	 * @return the value now known to be a dimensioned matrix row
	 * @throws RuntimeException for a non-dimensioned value
	 */
	public ValueManager.GenericValue
		checkForDimension (ValueManager.GenericValue value)
	throws RuntimeException
	{
		if (value instanceof ValueManager.DimensionedValue) return value;
		throw new RuntimeException ("Dimensioned value expected in expression");
	}


}

