
package net.myorb.math.specialfunctions;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexSpaceCore;

/**
 * description of the Lerch transcendent (PHI) function
 * @author Michael Druckman
 */
public class Lerch extends ComplexSpaceCore
{


	/**
	 * Lerch transcendent (PHI) function
	 * @param z parameter to the function
	 * @param s the order of the function
	 * @param alpha offset term for series denominator
	 * @param terms number of terms to apply to the series
	 * @return the computed function result
	 */
	public static ComplexValue <Double> PHI
		(
			ComplexValue <Double> z,
			ComplexValue <Double> s,
			ComplexValue <Double> alpha,
			ComplexValue <Double> terms
		)
	{
		ComplexValue <Double> sum = manager.getZero ();

		for (int n = 0; n <= terms.Re (); n++)
		{
			sum = sumOf
				(
					sum,
					term (z, s, alpha, n)
				);
		}

		return sum;
	}

	/*
	 * PHI (z,s,a) = SIGMA [ 0 <= n <= INFINITY ] ( z^n / ( n + a ) ^ s )
	 */

	public static ComplexValue <Double> term
		(
			ComplexValue <Double> z,
			ComplexValue <Double> s,
			ComplexValue <Double> a,
			int n
		)
	{
		ComplexValue <Double>
				num = POW (z, n), den = sumOf (a, S (n));
		return productOf (num, toThe (den, NEG (s)));
	}


	/*
	 * PHI (z,s,a) = 1/GAMMA (s) * INTEGRAL [0..INFINITY]
	 * 		( t^(s-1) / [(1 - z*exp(-t)) * exp(a*t)] * <*> t )
	 */

	public static ComplexValue <Double> integrand
		(
			ComplexValue <Double> z,
			ComplexValue <Double> s,
			ComplexValue <Double> a,
			ComplexValue <Double> t
		)
	{
		ComplexValue <Double> expProduct =
			productOf
				(
					reduce
					(
						ONE,
						productOf
						(
							z, exp (NEG (t))
						)
					),
					exp
					(
						productOf (a, t)
					)
				);
		return productOf
				(
					toThe (t, reduce (s, ONE)),
					oneOver (expProduct)
				);
	}
	public static ComplexValue <Double> ONE = S (1);


}

