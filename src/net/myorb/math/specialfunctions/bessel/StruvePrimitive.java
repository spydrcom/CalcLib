
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.Polynomial;

/**
 * Struve function specific polynomial construction primitives
 * @author Michael Druckman
 */
public abstract class StruvePrimitive extends UnderlyingOperators
{


	/**
	 * @return products of GAMMA function that calculate the Struve series denominator
	 * @param <T> data type being used
	 */
	public static <T> Denominator<T> getStruveDenominator ()
	{
		return new Denominator<T>()
		{
			public T eval (int m, Number a, ExpressionSpaceManager<T> sm)
			{
				// GAMMA(m+3/2) * GAMMA(m+a+3/2)
				double gammaIndex = gamma (m + THREE_HALVES);
				double gammaOrder = gamma (m + a.doubleValue () + THREE_HALVES);
				return sm.convertFromDouble (gammaIndex * gammaOrder);
			}
		};
	}
	public static final double THREE_HALVES = 3.0 / 2.0;


	/**
	 * @param a a real number identifying the order of the La description
	 * @param n the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @param sm a manager for the number space in use
	 * @return the representation of the polynomial
	 * @param <T> data type manager
	 */
	public static <T> Polynomial.PowerFunction<T> getModifiedPoly 
		(
			T a, int n, PolynomialSpaceManager<T> psm,
			ExpressionSpaceManager<T> sm
		)
	{
		return getPoly (a, true, n, psm, getStruveDenominator (), sm);
	}
	public static <T> Polynomial.PowerFunction<T> modifiedSumOfTerms
		(int a, int n, PolynomialSpaceManager<T> psm)
	{
		return sumOfTerms
		(
			a + 1, n, a, psm, true,
			getStruveDenominator ()
		);
	}


	/**
	 * @param a a real number identifying the order of the Ha description
	 * @param n the number of terms to include in the polynomial
	 * @param psm a space manager for polynomial management
	 * @param sm a manager for the number space in use
	 * @return the representation of the polynomial
	 * @param <T> data type manager
	 */
	public static <T> Polynomial.PowerFunction<T> getOrdinaryPoly 
		(
			T a, int n, PolynomialSpaceManager<T> psm,
			ExpressionSpaceManager<T> sm
		)
	{
		return getPoly (a, false, n, psm, getStruveDenominator (), sm);
	}
	public static <T> Polynomial.PowerFunction<T> ordinarySumOfTerms
		(int a, int n, PolynomialSpaceManager<T> psm)
	{
		return sumOfTerms
		(
			a + 1, n, a, psm, false,
			getStruveDenominator ()
		);
	}


}

