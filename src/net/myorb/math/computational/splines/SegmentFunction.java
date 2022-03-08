
package net.myorb.math.computational.splines;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;
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
			ExpressionComponentSpaceManager<T> mgr
		)
	{
		this.setPolynomialManager (mgr);
		this.representation = representation;
		this.mgr = mgr;
	}
	protected ExpressionComponentSpaceManager<T> mgr;
	protected SegmentRepresentation representation;


	@SuppressWarnings("unchecked")
	void setPolynomialManager (ExpressionComponentSpaceManager<T> mgr)
	{ this.polynomial = new ChebyshevPolynomial<Double> (mgr.getComponentManager ()); }
	protected ChebyshevPolynomial<Double> polynomial;


	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.Function#eval(java.lang.Object)
	 */
	public T eval (T x)
	{
		double realPart = translate (mgr.component (x, 0));
		double[] results = new double[mgr.getComponentCount ()];
		for (int c = 0; c < results.length; c++)
		{
			results[c] = polynomial.evaluatePolynomialV
				(
					getCoefficients (c), 
					polynomial.forValue (realPart)
				).getUnderlying ();
		}
		return mgr.construct (results);
	}


	/**
	 * @param parameter the parameter value in coordinates of original function
	 * @return the parameter value in coordinates of the spline function
	 */
	double translate (double parameter)
	{
		return SPLINE_LO + (parameter - representation.getSegmentLo ()) * representation.getUnitSlope ();
	}
	static final double SPLINE_LO = -1.5;


	/**
	 * @param component index of the component being fit
	 * @return the list of polynomial Coefficients
	 */
	GeneratingFunctions.Coefficients<Double> getCoefficients (int component)
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

