
package net.myorb.math.expressions.charting.implementations;

// CalcLib charting
import net.myorb.charting.PlotLegend;
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes.Colors;
import net.myorb.charting.DisplayGraphTypes.RealFunction;
import net.myorb.charting.DisplayGraphTypes.PlotCollection;
import net.myorb.charting.DisplayGraphUtil;

//CalcLib expressions
import net.myorb.math.expressions.charting.ExpressionGraphing;
import net.myorb.math.expressions.charting.DisplayGraphPrimitives;
import net.myorb.math.expressions.charting.DisplayGraphLibraryInterface;
import net.myorb.math.expressions.charting.MultiSegmentUtilities;
import net.myorb.math.expressions.charting.MouseSampleTrigger;

//CalcLib UDF support
import net.myorb.math.expressions.evaluationstates.Subroutine;

//Chart Providers
import net.myorb.jfree.ChartLibSupport;
import net.myorb.jfree.BarCharts;

//JRE swing
import javax.swing.JComponent;

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
	 * helper methods
	 */


	public MultiSegmentUtilities.SegmentManager getSegmentManager
				(MouseSampleTrigger <?> trigger)
	{
		return MultiSegmentUtilities.getSegmentUtilities (trigger);
	}

	public MultiSegmentUtilities.SegmentManager getSegmentManager
		(MouseSampleTrigger <?> trigger, String expression)
	{
		MultiSegmentUtilities.SegmentManager mgr =
			getSegmentManager (trigger);
		mgr.setExprs (new String[]{expression});
		return mgr;
	}

	public void show (String title, JComponent display)
	{
		DisplayGraphPrimitives.showFrame
			(title, display);
	}


	/*
	 * axis chart components based on segment infrastructure
	 */


	/**
	 * build the display component
	 * @param title title for the chart
	 * @param funcPlots collection of plots to include
	 * @param colors display choices for the plots
	 * @param mgr segment utilities manager
	 * @return Swing component
	 */
	public JComponent axisForSegments
		(
			String title, PlotCollection funcPlots, Colors colors,
			MultiSegmentUtilities.SegmentManager mgr
		)
	{
		return axisChart
		(
			title, funcPlots,
			mgr.getExprs (), mgr.getVar (),
			mgr.getAxisDisplay (),
			colors
		);
	}


	/*
	 * multiple plots against axis
	 * - implementation of DisplayGraphLibraryInterface
	 */


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphPrimitives#multiPlotWithAxis(net.myorb.math.expressions.charting.DisplayGraphAtomic.Colors, net.myorb.math.expressions.charting.DisplayGraphAtomic.PlotCollection, java.lang.String, net.myorb.math.expressions.charting.MouseSampleTrigger)
	 */
	public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlots,
			String title, MouseSampleTrigger <?> trigger
		)
	{
		show
		(
			title,

			axisForSegments
			(
				title, funcPlots, colors,
				getSegmentManager (trigger)
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
		show
		(
			title,

			axisForSegments
			(
				title, funcPlots, colors,
				getSegmentManager (null, expression)
			)
		);
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

		show
		(
			title,

			axisChart
			(
				title, functionPlot, new String[]{profile},
				parameter, profile, PlotLegend.getColorList ()
			)
		);
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
    	show (title, regressionPlotComponent (dataPoints, funcPlot, title));
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
		show (title, BarCharts.makeBarChartComponent (title, levels));
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
		show (null, BarCharts.makeChartComponent (styleName, null, portions));
	}


}

