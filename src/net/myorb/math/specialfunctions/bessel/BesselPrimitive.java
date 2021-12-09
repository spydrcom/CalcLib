
package net.myorb.math.specialfunctions.bessel;

import net.myorb.math.expressions.ExpressionSpaceManager;
import net.myorb.math.polynomial.PolynomialSpaceManager;
import net.myorb.math.Polynomial;

/**
 * Bessel function specific polynomial construction primitives
 * @author Michael Druckman
 */
public abstract class BesselPrimitive extends UnderlyingOperators
{


	/**
	 * @return products of GAMMA function that calculate the Bessel series denominator
	 * @param <T> data type being used
	 */
	public static <T> Denominator<T> getBesselDenominator ()
	{
		return new Denominator<T>()
		{
			public T eval (int k, Number p, ExpressionSpaceManager<T> sm)
			{
				// k! * GAMMA(k+p+1)
				double gammaSum = gamma (k + p.doubleValue () + 1);
				double product = gammaSum * factorial (k).doubleValue ();
				return sm.convertFromDouble (product);
			}
		};
	}


	/**
	 * @param a a real number identifying the order of the Ia description
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
		return getPoly (a, true, n, psm, getBesselDenominator (), sm);
	}

	public static <T> Polynomial.PowerFunction<T> modifiedSumOfTerms
		(int order, int n, PolynomialSpaceManager<T> psm)
	{
		return sumOfTerms (order, n, psm, true, getBesselDenominator ());
	}


	/**
	 * @param a a real number identifying the order of the Jp description
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
		return getPoly (a, false, n, psm, getBesselDenominator (), sm);
	}

	public static <T> Polynomial.PowerFunction<T> ordinarySumOfTerms
		(int order, int n, PolynomialSpaceManager<T> psm)
	{
		return sumOfTerms (order, n, psm, false, getBesselDenominator ());
	}


}

