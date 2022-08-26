
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties;
import net.myorb.math.expressions.charting.DisplayGraph.SimpleLegend;
import net.myorb.math.expressions.evaluationstates.ArrayDescriptor;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.VectorPlotEnabled;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.PlotLegend;

import java.awt.Color;

/**
 * utilities for multi-component transform realization
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class MultiComponentUtilities <T>
{


	/**
	 * identify the properties presented by a context
	 */
	public interface ContextProperties
	{

		/**
		 * get the data source count
		 * @return the number of data sources for the context
		 */
		int getComponentCount ();

		/**
		 * get the traditional plot colors for the data context
		 * @param colors a display graph object for palate choices
		 */
		void assignColors (DisplayGraphTypes.Colors colors);

		/**
		 * get an identifier set for the data sources
		 * @return a set of identifiers to label component
		 */
		String[] componentIdentifiers ();

	}


	public MultiComponentUtilities
		(
			ContextProperties contextProperties,
			Environment <T> environment
		)
	{
		this.contextProperties = contextProperties;
		this.environment = environment;
	}
	protected ContextProperties contextProperties;
	protected Environment <T> environment;


	/**
	 * get the traditional plot colors for the data type
	 * @param colors a display graph object for palate choices
	 */
	public void assignColors (DisplayGraphTypes.Colors colors)
	{
		contextProperties.assignColors (colors);
	}


	/**
	 * copy color selections from the Plot Legend object
	 * @param colors the list of selected colors
	 * @param plotCount the number to select
	 */
	public static void assignStandardColors (DisplayGraphTypes.Colors colors, int plotCount)
	{
		Color[] legendColors = PlotLegend.COLORS;
		if (plotCount > legendColors.length) throw new RuntimeException (EXCESSIVE);
		for (int i = 0; i < plotCount; i++) colors.add (legendColors[i]);
	}
	static final String EXCESSIVE = "Excessive number of plots requested";


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
				public String[] getPlotSymbols ()
				{ return contextProperties.componentIdentifiers (); }
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
		return constructPlotCollection (contextProperties.getComponentCount ());
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


	/**
	 * display a plot collection
	 * @param title a text title for use by the display frame
	 * @param funcPlot the set of plot points for each function
	 * @param colors the color to use for each plot
	 * @param legend a legend for the plot
	 */
	public static <T> void multiPlotWithAxis
		(
			String title,
			DisplayGraphTypes.PlotCollection funcPlot,
			DisplayGraphTypes.Colors colors,
			SimpleLegend <T> legend
		)
	{
		String displayName =
			ConventionalNotations.determineNotationFor (title);
		DisplayGraphAtomic.getChartLibrary ().multiPlotWithAxis
		(colors, funcPlot, displayName, legend);
	}


	/**
	 * multiple component plot using data type standard
	 * @param title text for the plot to use as a title
	 * @param parameter the parameter name to display in the legend
	 * @param funcPlot the set of plot points for each function
	 */
	public void multiComponentPlot
		(
			String title, String parameter,
			DisplayGraphTypes.PlotCollection funcPlot
		)
	{
		DisplayGraphTypes.Colors colors =
			new DisplayGraphTypes.Colors ();
		assignColors (colors);

		multiPlotWithAxis
			(
				title, funcPlot, colors,
				buildSimpleLegend (parameter)
			);
	}


	/**
	 * multiple component plot Vector Enabled plot
	 * @param title text for the plot to use as a title
	 * @param transform a vector enabled transform for plot computations
	 * @param domainDescription a descriptor of the plot domain
	 */
	public void multiComponentPlot
		(
			String title,
			VectorPlotEnabled <T> transform,
			ArrayDescriptor <T> domainDescription
		)
	{
		multiComponentPlot
			(
				title,
				
				ConventionalNotations.determineNotationFor
				(
					domainDescription.getVariable ()
				),

				evaluateSeries
				(
					transform, domainDescription
				)
			);
	}


}

