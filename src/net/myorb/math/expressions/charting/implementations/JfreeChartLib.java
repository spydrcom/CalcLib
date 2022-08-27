
package net.myorb.math.expressions.charting.implementations;

// CalcLib charting
import net.myorb.charting.PlotLegend;
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes.Colors;
import net.myorb.charting.DisplayGraphTypes.RealFunction;
import net.myorb.charting.DisplayGraphTypes.PlotCollection;
import net.myorb.charting.DisplayGraphUtil;

import net.myorb.math.expressions.charting.DisplayGraphLibraryInterface;
import net.myorb.math.expressions.charting.MultiSegmentUtilities;

//CalcLib expressions
import net.myorb.math.expressions.charting.ExpressionGraphing;
import net.myorb.math.expressions.charting.DisplayGraphPrimitives;
import net.myorb.math.expressions.charting.MouseSampleTrigger;

import net.myorb.math.expressions.evaluationstates.Subroutine;

//Chart Providers
import net.myorb.jfree.ChartLibSupport;
import net.myorb.jfree.BarCharts;

// JRE AWT
import java.awt.Color;

/**
 * an implementation of the chart library using JFreeChart
 * @author Michael Druckman
 */
public class JfreeChartLib extends ChartLibSupport
	implements DisplayGraphLibraryInterface
{


    /*
     * line plots for multiple functions using JFreeChart
     */


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphPrimitives#multiPlotWithAxis(net.myorb.math.expressions.charting.DisplayGraphAtomic.Colors, net.myorb.math.expressions.charting.DisplayGraphAtomic.PlotCollection, java.lang.String, net.myorb.math.expressions.charting.MouseSampleTrigger)
	 */
	@SuppressWarnings("rawtypes")
	public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlots,
			String title, MouseSampleTrigger trigger
		)
	{
		MultiSegmentUtilities.SegmentManager mgr =
			new MultiSegmentUtilities.SegmentManager ();
		mgr.examine (trigger);

		DisplayGraphPrimitives.showFrame
		(
			title,

			axisChart
			(
				title, funcPlots,
				mgr.getExprs (), mgr.getVar (),
				mgr.getAxisDisplay (),
				colors
			)
		);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphPrimitives#multiPlotWithAxis(net.myorb.math.expressions.charting.DisplayGraphAtomic.Colors, net.myorb.math.expressions.charting.DisplayGraphAtomic.PlotCollection, java.lang.String, java.lang.String, net.myorb.math.expressions.charting.DisplayGraphAtomic.RealFunction)
	 */
	public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlots, String title,
			String expression, RealFunction f
		)
	{
		DisplayGraphPrimitives.showFrame (title, axisChart (title, funcPlots, new String[]{expression}, "X", "f(X)", colors));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphPrimitives#singlePlotWithAxis(java.awt.Color, net.myorb.math.expressions.charting.DisplayGraphAtomic.PlotCollection, java.lang.String, java.lang.String, net.myorb.math.expressions.charting.DisplayGraphAtomic.RealFunction)
	 */
	public void singlePlotWithAxis
		(
			Color color, PlotCollection functionPlot,
			String functionName, String parameter, RealFunction f
		)
	{
		String profile = Subroutine.formatFullFormalProfile
				(functionName, ExpressionGraphing.makeStringsList (parameter));
		String title = DisplayGraphUtil.titleFor (profile, parameter, functionPlot.get (0));
		DisplayGraphPrimitives.showFrame (title, axisChart (title, functionPlot, new String[]{profile}, parameter, profile, PlotLegend.getColorList ()));
	}


    /*
     * regression plots using JFreeChart
     */


    /* (non-Javadoc)
     * @see net.myorb.math.expressions.charting.DisplayGraphPrimitives#regressionPlot(net.myorb.math.expressions.charting.DisplayGraphAtomic.Point.Series, net.myorb.math.expressions.charting.DisplayGraphAtomic.Point.Series, java.lang.String)
     */
    public void regressionPlot
		(
			Point.Series dataPoints,
			Point.Series funcPlot,
			String title
		)
	{
    	DisplayGraphPrimitives.showFrame (title, regressionPlotComponent (dataPoints, funcPlot, title));
	}


	/*
     * bar charts using JFreeChart
     */


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphPrimitives#barChart(net.myorb.math.expressions.charting.DisplayGraphAtomic.Point.Series, java.lang.String)
	 */
	public void barChart
		(
			Point.Series levels,
			String title
		)
	{
		DisplayGraphPrimitives.showFrame (title, BarCharts.makeBarChartComponent (title, levels));
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphLibraryInterface#traditionalChart(java.lang.String, net.myorb.math.expressions.charting.DisplayGraphLibraryInterface.Portions)
	 */
	public void traditionalChart
		(
			String styleName,
			Portions portions
		)
	{
		DisplayGraphPrimitives.showFrame (null, BarCharts.makeChartComponent (styleName, null, portions));
	}


}

