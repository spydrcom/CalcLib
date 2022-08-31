
package net.myorb.math.expressions.charting.implementations;

import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes.Point.Series;
import net.myorb.charting.DisplayGraphTypes.PlotCollection;

import net.myorb.charting.DisplayGraphTypes.RealFunction;
import net.myorb.charting.DisplayGraphTypes.Colors;

import net.myorb.math.expressions.charting.DisplayGraphLibraryInterface;
import net.myorb.math.expressions.charting.MouseSampleTrigger;
import net.myorb.math.expressions.charting.MultiSegmentUtilities;

import com.xeiam.xchart.SeriesMarker;
import com.xeiam.xchart.XChartPanel;
import com.xeiam.xchart.Chart;

import javax.swing.JPanel;
import java.awt.Color;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * an implementation of the chart library using XChart
 * @author Michael Druckman
 */
public class XChartLib
	implements DisplayGraphLibraryInterface
{


	/**
	 * Creates a XChart object
	 * 
	 * @param chartTitle
	 *            the Chart title
	 * @param xTitle
	 *            The X-Axis title to show
	 * @param yTitle
	 *            The Y-Axis title to show
	 * @param seriesNames
	 *            The name of the series
	 * @param xData
	 *            A Collection containing the X-Axis data
	 * @param yData
	 *            A Collection containing Y-Axis data
	 * @return a Chart Object
	 */
	public static JPanel getChart
		(
			String chartTitle, String xTitle, String yTitle, List<String> seriesNames,
			List<Collection<Number>> xData, List<Collection<Number>> yData
		)
	{
		// Create Chart
		Chart chart = new Chart(600, 400);

		// Customize Chart
		chart.setChartTitle(chartTitle);
		chart.setXAxisTitle(xTitle); chart.setYAxisTitle(yTitle);

		// add each series to the chart
		for (int i = 0; i < seriesNames.size(); i++)
		{
			chart.addSeries
			(
				seriesNames.get(i), xData.get(i), yData.get(i)
			)
			.setMarker(SeriesMarker.NONE);
		}

		// wrap as swing component
		return new XChartPanel (chart);
	}


	/**
	 * prepare data to build an XChart from
	 * @param chartTitle a title for the chart
	 * @param xTitle a label to describe the x axis
	 * @param yTitle a label to describe the y axis
	 * @param seriesNames a list of the names of the series
	 * @param funcPlot the collection of plot points
	 * @return the swing component holding the chart
	 */
	public static JPanel getChart
		(
			String chartTitle,
			String xTitle, String yTitle, List<String> seriesNames,
			PlotCollection funcPlot
		)
	{
		List<Collection<Number>>
		xData = new ArrayList<Collection<Number>>(),
		yData = new ArrayList<Collection<Number>>();
		List<Number> xAxis, yAxis;

		for (Point.Series s : funcPlot)
		{
			xAxis = new ArrayList<Number>();
			yAxis = new ArrayList<Number>();
			s.toCoordinateLists (xAxis, yAxis);
			xData.add (xAxis); yData.add (yAxis);
		}
		
		return getChart (chartTitle, xTitle, yTitle, seriesNames, xData, yData);
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphLibraryInterface#traditionalChart(java.lang.String, net.myorb.charting.DisplayGraphLibraryInterface.Portions)
	 */
	@Override
	public void traditionalChart
		(
			String styleName, Portions portions
		)
	{
		throw new RuntimeException ("Unimplemented feature: XChart traditional plot");
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphLibraryInterface#regressionPlot(net.myorb.charting.DisplayGraphTypes.Point.Series, net.myorb.charting.DisplayGraphTypes.Point.Series, java.lang.String)
	 */
	@Override
	public void regressionPlot
		(
			Series dataPoints, Series funcPlot, String title
		)
	{
		throw new RuntimeException ("Unimplemented feature: XChart regression plot");
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphLibraryInterface#barChart(net.myorb.charting.DisplayGraphTypes.Point.Series, java.lang.String)
	 */
	@Override
	public void barChart
		(Series funcPlot, String title)
	{
		throw new RuntimeException ("Unimplemented feature: XChart bar chart");
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphLibraryInterface#singlePlotWithAxis(java.awt.Color, net.myorb.charting.DisplayGraphTypes.PlotCollection, java.lang.String, java.lang.String, net.myorb.charting.DisplayGraphTypes.RealFunction)
	 */
	@Override
	public void singlePlotWithAxis
		(
			Color color, PlotCollection funcPlot,
			String functionName, String parameter,
			RealFunction f
		)
	{

		String profile = functionName + " (" + parameter + ")";
		List<String> seriesNames = new ArrayList<String>();
		seriesNames.add (functionName);

		JPanel panel = getChart
		(
			functionName, parameter, profile, seriesNames, funcPlot
		);
		ChartHelpers.show (functionName, panel);

		//throw new RuntimeException ("Unimplemented feature: XChart axis plot");
	}


	/* (non-Javadoc)
	 * @see net.myorb.charting.DisplayGraphLibraryInterface#multiPlotWithAxis(net.myorb.charting.DisplayGraphTypes.Colors, net.myorb.charting.DisplayGraphTypes.PlotCollection, java.lang.String, java.lang.String, net.myorb.charting.DisplayGraphTypes.RealFunction)
	 */
	@Override
	public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlot,
			String title, String expression,
			RealFunction f
		)
	{

		List<String> seriesNames = new ArrayList<String>();
		seriesNames.add (expression);

		JPanel panel = getChart
		(
			title, "x", "f (x)", seriesNames, funcPlot
		);
		ChartHelpers.show (title, panel);

		//throw new RuntimeException ("Unimplemented feature: XChart axis plot w/ expression");
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.charting.DisplayGraphLibraryInterface#multiPlotWithAxis(net.myorb.charting.DisplayGraphTypes.Colors, net.myorb.charting.DisplayGraphTypes.PlotCollection, java.lang.String, net.myorb.math.expressions.charting.MouseSampleTrigger)
	 */
	@Override
	public void multiPlotWithAxis
		(
			Colors colors, PlotCollection funcPlot,
			String title, MouseSampleTrigger <?> trigger
		)
	{

		MultiSegmentUtilities.SegmentManager mgr =
				ChartHelpers.getSegmentManager (trigger);
		JPanel panel = getChart
		(
			title, mgr.getVar (), mgr.getAxisDisplay (),
			getSeriesNames (mgr.getExprs ()), funcPlot
		);
		ChartHelpers.show (title, panel);

		//throw new RuntimeException ("Unimplemented feature: XChart axis plot w/ trigger");
	}

	List<String> getSeriesNames (String[] exprs)
	{
		List<String> seriesNames = new ArrayList<String>();
		for (String expr : exprs) seriesNames.add (expr);
		return seriesNames;
	}


}

