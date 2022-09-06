
package net.myorb.math.expressions.charting;

import net.myorb.charting.DisplayGraphTypes;
import net.myorb.charting.DisplayGraphSegmentTools;

import net.myorb.math.expressions.TypedRangeDescription;
import net.myorb.math.expressions.evaluationstates.Arrays;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.DataConversions;

import net.myorb.gui.components.DisplayFrame;

import net.myorb.data.abstractions.PrimitiveRangeDescription;
import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;
import net.myorb.data.abstractions.Function;

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * provide low-level properties and methods for supporting plots, graphs, and charts
 * @author Michael Druckman
 */
public class DisplayGraphAtomic extends DisplayGraphTypes
{


	public static final int DEFAULT_PLOT_SIZE = DisplayFrame.DEFAULT_DISPLAY_AREA_SIZE;
	public static Color getColor (String named) { return Color.getColor (named); }


	/*
	 * access to charting library as specified in configuration
	 */


	protected static DisplayGraphLibraryInterface displayLibrary = new DisplayGraphPrimitives ();
	public static void setActiveChartLibrary (DisplayGraphLibraryInterface activeDisplayLibrary) { displayLibrary = activeDisplayLibrary; }
	public static DisplayGraphLibraryInterface getChartLibrary () { return displayLibrary; }


	/*
	 * plot lists, collections, and series
	 */


	/**
	 * build points series from 2D sequences
	 * @param points a 2D data sequence describing the plot
	 * @return the point series
	 */
	public static Point.Series
		forRealSequence (DataSequence2D<Double> points)
	{ return DisplayGraphSegmentTools.checkRange ((Point.Series) points.addTo (new PointCollection ())); }


	/**
	 * build points series from axis sequences
	 * @param x the sequence describing the x-axis
	 * @param y the sequence describing the y-axis
	 * @return the point series
	 */
	public static Point.Series forRealSequence
		(DataSequence<Double> x, DataSequence<Double> y)
	{ return forRealSequence (new DataSequence2D<Double> (x, y)); }


	/**
	 * construct list of points from domain and range
	 * @param domain the list of x-axis value to be graphed
	 * @param range the list of y-axis value to be graphed
	 * @return the list of points
	 */
	public static Point.Series pointsFor (RealSeries domain, RealSeries range)
	{ return forRealSequence (new DataSequence2D<Double> (domain, range).corrected ()); }


	/**
	 * evaluate function at each point of domain
	 * @param domain the list of domain values
	 * @param f the function being plotted
	 * @return the list of points
	 */
	public static Point.Series pointsFor (RealSeries domain, Function<Double> f)
	{ return forRealSequence (DataSequence2D.collectDataFor (f, domain)); }


	/**
	 * evaluate function at each point of domain
	 * @param domain the list of domain values
	 * @param f the function being plotted
	 * @return the list of points
	 */
	public static PlotCollection getPlotList
		(RealSeries domain, Function<Double> f)
	{ return makePlotCollection (pointsFor (domain, f)); }


	/**
	 * build plot points for a
	 *  function over a segment of x-axis
	 * @param f the function being plotted
	 * @param lo the low value of x for the domain
	 * @param hi the high value of x for the domain
	 * @param inc the increment value for the domain
	 * @return the list of points
	 */
	public static PlotCollection getPlotList
	(Function<Double> f, double lo, double hi, double inc)
	{ return getPlotList (domain (lo, hi, inc), f); }


	/**
	 * list points of function curve
	 * @param f the function being plotted
	 * @param lo the low value of x for the domain
	 * @param hi the high value of x for the domain
	 * @param inc the increment value for the domain
	 * @return the list of points
	 */
	public static Point.Series getPlotPoints
	(Function<Double> f, double lo, double hi, double inc)
	{ return pointsFor (domain (lo, hi, inc), f); }


	/**
	 * list points of function curve
	 * @param <T> the data type being managed in properties
	 * @param f real wrapper for the function being plotted
	 * @param properties a description of the domain
	 * @param manager a manager for the data type
	 * @return the plot list
	 */
	public static <T> PlotCollection getPlotList
		(
			Function<Double> f,
			TypedRangeDescription.TypedRangeProperties<T> properties,
			ExpressionSpaceManager<T> manager
		)
	{
		return getPlotList (getDomain (properties, manager), f);
	}


	/**
	 * generate a point series from a data sequence
	 * @param points the 2D set of points as a sequence
	 * @return a point series with the points matching the sequence
	 * @param <T> the data type of the domain
	 */
	public static <T> Point.Series forSequence (DataSequence2D<T> points)
	{
		ExpressionSpaceManager<T> mgr =
		(ExpressionSpaceManager<T>) points.getSpaceManager ();
		DataConversions<T> conversion = mgr.getDataConversions ();
		DataSequence<Double> x = conversion.convertToSeries (points.xAxis),
				y = conversion.convertToSeries (points.yAxis);
		return forRealSequence (x, y);
	}


	/**
	 * run full point series for function
	 *  over a domain of evenly spaced points
	 * @param f a function of the domain type
	 * @param lo the lo end value of the domain span
	 * @param hi the hi end value of the domain span
	 * @param inc the increment between elements of the series
	 * @return a point series with the points matching the sequence
	 * @param <T> the data type of the domain
	 */
	public static <T> Point.Series pointsFor (Function<T> f, T lo, T hi, T inc)
	{
		return forSequence (DataSequence2D.collectDataFor (f, lo, hi, inc));
	}


	/*
	 * domain constructors
	 */


	/**
	 * generate domain using data sequence
	 * @param lo the lo end value of the domain span
	 * @param hi the hi end value of the domain span
	 * @param inc the increment between elements of the series
	 * @param mgr a type manager for the data
	 * @param <T> the data type of the domain
	 * @return a sequence holding the domain
	 */
	public static <T> DataSequence<T> domain
		(T lo, T hi, T inc, ExpressionSpaceManager<T> mgr)
	{
		return DataSequence.evenlySpaced (lo, hi, inc, mgr);
	}


	/**
	 * build a domain from an array descriptor
	 * @param descriptor description of the domain values
	 * @param mgr a type manager for the data
	 * @param <T> the data type of the domain
	 * @return a sequence holding the domain
	 */
	public static <T> DataSequence <T> domain
		(Arrays.Descriptor<T> descriptor, ExpressionSpaceManager<T> mgr)
	{
		return DataSequence.evenlySpaced
				(
					descriptor.getLo (), descriptor.getHi (),
					descriptor.getDelta (), mgr
				);
	}


	/**
	 * provide a domain value list
	 * @param lo the low value for the domain
	 * @param hi the high value for the domain
	 * @param inc the increment value to use
	 * @return the list of domain values
	 */
	public static RealSeries domain
		(double lo, double hi, double inc)
	{
		double threshold = hi + inc / 2;
		RealSeries list = new RealSeries ();
		for (double x = lo; x <= threshold; x += inc)
		{ list.add (x); }
		return list;
	}


	/**
	 * build domain from Typed Range Properties
	 * @param <T> the data type being managed in properties
	 * @param properties the source TypedRangeProperties object
	 * @param manager a manager for the data type
	 * @return the domain values list
	 */
	public static <T> RealSeries getDomain
	(TypedRangeDescription.TypedRangeProperties<T> properties, ExpressionSpaceManager<T> manager)
	{ return domain (TypedRangeDescription.toPrimitiveRangeDescription (properties, manager)); }


	/**
	 * list domain values from range description
	 * @param range description of the domain values
	 * @return list of domain value
	 */
	public static RealSeries domain (PrimitiveRangeDescription range)
	{
		return domain (range.getLo ().doubleValue (), range.getHi ().doubleValue (), range.getIncrement ().doubleValue ());
	}


	/**
	 * list domain values from array description
	 * @param <T> type manager for descriptor data
	 * @param arrayDescriptor descriptor for array
	 * @param conversion data conversion object for type
	 * @return list of floating domain values
	 */
	public static <T> RealSeries domain (Arrays.Descriptor<T> arrayDescriptor, DataConversions<T> conversion)
	{
		return domain
		(
			conversion.toDouble (arrayDescriptor.getLo ()), conversion.toDouble (arrayDescriptor.getHi ()), 
			conversion.toDouble (arrayDescriptor.getDelta ())
		);
	}


	/*
	 * low level primitives
	 */


	/**
	 * determine span of values of points list
	 * @param points the list of points
	 * @return a range descriptor
	 */
	public static PrimitiveRangeDescription pointsDomainSpan (Point.Series points)
	{
		double lo = points.get (0).x, hi = points.get (points.size()-1).x;
		return new PrimitiveRangeDescription (lo, hi, 0);
	}
	public static PrimitiveRangeDescription pointsRangeSpan (Point.Series points)
	{
		double lo = points.get (0).y, hi = points.get (points.size()-1).y;
		return new PrimitiveRangeDescription (lo, hi, 0);
	}


	/**
	 * adjust scaling to add 2% to both X and Y axis
	 * @param scalingFactors the point holding scaling factors
	 */
	public static void add2Percent (Point scalingFactors)
	{
		scalingFactors.x *= 1.04; scalingFactors.y *= 1.04;
	}


	/**
	 * compile list of plots
	 * @param plots the plots being assembled
	 * @return list of the plots
	 */
	public static PlotCollection makePlotCollection (Point.Series... plots)
	{
		PlotCollection collection = new PlotCollection (), segments = new PlotCollection ();
		for (Point.Series series : plots) collection.add (DisplayGraphSegmentTools.checkRange (series));
		DisplayGraphSegmentTools.separateSegments (collection, segments);
		return segments;
	}


	/**
	 * compile list of colors
	 * @param color the colors being assembled
	 * @return list of the colors
	 */
	public static Colors makeColorList (Color... color)
	{
		Colors list = new Colors ();
		for (Color c : color) list.add (c);
		return list;
	}


	/**
	 * compile list of strings
	 * @param strings the strings being assembled
	 * @return list of the strings
	 */
	public static List<String> makeStringsList (String... strings)
	{
		List<String> list = new ArrayList<String>();
		for (String s : strings) list.add (s);
		return list;
	}


}

