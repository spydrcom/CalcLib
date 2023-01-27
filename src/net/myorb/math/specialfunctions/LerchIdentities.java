
package net.myorb.math.specialfunctions;

import net.myorb.math.complexnumbers.ComplexSpaceCore;
import net.myorb.math.complexnumbers.ComplexValue;

/**
 * identities using the Lerch transcendent (PHI) function
 * @author Michael Druckman
 */
public class LerchIdentities extends ComplexSpaceCore
{


	/**
	 * construction requires specification of the series term count
	 * @param termCount number of terms to apply to the series
	 */
	public LerchIdentities (int termCount)
	{ this.terms = RE (termCount); }
	ComplexValue <Double> terms;


	/**
	 * Lerch PHI function
	 * - pass-thru call to PHI function
	 * @param z parameter to the function
	 * @param s the order of the function
	 * @param alpha offset term for series denominator
	 * @return function result
	 */
	public ComplexValue <Double> phi
		(
			ComplexValue <Double> z,
			ComplexValue <Double> s,
			ComplexValue <Double> alpha
		)
	{
		return Lerch.PHI (z, s, alpha, terms);
	}


	/**
	 * Polygamma psi function
	 * @param n order of the function
	 * @param alpha parameter to the function
	 * @return function result
	 */
	public ComplexValue <Double> psi
		(
			ComplexValue <Double> n,
			ComplexValue <Double> alpha
		)
	{
		int order = n.Re ().intValue ();
		ComplexValue <Double> phi = Lerch.PHI (ONE, RE (order+1), alpha, terms);
		return negWhenEven (productOf (RE (F (order)), phi), order);
	}
	public static ComplexValue <Double> ONE = S (1);


	/**
	 * Polylogarithm Li function
	 * @param s order of the function
	 * @param z parameter to the function
	 * @return function result
	 */
	public ComplexValue <Double> Li
		(
			ComplexValue <Double> s,
			ComplexValue <Double> z
		)
	{
		return productOf (z, Lerch.PHI (z, s, ONE, terms));
	}


	/**
	 * Dirichlet eta function
	 * @param s parameter to the function
	 * @return function result
	 */
	public ComplexValue <Double> DirichletEta
		(
			ComplexValue <Double> s
		)
	{
		return Lerch.PHI (NEG_ONE, s, ONE, terms);
	}
	public static ComplexValue <Double> NEG_ONE = S (-1);


	/**
	 * Dirichlet beta function
	 * @param s parameter to the function
	 * @return function result
	 */
	public ComplexValue <Double> DirichletBeta
		(
			ComplexValue <Double> s
		)
	{
		ComplexValue <Double> TwoTo = toThe (TWO, NEG (s));
		return productOf (TwoTo, Lerch.PHI (NEG_ONE, s, HALF, terms));
	}
	public static ComplexValue <Double> HALF = RE (0.5);


	/**
	 * Legendre chi function
	 * @param s order of the function
	 * @param z parameter to the function
	 * @return function result
	 */
	public ComplexValue <Double> LegendreChi
		(
			ComplexValue <Double> s,
			ComplexValue <Double> z
		)
	{
		ComplexValue <Double> TwoTo = productOf (z, toThe (TWO, NEG (s)));
		return productOf (TwoTo, Lerch.PHI (POW (z, 2), s, HALF, terms));
	}
	public static ComplexValue <Double> TWO = S (2);


	/**
	 * Hurwitz zeta function
	 * @param s parameter to the function
	 * @param alpha offset term
	 * @return function result
	 */
	public ComplexValue <Double> HurwitzZeta
		(
			ComplexValue <Double> s,
			ComplexValue <Double> alpha
		)
	{
		return Lerch.PHI (ONE, s, alpha, terms);
	}


	/**
	 * Riemann zeta function
	 * @param s parameter to the function
	 * @return function result
	 */
	public ComplexValue <Double> RiemannZeta
		(
			ComplexValue <Double> s
		)
	{
		return Lerch.PHI (ONE, s, ONE, terms);
	}


	/**
	 * Lerch zeta function
	 * @param lambda parameter to the function
	 * @param s order of the function
	 * @param alpha offset term
	 * @return function result
	 */
	public ComplexValue <Double> L
		(
			ComplexValue <Double> lambda,
			ComplexValue <Double> s, ComplexValue <Double> alpha
		)
	{
		return Lerch.PHI (productOf (TWO_PI_I, lambda), s, alpha, terms);
	}
	public static ComplexValue <Double> TWO_PI_I = IM (Math.PI*2);


}
