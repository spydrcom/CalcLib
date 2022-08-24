
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend;
import net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties;
import net.myorb.math.expressions.VectorPlotEnabled;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.charting.DisplayGraphTypes;

/**
 * utilities for multi-dimensional transform realization
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MultiDimensionalUtilities <T>
{


	public MultiDimensionalUtilities (Environment <T> environment)
	{
		this.mgr = (ExpressionComponentSpaceManager <T>)
				environment.getSpaceManager ();
		this.environment = environment;
	}
	protected ExpressionComponentSpaceManager<T> mgr;
	protected Environment <T> environment;


	/**
	 * get the traditional plot colors for the data type
	 * @param colors a display graph object for palate choices
	 */
	public void assignColors (DisplayGraphTypes.Colors colors)
	{
		mgr.assignColors (colors);
	}


	/**
	 * build a legend using the axis labels traditionally shown
	 * @param parameter the name of the parameter to show for the x-axis
	 * @return a simple legend object
	 */
	public SimpleLegend <T> buildSimpleLegend (String parameter)
	{
		return SimpleLegend.buildLegendFor
		(
			new SimpleLegend.LegendProperties ()
			{
				public String[] getPlotSymbols () { return mgr.axisLabels (); }
				public String getVariable () { return parameter; }
			}
		);
	}


	/**
	 * produce a plot collection from evaluation of a transform over a domain
	 * @param transform an object that implements the vector plot contract
	 * @param domainDescription descriptor of domain
	 * @return resulting collection of plots
	 */
	public DisplayGraphTypes.PlotCollection evaluateSeries
	(VectorPlotEnabled <T> transform, TypedRangeProperties <T> domainDescription)
	{
		DisplayGraphTypes.PlotCollection funcPlot = getPlotCollection ();
		transform.evaluateSeries (domainDescription, funcPlot, environment);
		return funcPlot;
	}


	/**
	 * construct a plot collection for the display
	 * @return a plot matrix for collecting the computed data
	 */
	public DisplayGraphTypes.PlotCollection getPlotCollection ()
	{
		return constructPlotCollection (mgr.getComponentCount ());
	}


	/**
	 * construct a plot collection of given size
	 * @param plotCount the number of plots in the collection
	 * @return the collection with point series inserted
	 */
	public static DisplayGraphTypes.PlotCollection constructPlotCollection (int plotCount)
	{
		DisplayGraphTypes.PlotCollection collection = new DisplayGraphTypes.PlotCollection ();
		for (int i = 0; i < plotCount; i++) { collection.add (new DisplayGraphTypes.Point.Series ()); }
		return collection;
	}


}

