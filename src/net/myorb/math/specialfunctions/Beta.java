
package net.myorb.math.specialfunctions;

import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.ComplexValue;

/**
 * description of the Catalan beta function
 * @author Michael Druckman
 */
public class Beta extends ComplexSpaceCore
{

	// beta (s) = SIGMA [ 0 <= n <= INFINITY ] ( (-1)^n / (2*n+1)^s )

	/**
	 * the infinite series for function evaluation
	 * @param s the parameter for beta function evaluation
	 * @param terms the number of terms for series evaluation
	 * @return the computed function value
	 */
	public static ComplexValue <Double> series
		(ComplexValue <Double> s, int terms)
	{
		ComplexValue <Double> sum = manager.getZero ();

		for (int n = 0; n <= terms; n++)
		{
			sum = sumOf
				(
					sum,
					term (s, n)
				);
		}

		return sum;
	}
	public static ComplexValue <Double> term
		(ComplexValue <Double> s, int n)
	{
		ComplexValue <Double> value;
		value = cplxLib.power (RE (2*n + 1), NEG (s));
		return negWhenEven (value, n);
	}

	// beta (1-s) = (pi/2)^(-s) * sin(s*pi/2) * GAMMA(s) * beta(s)

	/**
	 * function evaluation for s LE 0.5
	 * @param s the negated parameter to the beta function
	 * @param n the number of terms for series evaluation
	 * @return the computed function value
	 */
	public static ComplexValue <Double> negative
		(ComplexValue <Double> s, int n)
	{
		ComplexValue <Double> value;
		value = cplxLib.power (PI_2, NEG (s));
		value = productOf (value, cplxLib.sin (productOf (PI_2, s)));
		value = productOf (value, cplxLib.gamma (s));
		value = productOf (value, series (s, n));
		return value;		
	}
	public static final ComplexValue <Double> PI_2 = RE (Math.PI/2);

	/**
	 * general function evaluation
	 * @param s the parameter to the beta function
	 * @param terms the number of terms for series evaluation
	 * @return the computed function value
	 */
	public static ComplexValue <Double> eval
	(ComplexValue <Double> s, int terms)
	{
		if ( s.Re () > 0.5 )
		{
			return series (s, terms);
		}
		else
		{
			return negative (CV (1 - s.Re (), s.Im ()), terms);
		}
	}

}
