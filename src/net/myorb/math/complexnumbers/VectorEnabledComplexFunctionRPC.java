
package net.myorb.math.complexnumbers;

import net.myorb.math.expressions.TypedRangeDescription.TypedRangeProperties;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.charting.DisplayGraphTypes.Point.Series;
import net.myorb.charting.DisplayGraphTypes.Point;

import java.util.List;

/**
 * helper methods for forming complex number RPCs
 * @author Michael Druckman
 */
public class VectorEnabledComplexFunctionRPC
{


	/**
	 * this is the evaluation request as an abstract
	 *  with a default behavior throwing a RuntimeException.
	 *  the exception will be the result if no override is provided.
	 * @param lo the lo value of the vector, the starting point
	 * @param hi the hi value of the vector, the ending point
	 * @param inc the increment to use for the domain
	 * @return computed list of values
	 */
	public List<ComplexValue<Double>> evaluateRequestUsing
		(
			ComplexValue<Double> lo,
			ComplexValue<Double> hi,
			ComplexValue<Double> inc
		)
	{
		throw new RuntimeException ("RPC evaluation not implemented");
	}


	/**
	 * construct graph point series
	 *  for complex values with separate R/I components
	 * @param domainDescription the parameters of the real domain
	 * @param series the series of points per component to be constructed
	 * @param environment the control state of the simulation
	 */
	public void evaluateSeries
		(
			TypedRangeProperties<ComplexValue<Double>> domainDescription,
			List<Series> series, Environment<ComplexValue<Double>> environment
		)
	{
		evaluateSeries
		(
			domainDescription.getTypedLo (), domainDescription.getTypedHi (),
			domainDescription.getTypedIncrement (),
			series
		);
	}


	/**
	 * define the domain and request the evaluation.
	 *  values returned from the RPC will populate the series.
	 * @param lo the lo value of the vector, the starting point
	 * @param hi the hi value of the vector, the ending point
	 * @param inc the increment to use for the domain
	 * @param series the series collecting values
	 */
	public void evaluateSeries
		(
			ComplexValue<Double> lo,
			ComplexValue<Double> hi,
			ComplexValue<Double> inc,
			List<Series> series
		)
	{
		List<ComplexValue<Double>> values =
			evaluateRequestUsing (lo, hi, inc);
		buildSeries (values, lo, inc, series);
	}


	/**
	 * build the series list of points with R/I values
	 * @param values the list of complex values computed
	 * @param lo the lo value of the vector, the starting point
	 * @param inc the increment to use for the domain
	 * @param series the series collecting output
	 */
	public static void buildSeries
		(
			List<ComplexValue<Double>> values,
			ComplexValue<Double> lo, ComplexValue<Double> inc,
			List<Series> series
		)
	{
		double x, delta;
		if (isOnImagAxis (inc))
		{ x = lo.Im (); delta = inc.Im (); }
		else { x = lo.Re (); delta = inc.Re (); }

		List<Point> r = series.get (0), i = series.get (1);

		for (ComplexValue<Double> z : values)
		{
			r.add (new Point (x, z.Re ()));
			i.add (new Point (x, z.Im ()));
			x += delta;
		}
	}


	/**
	 * compute the count of points, inc==null implies 1
	 * @param lo the lo value of the vector, the starting point
	 * @param hi the hi value of the vector, the ending point
	 * @param inc the increment to use for the domain
	 * @return the count of points
	 */
	public static int count
		(
			ComplexValue<Double> lo,
			ComplexValue<Double> hi,
			ComplexValue<Double> inc
		)
	{
		if (inc != null)
		{
			double count;
			if (isOnImagAxis (inc))
			{ count = ((hi.Im () - lo.Im ()) / inc.Im ()); }
			else { count = ((hi.Re () - lo.Re ()) / inc.Re ()); }
			return (int) count;
		} else return 1;
	}


	/**
	 * include the components of a point in the buffer.
	 *  point may be null, this implies a (1,0) increment.
	 * @param point a complex value to treat as a point with R/I components
	 * @param to the StringBuffer building the request string
	 * @return the StringBuffer for chaining
	 */
	public static StringBuffer add
	(ComplexValue<Double> point, StringBuffer to)
	{
		double r = 1.0, i = 0.0;
		if (point != null) { r = point.Re (); i = point.Im (); }
		to.append (r).append (", ").append (i);
		return to;
	}


	/**
	 * the axis focus is necessarily the non-zero component of increment
	 * @param increment the increment between subsequent points of the evaluation
	 * @return TRUE for increment moving along IMAG axis, FALSE if REAL
	 */
	public static boolean isOnImagAxis (ComplexValue<Double> increment)
	{
		return increment.Re () == 0.0;
	}


}

