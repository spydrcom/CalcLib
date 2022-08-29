
package net.myorb.math.expressions.charting;

import net.myorb.math.expressions.evaluationstates.Subroutine;

import java.awt.Color;

/**
 * implementation of graphics interface
 *  making DisplayGraph class the default behavior
 * @author Michael Druckman
 */
public class DisplayGraphPrimitives extends DisplayGraph
	implements DisplayGraphLibraryInterface
{


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphLibraryInterface#traditionalChart(java.lang.String, net.myorb.charting.DisplayGraphLibraryInterface.Portions)
	 */
	public void traditionalChart
		(
			String styleName,
			Portions portions
		)
	{
		throw new RuntimeException ("Feature not implemented");
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphLibraryInterface#regressionPlot(net.myorb.charting.DisplayGraphTypes.Point.Series, net.myorb.charting.DisplayGraphTypes.Point.Series, java.lang.String)
	 */
	public void regressionPlot (Point.Series dataPoints, Point.Series funcPlot, String title)
	{
		regressionPlot
		(
			"RED", dataPoints,
			"BLUE", funcPlot,
			title
		);
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphLibraryInterface#barChart(net.myorb.charting.DisplayGraphTypes.Point.Series, java.lang.String)
	 */
	public void barChart (Point.Series funcPlot, String title)
	{
		barChart ("BLUE", funcPlot, title);
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphLibraryInterface#singlePlotWithAxis(java.awt.Color, net.myorb.charting.DisplayGraphTypes.PlotCollection, java.lang.String, java.lang.String, net.myorb.charting.DisplayGraphTypes.RealFunction)
	 */
	public void singlePlotWithAxis
		(
			Color color, PlotCollection funcPlot,
			String functionName, String parameter,
			RealFunction f
		)
	{
		String profile = Subroutine.formatFullFormalProfile
			(functionName, ExpressionGraphing.makeStringsList (parameter));
		plotWithAxis (makeColorList (Color.BLUE), funcPlot, profile, profile, f);
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphLibraryInterface#multiPlotWithAxis(net.myorb.charting.DisplayGraphTypes.Colors, net.myorb.charting.DisplayGraphTypes.PlotCollection, java.lang.String, java.lang.String, net.myorb.charting.DisplayGraphTypes.RealFunction)
	 */
	public void multiPlotWithAxis
		(
			Colors colors,
			PlotCollection funcPlot, String title, String expression,
			RealFunction f
		)
	{
		plotWithAxis (colors, funcPlot, title, expression, f);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphLibraryInterface#multiPlotWithAxis(net.myorb.charting.DisplayGraphTypes.Colors, net.myorb.charting.DisplayGraphTypes.PlotCollection, java.lang.String, net.myorb.math.expressions.charting.MouseSampleTrigger)
	 */
	public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlot, String title,
			MouseSampleTrigger <?> trigger
		)
	{
		plotWithAxis (colors, funcPlot, title, trigger);
	}


}

