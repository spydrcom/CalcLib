
package net.myorb.math.expressions.charting;

// computation
import net.myorb.math.computational.FFT;
import net.myorb.math.computational.Fourier;

// expression
import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;
import net.myorb.math.expressions.evaluationstates.Environment;
import net.myorb.math.expressions.*;

// charting
import net.myorb.charting.DisplayGraphUtil;
import net.myorb.charting.DisplayGraphTypes.Point;
import net.myorb.charting.DisplayGraphTypes.RealSeries;

// abstraction
import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.Function;
import net.myorb.math.*;

// JRE
import java.util.List;

/**
 * methods for building charts of regression operations
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class RegressionCharts<T>
{


	public RegressionCharts (Environment<T> environment)
	{
		this (environment.getSpaceManager ());
		this.conversion = environment.getConversionManager ();
		this.valueManager = environment.getValueManager ();
		this.valueStack = environment.getValueStack ();
		this.environment = environment;
	}
	public RegressionCharts (SpaceManager<T> spaceManager)
	{
		this.spaceManager = (ExpressionSpaceManager<T>) spaceManager;
	}
	protected Environment<T> environment;
	protected DataConversions<T> conversion;
	protected ExpressionSpaceManager<T> spaceManager;
	protected ValueManager<T> valueManager;
	protected ValueStack<T> valueStack;


	/**
	 * produce scatter plot
	 * @param xSymbol x-axis data
	 * @param ySymbol y-axis data
	 */
	public void scatter (String xSymbol, String ySymbol)
	{
		//System.out.println ("SCATTER " + xSymbol + ", " + ySymbol);

		List<T> xData = getSymbolList (xSymbol), yData = getSymbolList (ySymbol);
		DisplayGraph.Point.Series points = new DisplayGraph.Point.Series ();

		for (int i = 0; i < xData.size (); i++)
		{
			Double x = spaceManager.convertToDouble (xData.get (i));
			Double y = spaceManager.convertToDouble (yData.get (i));
			points.add (new DisplayGraph.Point (x, y));
		}

		DisplayGraph.pointPlot ("WHITE", points, xSymbol + " / " + ySymbol);
	}


	/**
	 * run FFT
	 * @param data array name
	 */
	public void transorm (String data)
	{
		//System.out.println ("FFT " + data);
		List<T> values = getSymbolList (data);
		RealSeries results = new RealSeries ();
		Fourier.TimeSeries series = conversion.newTimeSeries (values);
		new ListOperations<Double>(new ExpressionFloatingFieldManager ())
		.fillAppendingWith (results, 0.0, series.size()/2);

		//System.out.println (series);
		FFT.analysis (series, results);
		//System.out.println (results);

		for (Double v : results) System.out.println (v);
		
		RealSeries domain = DisplayGraphUtil.domain (0, results.size() - 1, 1);
		Point.Series points = DisplayGraphUtil.pointsFor (domain, results);
		DisplayGraph.getChartLibrary ().barChart (points, data);
	}


	/**
	 * chart data point with regression curve
	 * @param sequence the data points to plot
	 * @param f the regression function
	 * @param title a title for the frame
	 */
	public void chartRegression
		(
			DataSequence2D<T> sequence,
			Function<Double> f,
			String title
		)
	{
		Point.Series points = sequenceToPoints (sequence);
		Double x = points.get (0).x, lo = x, hi = x;

		for (Point p : points)
		{
			lo = p.x < lo? p.x: lo; hi = p.x > hi? p.x: hi;
		}

		Double inc = (hi - lo) / 100;
		Point.Series funcPlot = DisplayGraphUtil.getPlotPoints (f, lo, hi, inc);
		DisplayGraph.getChartLibrary ().regressionPlot (points, funcPlot, title);
	}


	/**
	 * convert sequence to list of points
	 * @param data the data sequence in the generic type
	 * @return a list of plot points
	 */
	public Point.Series sequenceToPoints (DataSequence2D<T> data)
	{
		Point.Series points = new Point.Series ();
		for (int i = 0; i < data.xAxis.size (); i++)
		{
			Double x = spaceManager.convertToDouble (data.xAxis.get (i));
			Double y = spaceManager.convertToDouble (data.yAxis.get (i));
			points.add (new Point (x, y));
		}
		return points;
	}


	/**
	 * plot data with regression
	 * @param dataPoints the data points to plot
	 * @param funcPlot the regression function plot points
	 * @param title a title for the frame
	 */
	public static void chartRegression
		(
				Point.Series dataPoints,
			Point.Series funcPlot,
			String title
		)
	{
		DisplayGraph.getChartLibrary ().regressionPlot (dataPoints, funcPlot, title);
	}


	/**
	 * get symbol contents
	 * @param name the name of the symbol
	 * @return a generic wrapper
	 */
	public ValueManager.GenericValue getSymbolData (String name)
	{
		return environment.getSymbolMap ().getValue (name);
	}
	public List<T> getSymbolList (String name)
	{
		return valueManager.toArray (getSymbolData (name));
	}


	/**
	 * harmonic regression chart
	 * @param data the original sequence of data points
	 * @param regressionSeries the computed harmonic series regression
	 */
	public static void harmonicPlot
	(DataSequence2D<Double> data, Fourier.Series regressionSeries)
	{
		DisplayGraph.getChartLibrary ().regressionPlot
		(
			sequenceFor (data),
			plotFor (regressionSeries, data.xAxis.size ()),
			"Harmonic X/Y Regression"
		);
	}
	public static Point.Series sequenceFor (DataSequence2D<Double> data)
	{
		Point.Series points = new Point.Series ();
		for (int x = 0; x < data.xAxis.size (); x++)
		{ points.add (new Point (data.xAxis.get (x), data.yAxis.get (x))); }
		return points;
	}


	/**
	 * harmonic regression chart
	 * @param timeSeries the original time series
	 * @param regressionSeries the computed harmonic series regression
	 */
	public static void harmonicPlot
	(Fourier.TimeSeries timeSeries, Fourier.Series regressionSeries)
	{
		DisplayGraph.getChartLibrary ().regressionPlot
		(
			sequenceFor (timeSeries),
			plotFor (regressionSeries, timeSeries.size ()),
			"Harmonic Time Series Regression"
		);
	}
	public static Point.Series plotFor (Fourier.Series series, int limit)
	{ return DisplayGraphUtil.getPlotPoints (series, -limit, limit, limit/100.0); }

	public static Point.Series sequenceFor (Fourier.TimeSeries series)
	{
		Point.Series points = new Point.Series ();
		for (double x = 1; x <= series.size(); x++) points.add (newPoint (x, series));
		return points;
	}
	public static Point newPoint (double x, Fourier.TimeSeries series)
	{ return new Point (x, series.get (((int)x) - 1)); }


}

