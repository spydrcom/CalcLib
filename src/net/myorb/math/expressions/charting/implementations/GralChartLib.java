
package net.myorb.math.expressions.charting.implementations;

// CalcLib expressions
import net.myorb.math.expressions.ConventionalNotations;
import net.myorb.math.expressions.evaluationstates.Subroutine;
import net.myorb.math.expressions.charting.DisplayGraphLibraryInterface;

// CalcLib display graph types
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes.PlotCollection;
import net.myorb.charting.DisplayGraphTypes.RealFunction;
import net.myorb.charting.DisplayGraphTypes.Colors;
import net.myorb.charting.DisplayGraphUtil;

//CalcLib charting
import net.myorb.math.expressions.charting.MouseSampleTrigger;
import net.myorb.math.expressions.charting.ExpressionGraphing;

//Chart Providers
import net.myorb.gral.ChartLibSupport;

// JRE AWT
import java.awt.Color;

/**
 * an implementation of the chart library using GRAL
 * @author Michael Druckman
 */
public class GralChartLib
	extends ChartLibSupport
	implements DisplayGraphLibraryInterface
{


	/*
     * function line plots using GRAL
     */


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphPrimitives#multiPlotWithAxis(net.myorb.math.expressions.charting.DisplayGraphAtomic.Colors, net.myorb.math.expressions.charting.DisplayGraphAtomic.PlotCollection, java.lang.String, java.lang.String, net.myorb.math.expressions.charting.DisplayGraphAtomic.RealFunction)
	 */
	public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlots, String title, String expression, RealFunction f
		)
	{
		showComponentFrame (title, axisChartComponent (funcPlots, title, new String[]{expression}, "X", "f(X)"));
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
		String exprs[] = trigger.getDisplay ().getPlotExpressions (),
			var = ConventionalNotations.determineNotationFor (trigger.getDisplay ().getVariable ());
		showComponentFrame (title, axisChartComponent (funcPlots, title, exprs, var, "f(" + var + ")"));
	}


    /*
     * regression plots using GRAL
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
    	showComponentFrame (title, cvtToRegression (dataPoints, funcPlot));
	}


	/*
     * bar charts using GRAL
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
		showComponentFrame (title, makeTransformChart (title, levels));
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
		showComponentFrame (null, componentFor (styleName, portions));
	}

}

