
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
	 * definition of the Lerch Transcendent (PHI) function
	 */
	public interface Transcendent
	{
		/**
		 * Lerch transcendent (PHI) function
		 * @param z parameter to the function call
		 * @param s the order of the described function
		 * @param alpha offset term for series denominator
		 * @return the computed function result
		 */
		ComplexValue <Double> PHI
		(
			ComplexValue <Double> z,
			ComplexValue <Double> s,
			ComplexValue <Double> alpha
		);
	}


	/**
	 * implementation of Transcendent.PHI using series
	 */
	public static class Series implements Transcendent
	{
		public Series
			(
				ComplexValue <Double> tolerance,
				ComplexValue <Double> termMaximum
			)
		{
			this.tolerance = tolerance; this.terms = termMaximum;
		}
		public ComplexValue <Double> PHI
			(
				ComplexValue <Double> z,
				ComplexValue <Double> s,
				ComplexValue <Double> alpha
			)
		{
			return Lerch.PHI (z, s, alpha, tolerance, terms);
		}
		protected ComplexValue <Double> tolerance, terms;
	}


	/**
	 * Lerch transcendent (PHI) function
	 * - limited form of the infinite series
	 * @param z parameter to the function call
	 * @param s the order of the described function
	 * @param alpha offset term for series denominator
	 * @param tolerance the shortcut point of the terms
	 * @param terms number of terms to apply to the series
	 * @return the computed function result
	 */
	public static ComplexValue <Double> PHI
		(
			ComplexValue <Double> z,
			ComplexValue <Double> s,
			ComplexValue <Double> alpha,
			ComplexValue <Double> tolerance,
			ComplexValue <Double> terms
		)
	{
		boolean ignoreTolerance = tolerance.Re () <= 0;
		ComplexValue <Double> sum = manager.getZero (), term;

		for (int n = 0; n <= terms.Re (); n++)
		{
			sum = sumOf
				(
					sum,
					term = nTHterm (z, s, alpha, n)
				);
			if ( ignoreTolerance ) continue;
			if ( withinTolerance (term, tolerance) ) return sum;
		}

		return sum;
	}
	static boolean withinTolerance
	(ComplexValue <Double> term, ComplexValue <Double> tolerance)
	{ return manager.lessThan (term, tolerance); }

	/*
	 * PHI (z,s,a) = SIGMA [ 0 <= n <= INFINITY ] ( z^n / ( n + a ) ^ s )
	 */

	/**
	 * computation of one term
	 * @param z parameter to the function call
	 * @param s the order of the described function
	 * @param a offset term for series denominator
	 * @param n the index of the term
	 * @return the computed value
	 */
	public static ComplexValue <Double> nTHterm
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

	/**
	 * compute a value of the PHI integrand
	 * @param z parameter to the function call
	 * @param s the order of the described function
	 * @param a offset term for series denominator
	 * @param t the integration variable
	 * @return the computed value
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

