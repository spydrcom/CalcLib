
package net.myorb.math.computational;

import net.myorb.math.polynomial.OrdinaryPolynomialCalculus;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.data.abstractions.DataSequence;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

import java.util.List;

/**
 * Lagrange interpolation based on
 *  derivative of polynomial of X coordinate roots
 * @param <T> the underlying data type
 * @author Michael Druckman
 */
public class LagrangeInterpolationUsingCalculus<T> extends LagrangeInterpolation<T>
{


	/*
	 * 
	 * Lagrange Interpolation
	 * 
	 * Interpolation Polynomial:
	 * 
	 *                                   P(x) * y[j]
	 * Ln(x) = SUM [ 1 <= j <= n ] -------------------------
	 *                               P'(x[j]) * (x - x[j])
	 * 
	 * P(x) = (x - x[1]) ... (x - x[n])
	 * 
	 */


	/**
	 * @param poly the polynomial P(X)
	 * @return P'(X)
	 */
	public Polynomial.PowerFunction<T> derivativeOf (Polynomial.PowerFunction<T> poly)
	{
		return ordinaryPolynomialCalculus.getFunctionDerivative (poly);
	}


	/**
	 * @param xValues x[1], x[2], ..., x[n]
	 * @return (X-x[1])(X-x[2]) ... (X-x[n]))
	 */
	public Polynomial.PowerFunction<T> xPoly (List<T> xValues)
	{
		Polynomial.PowerFunction<T> p = polynomialSpaceManager.getOne ();
		for (T xn : xValues) p = polynomialSpaceManager.multiply (p, xMinus (xn));
		return p;
	}


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.LagrangeInterpolation#verifyDataSet(net.myorb.math.DataSequence2D)
	 */
	public void verifyDataSet (DataSequence2D<T> dataSet) throws RuntimeException
	{
		super.verifyDataSet (dataSet);
		polyOfX = xPoly (dataSet.xAxis);		// (X-x[1])(X-x[2])(X-x[3]) ... (X-x[n]))
		pPrime = derivativeOf (polyOfX);		// first derivative of the polynomial
	}
	protected Polynomial.PowerFunction<T> polyOfX, pPrime;


	/* (non-Javadoc)
	 * @see net.myorb.math.computational.LagrangeInterpolation#iThLagrangeTerm(int, net.myorb.math.DataSequence)
	 */
	public Polynomial.PowerFunction<T> iThLagrangeTerm (int i, DataSequence<T> xSamples)
	{
		T xval = xSamples.get (i), con = spaceManager.invert (pPrime.eval (xval));
		return polynomialSpaceManager.times (con, polynomialSpaceManager.divide (polyOfX, xMinus (xval), rem));
	}


	/**
	 * @param constant a constant value
	 * @return a polynomial representation of (X - constant)
	 */
	public Polynomial.PowerFunction<T> xMinus (T constant)
	{
		return xPlus (spaceManager.negate (constant));
	}


	public LagrangeInterpolationUsingCalculus (SpaceManager<T> spaceManager)
	{
		super (spaceManager);
		this.ordinaryPolynomialCalculus = new OrdinaryPolynomialCalculus<T> (spaceManager);
		this.rem = polynomialSpaceManager.getZero ();
	}
	protected OrdinaryPolynomialCalculus<T> ordinaryPolynomialCalculus;
	protected Polynomial.PowerFunction<T> rem;


}

