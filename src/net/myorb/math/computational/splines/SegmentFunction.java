
package net.myorb.math.computational.splines;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.data.abstractions.SpaceDescription;

import net.myorb.math.GeneratingFunctions;
import net.myorb.math.SpaceManager;
import net.myorb.math.Function;

/**
 * describe a function that maps the curve of best fit to a spline segment
 * @param <T> type on which operations are to be executed
 * @author Michael Druckman
 */
public class SegmentFunction<T> implements Function<T>
{


	public SegmentFunction
		(
			SegmentRepresentation representation,
			ExpressionComponentSpaceManager<T> mgr,
			SplineMechanisms spline
		)
	{
		this.spline = spline;
		this.representation = representation;
		this.mgr = mgr;
	}
	protected ExpressionComponentSpaceManager<T> mgr;
	protected SegmentRepresentation representation;
	protected SplineMechanisms spline;


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
	 * @param parameter the parameter value in coordinates of original function
	 * @return the parameter value in coordinates of the spline function
	 */
	public double translate (double parameter)
	{
		return spline.getSplineOptimalLo () + (parameter - representation.getSegmentLo ()) * representation.getUnitSlope ();
	}


	/**
	 * @param component index of the component being fit
	 * @return the list of polynomial Coefficients
	 */
	public GeneratingFunctions.Coefficients<Double>
			getCoefficients (int component)
	{
		GeneratingFunctions.Coefficients<Double> c =
			new GeneratingFunctions.Coefficients<Double>();
		c.addAll (representation.getCoefficientsFor (component));
		return c;
	}


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.ManagedSpace#getSpaceDescription()
	 */
	public SpaceDescription<T> getSpaceDescription () { return mgr; }
	public SpaceManager<T> getSpaceManager () { return mgr; }


}

