
package net.myorb.math.expressions.charting;

import net.myorb.charting.DisplayGraphTypes.PlotCollection;
import net.myorb.charting.DisplayGraphTypes.Colors;

/**
 * graphics library interface
 * @author Michael Druckman
 */
public interface DisplayGraphLibraryInterface
	extends net.myorb.charting.DisplayGraphLibraryInterface
{

	/**
	 * multi function plotting with a legend
	 * @param colors the color to use for each plot
	 * @param funcPlot the set of plot points for each function
	 * @param title a text title for use by the display frame
	 * @param trigger the mouse handler for the legend
	 */
	@SuppressWarnings ("rawtypes")
	public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlot, String title, MouseSampleTrigger trigger
		);

}

