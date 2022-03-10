
package net.myorb.math.computational.splines;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;

import net.myorb.math.expressions.ExpressionComponentSpaceManager;

import net.myorb.math.GeneratingFunctions;

/**
 * implement mechanisms of Chebyshev T-Polynomial spline algorithms
 * @author Michael Druckman
 */
public class ChebyshevSpline implements SplineMechanisms
{


	public static final double SPLINE_LO = -1.5, SPLINE_HI = 1.5;
	public static final double SPLINE_RANGE = SPLINE_HI - SPLINE_LO;
	public static final int SPLINE_TICKS = 31;


	public ChebyshevSpline
		(
			ExpressionComponentSpaceManager<?> mgr
		)
	{
		this.setPolynomialManager (mgr);
	}


	@SuppressWarnings("unchecked")
	public void setPolynomialManager (ExpressionComponentSpaceManager<?> mgr)
	{ this.polynomial = new ChebyshevPolynomial<Double> (mgr.getComponentManager ()); }
	protected ChebyshevPolynomial<Double> polynomial;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#getSplineOptimalLo()
	 */
	public double getSplineOptimalLo ()
	{
		return SPLINE_LO;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#evalSplineAt(double, net.myorb.math.GeneratingFunctions.Coefficients)
	 */
	public double evalSplineAt (double x, GeneratingFunctions.Coefficients<Double> coefficients)
	{
		return polynomial.evaluatePolynomialV
				(
					coefficients, 
					polynomial.forValue (x)
				).getUnderlying ();
	}


}

