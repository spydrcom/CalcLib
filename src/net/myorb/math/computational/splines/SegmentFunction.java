
package net.myorb.math.computational.splines;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * describe a function that maps the curve of best fit to a spline segment
 * @param <T> data type on which operations are to be executed
 * @author Michael Druckman
 */
public class SegmentFunction <T> implements Function <T>
{


	public SegmentFunction
		(
			SegmentRepresentation representation,
			ExpressionComponentSpaceManager <T> mgr,
			SplineMechanisms spline
		)
	{
		this.spline = spline;
		this.representation = representation;
		this.flat = representation.getUnitSlope () == 0.0;
		this.mgr = mgr;
	}
	protected ExpressionComponentSpaceManager <T> mgr;
	protected SegmentRepresentation representation;
	protected SplineMechanisms spline;
	boolean flat;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		double realPart = translate (mgr.component (x, 0));
		double[] results = new double[mgr.getComponentCount ()];
		for (int c = 0; c < results.length; c++)
		{
			results[c] = spline.evalSplineAt (realPart, getCoefficients (c));
		}
		return mgr.construct (results);
	}


	/**
	 * compute integral for full segment range
	 * @return integral computed for full segment range
	 */
	public T evalIntegral ()
	{
		return evalIntegralOver
			(
				representation.getSegmentLo (),
				representation.getSegmentHi ()
			);
	}


	/**
	 * use the spline to compute the integral over a range
	 * @param lo the lo end of integral range in function coordinates
	 * @param hi the hi end of integral range in function coordinates
	 * @return the computed value of the integral for the specified range
	 */
	public T evalIntegralOver
		(
			double lo, double hi
		)
	{
		double slo = translate (lo), shi = translate (hi);
		double[] results = new double[mgr.getComponentCount ()];
		for (int c = 0; c < results.length; c++)
		{
			results[c] =
				representation.getSegmentSlope () *
				spline.evalIntegralOver (slo, shi, getCoefficients (c));
		}
		return mgr.construct (results);
	}


	/**
	 * compute portion of function integral covered by segment range
	 * @param lo the lo end of integral range in function coordinates
	 * @param hi the hi end of integral range in function coordinates
	 * @return the computed value of the integral contribution for the specified range
	 */
	public T evalIntegralContribution
		(
			double lo, double hi
		)
	{
		double
			segLo = representation.getSegmentLo (),
			segHi = representation.getSegmentHi ();
		if (lo > segHi || hi < segLo) return mgr.getZero ();

		double rngLo = lo, rngHi = hi;
		if (rngLo < segLo) rngLo = segLo;
		if (rngHi > segHi) rngHi = segHi;

		return evalIntegralOver (rngLo, rngHi);
	}


	/**
	 * @param parameter the parameter value in coordinates of original function
	 * @return the parameter value in coordinates of the spline function
	 */
	public double translate (double parameter)
	{
		if ( ! flat )							// translation is needed
		{
			return
				spline.getSplineOptimalLo () +
				representation.getUnitSlope () *
				(
					parameter - representation.getSegmentLo ()
				);
		}
		else return parameter;					// no translation needed
	}


	/**
	 * @param component index of the component being fit
	 * @return the list of polynomial Coefficients
	 */
	public GeneratingFunctions.Coefficients <Double>
			getCoefficients (int component)
	{
		GeneratingFunctions.Coefficients<Double> c =
			new GeneratingFunctions.Coefficients <Double> ();
		c.addAll (representation.getCoefficientsFor (component));
		return c;
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription <T> getSpaceDescription () { return mgr; }
	public SpaceManager <T> getSpaceManager () { return mgr; }


}

