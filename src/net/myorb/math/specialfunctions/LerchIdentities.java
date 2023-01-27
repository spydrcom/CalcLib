
package net.myorb.math.specialfunctions;

import net.myorb.math.complexnumbers.ComplexValue;
import net.myorb.math.complexnumbers.ComplexSpaceCore;

/**
 * identities using the Lerch transcendent (PHI) function
 * @author Michael Druckman
 */
public class LerchIdentities extends ComplexSpaceCore
{


	/**
	 * construction requires an implementation of PHI
	 * @param T the implementation of PHI specified as Lerch.Transcendent
	 */
	public LerchIdentities
		(Lerch.Transcendent T) { this.T = T; }
	protected Lerch.Transcendent T;


	/**
	 * Lerch PHI function
	 * - pass-thru call to PHI function
	 * @param z parameter to the function call
	 * @param s the order of the described function
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
		// PHI (z,s,a) = SIGMA [k=0:INF] (z^k/(k+a)^s)
		return T.PHI (z, s, alpha);
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
		// PSI (n,a) = (-1)^(n+1) * PHI (1,n+1,a) * n!
		ComplexValue <Double> phi = T.PHI (ONE, RE (order+1), alpha);
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
		// Li(s,z) = z * PHI (z,s,1)
		//	= SIGMA [k=1:INF] ( z^k / k^s )
		return productOf (z, T.PHI (z, s, ONE));
	}


	/**
	 * Dirichlet eta function
	 * @param s parameter to the function
	 * @return function result
	 */
	public ComplexValue <Double> eta
		(
			ComplexValue <Double> s
		)
	{
		// eta(s) = PHI (-1,s,1)
		//	= SIGMA [k=1:INF] ((-1)^(k-1)/k^s)
		return T.PHI (NEG_ONE, s, ONE);
	}
	public static ComplexValue <Double> NEG_ONE = S (-1);


	/**
	 * Dirichlet beta function
	 * @param s parameter to the function
	 * @return function result
	 */
	public ComplexValue <Double> beta
		(
			ComplexValue <Double> s
		)
	{
		// beta(s) = 2^(-s) * PHI (-1,s,1/2)
		//	= SIGMA [k=0:INF] ((-1)^k/(2k+1)^s)
		ComplexValue <Double> TwoToS = toThe (TWO, NEG (s));
		return productOf (TwoToS, T.PHI (NEG_ONE, s, HALF));
	}
	public static ComplexValue <Double> HALF = RE (0.5);


	/**
	 * Legendre chi function
	 * @param s order of the function
	 * @param z parameter to the function
	 * @return function result
	 */
	public ComplexValue <Double> chi
		(
			ComplexValue <Double> s,
			ComplexValue <Double> z
		)
	{
		// chi(s,z) = 2^(-s) * PHI (z^2,s,1/2) * z
		//	= SIGMA [k=0:INF] ( z ^ (2k+1) / (2k+1) ^ s )
		ComplexValue <Double> TwoTo = productOf (z, toThe (TWO, NEG (s)));
		return productOf (TwoTo, T.PHI (POW (z, 2), s, HALF));
	}
	public static ComplexValue <Double> TWO = S (2);


	/**
	 * Riemann zeta function
	 * @param s parameter to the function
	 * @return function result
	 */
	public ComplexValue <Double> zeta
		(
			ComplexValue <Double> s
		)
	{
		// zeta (s) = PHI (1,s,1)
		//	= SIGMA [k=1:INF] ( 1 / k^s )
		return T.PHI (ONE, s, ONE);
	}


	/**
	 * Hurwitz zeta function
	 * @param s parameter to the function
	 * @param alpha offset term
	 * @return function result
	 */
	public ComplexValue <Double> zeta
		(
			ComplexValue <Double> s,
			ComplexValue <Double> alpha
		)
	{
		// zeta (s,a) = PHI (1,s,a)
		//	= SIGMA [k=0:INF] ( 1 / (k+a)^s )
		return T.PHI (ONE, s, alpha);
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
		// L (lambda,s,alpha) = PHI ( exp (2*i*pi*lambda), s, alpha)
		//		= SIGMA [k=0:INF] ( e ^ ( 2 i PI lambda k ) / (k+a)^s )
		return T.PHI (exp (productOf (TWO_PI_I, lambda)), s, alpha);
	}
	public static ComplexValue <Double> TWO_PI_I = IM (Math.PI*2);


}

