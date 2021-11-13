
package net.myorb.math.expressions.charting.implementations;

// CalcLib expressions
import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.evaluationstates.Subroutine;

// CalcLib display graph types
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes.PlotCollection;
import net.myorb.charting.DisplayGraphTypes.RealFunction;
import net.myorb.charting.DisplayGraphTypes.Colors;
import net.myorb.charting.DisplayGraphUtil;

//CalcLib charting
import net.myorb.math.expressions.charting.MouseSampleTrigger;
import net.myorb.math.expressions.charting.DisplayGraphLibraryInterface;
import net.myorb.math.expressions.charting.ExpressionGraphing;

//Chart Providers
import net.myorb.bfo.ChartLibSupport;
import net.myorb.bfo.BarAndPieSupport;

// JRE AWT
import java.awt.Color;

/**
 * an implementation of the chart library using BFO
 * @author Michael Druckman
 */
public class BfoChartLib
	extends ChartLibSupport
	implements DisplayGraphLibraryInterface
{


	/*
     * function line plots using BFO
     */


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphPrimitives#multiPlotWithAxis(net.myorb.math.expressions.charting.DisplayGraphAtomic.Colors, net.myorb.math.expressions.charting.DisplayGraphAtomic.PlotCollection, java.lang.String, java.lang.String, net.myorb.math.expressions.charting.DisplayGraphAtomic.RealFunction)
	 */
	public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlots, String title, String expression, RealFunction f
		)
	{
		if (TRACE) System.out.println ("multiPlotWithAxis f(X)");
		showImageFrame (title, axisChartDisplay (title, funcPlots, new String[]{expression}, "X", "f(X)"));
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
		if (TRACE) System.out.println ("singlePlotWithAxis");
		String profile = Subroutine.formatFullFormalProfile
			(functionName, ExpressionGraphing.makeStringsList (parameter));
		showPlot
		(
			DisplayGraphUtil.titleFor
				(profile, parameter, functionPlot.get (0)),
			functionPlot, profile, parameter
		);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphPrimitives#multiPlotWithAxis(net.myorb.math.expressions.charting.DisplayGraphAtomic.Colors, net.myorb.math.expressions.charting.DisplayGraphAtomic.PlotCollection, java.lang.String, net.myorb.math.expressions.charting.MouseSampleTrigger)
	 */
	@SuppressWarnings ("rawtypes") public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlots, String title, MouseSampleTrigger trigger
		)
	{
		if (TRACE) System.out.println ("multiPlotWithAxis");
		String exprs[] = trigger.getDisplay ().getPlotExpressions (),
			var = ConventionalNotations.determineNotationFor (trigger.getDisplay ().getVariable ());
		showImageFrame (title, axisChartDisplay (title, funcPlots, exprs, var, "f(" + var + ")"));
	}


    /*
     * regression plots using BFO
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
		if (TRACE) System.out.println ("regressionPlot");
		showImageFrame (title, cvtToRegression (dataPoints, funcPlot));
	}


	/*
     * bar charts using BFO
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
		if (TRACE) System.out.println ("barChart");
		showImageFrame (title, makeBarChart (title, levels));
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
		if (TRACE) System.out.println ("traditionalChart");
		try { showImageFrame (null, new BarAndPieSupport ().makeChart (styleName, portions)); }
		catch (Exception e) { e.printStackTrace(); }
	}
	static boolean TRACE = false;


}

