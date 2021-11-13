
package net.myorb.math.polynomial.families;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.expressions.evaluationstates.ListSupport;

import net.myorb.math.specialfunctions.HyperGeometricFunction;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.GeneratingFunctions;

import net.myorb.math.SpaceManager;
import net.myorb.math.Polynomial;

/**
 * support for Hyper-Geometric Polynomial generation
 * @author Michael Druckman
 */
public class HyperGeometricPolynomial<T> extends HyperGeometricFunction
{


	/**
	 * @param manager a data type manager to use in coefficient computations
	 */
	public HyperGeometricPolynomial (SpaceManager<T> manager)
	{
		this.polynomialManager = new PolynomialSpaceManager<T>(manager);
		this.typeManager = toExpressionManager (manager);
	}
	protected PolynomialSpaceManager<T> polynomialManager;
	protected ExpressionSpaceManager<T> typeManager;


	/**
	 * @param to specific term count to be used overriding default
	 */
	public void setTermCount (T to)
	{ this.specifiedTermCount = typeManager.convertToInteger (to); }
	protected int specifiedTermCount = -1;


	/**
	 * @param numerator the factors of the term numerators
	 * @param denominator the factors of the term denominators
	 * @return the coefficients of the specified polynomial
	 */
	public GeneratingFunctions.Coefficients<T> generatePolynomialCoefficientsFor
		(double[] numerator, double[] denominator)
	{
		double nFactorial = 1;
		int terms = specifiedTermCount;
		GeneratingFunctions.Coefficients<T> coefficients =
			polynomialManager.newScalar (1).getCoefficients ();
		if (terms < 0) terms = getTermCount (numerator);

		for (int n = 1; n <= terms; n++)
		{
			coefficients.add
			(
				typeManager.convertFromDouble
				(
					computeRatio (numerator, denominator, n)
					/ (nFactorial *= n)
				)
			);
		}

		return coefficients;
	}


	/**
	 * @param numerator the factors of the term numerators
	 * @param denominator the factors of the term denominators
	 * @return the coefficients of the specified polynomial
	 */
	public GeneratingFunctions.Coefficients<T> generatePolynomialCoefficientsFor
		(Polynomial.Coefficients<T> numerator, Polynomial.Coefficients<T> denominator)
	{
		return generatePolynomialCoefficientsFor
		(
			ListSupport.toArray (numerator, typeManager),
			ListSupport.toArray (denominator, typeManager)
		);
	}


	/**
	 * @param numerator the factors of the term numerators
	 * @param denominator the factors of the term denominators
	 * @return the polynomial power function
	 */
	public Polynomial.PowerFunction<T> generatePolynomialFor
		(double[] numerator, double[] denominator)
	{
		return polynomialManager.getPolynomialFunction
		(
			generatePolynomialCoefficientsFor (numerator, denominator)
		);
	}


}

