
package net.myorb.math.computational.splines;

import net.myorb.math.polynomial.families.ChebyshevPolynomial;
import net.myorb.math.polynomial.families.chebyshev.ChebyshevPolynomialCalculus;

import net.myorb.math.expressions.managers.ExpressionFloatingFieldManager;

import net.myorb.math.GeneratingFunctions;

/**
 * implement mechanisms of Chebyshev T-Polynomial spline algorithms
 * @author Michael Druckman
 */
public class ChebyshevSpline implements SplineMechanisms
{


	/**
	 * data type manager for real number domain space
	 */
	public static ExpressionFloatingFieldManager realManager = new ExpressionFloatingFieldManager ();


	/*
	 * constants that describe the optimal Chebyshev T-Polynomial Spline
	 */

	public static final double SPLINE_LO = -1.5, SPLINE_HI = 1.5;
	public static final double SPLINE_RANGE = SPLINE_HI - SPLINE_LO;
	public static final int SPLINE_TICKS = 31, SPLINE_SPACES = SPLINE_TICKS - 1;


	/**
	 * construct objects implementing Chebyshev Polynomial functionalities
	 */
	public ChebyshevSpline ()
	{
		this.calculus = new ChebyshevPolynomialCalculus <Double> (realManager);
		this.polynomial = new ChebyshevPolynomial <Double> (realManager);
	}


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
	public double evalSplineAt (double x, GeneratingFunctions.Coefficients <Double> coefficients)
	{
		return polynomial.evaluatePolynomialV
				(
					coefficients, 
					polynomial.forValue (x)
				).getUnderlying ();
	}
	protected ChebyshevPolynomial <Double> polynomial;


	/*
	 * polynomial calculus implementation
	 */

	public double evaluatePolynomialIntegral
		(
			GeneratingFunctions.Coefficients <Double> coefficients, 
			double at
		)
	{
		return calculus.evaluatePolynomialIntegral (coefficients, at);
	}
	public double evaluatePolynomialIntegral
		(
			GeneratingFunctions.Coefficients <Double> coefficients, 
			double lo, double hi
		)
	{
		return calculus.evaluatePolynomialIntegral (coefficients, lo, hi);
	}
	protected ChebyshevPolynomialCalculus <Double> calculus;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#evalIntegralOver(double, double, net.myorb.math.GeneratingFunctions.Coefficients)
	 */
	public double evalIntegralOver
		(
			double lo, double hi,
			GeneratingFunctions.Coefficients <Double> coefficients
		)
	{
		return evaluatePolynomialIntegral (coefficients, lo, hi);
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.splines.SplineMechanisms#getInterpreterPath()
	 */
	public String getInterpreterPath ()
	{
		return ChebyshevSpline.class.getCanonicalName ();
	}


}

