
package net.myorb.math.computational;

import net.myorb.math.GeneratingFunctions.Coefficients;
import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.evaluationstates.Environment;

import net.myorb.data.abstractions.DataSequence2D;
import net.myorb.math.Polynomial;

/**
 * traditional approach to quadrature approximation as specified by Gauss
 * @param <T> the underlying data type
 * @author Michael Druckman
 */
public class GaussQuadrature<T> extends LagrangeInterpolationUsingCalculus<T>
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
	 * @param dataSet the data ( (x[1],y[1]), (x[2],y[2]), ..., (x[n],y[n]) )
	 * @return polynomial description of the interpolation of the data
	 */
	public Polynomial.PowerFunction<T> lagrangeInterpolation (DataSequence2D<T> dataSet)
	{
		Polynomial.PowerFunction<T> f = forSequence (dataSet);
		if (regression != null) f = regression.forGeneralFunction (title, f, dataSet);
		return f;
	}
	protected String title = "Lagrange (Calculus) Interpolation";
	protected Environment<T> environment = null;
	protected Regression<T> regression = null;

	/**
	 * @param dataSet the data ( (x[1],y[1]), (x[2],y[2]), ..., (x[n],y[n]) )
	 * @return coefficient array of the integrated polynomial
	 */
	public Coefficients<T> computeIntegral (DataSequence2D<T> dataSet)
	{
		Polynomial.PowerFunction<T> p = lagrangeInterpolation (dataSet);
		return ordinaryPolynomialCalculus.getFunctionIntegral (p).getCoefficients ();
	}

	/**
	 * @param dataSet the data ( (x[1],y[1]), (x[2],y[2]), ..., (x[n],y[n]) )
	 * @param fromX the low end of the definite integral range
	 * @param toX the high end of the definite integral range
	 * @return the computed quadrature for the range
	 */
	public T evaluateIntegral (DataSequence2D<T> dataSet, T fromX, T toX)
	{
		Polynomial.PowerFunction<T> p = lagrangeInterpolation (dataSet);
		return ordinaryPolynomialCalculus.evaluatePolynomialIntegral (p, fromX, toX);
	}

	public GaussQuadrature (Environment<T> environment)
	{
		this (environment.getSpaceManager ());
		this.regression = new Regression<T>(environment);
		this.environment = environment;
	}
	public GaussQuadrature (ExpressionSpaceManager<T> sm) { super (sm); }

}

