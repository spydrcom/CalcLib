
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.complexnumbers.ComplexValue;

/**
 * Gauss Pi definitive integral formula
 * @author Michael Druckman
 */
public class GaussPi extends ComplexExponentComponents
{

	// PI(z) = GAMMA(z+1) = z*GAMMA(z) = INTEGRAL [0 <= t <= INFINITY] ( exp(-t) * t^z * <*> t )

	/**
	 * compute t^z / e^t
	 * @param s the real part of the exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public double mu (double s, double t)
	//{ return Math.pow (t, s) / Math.exp (t); }
	// a little algebra shows equivalent formula (perhaps faster)
	{ return Math.exp (kLnT (s, t) - t); }

	/**
	 * evaluate integral in fully complex arithmetic
	 * @param s complex parameter to function
	 */
	public GaussPi (ComplexValue <Double> s)
	{
		super (s);
	}

	/**
	 * prepare evaluation using just real numbers
	 * @param alpha the real part of the parameter
	 * @param sigma the imag part of the parameter
	 */
	public GaussPi (double alpha, double sigma)
	{
		super (alpha, sigma);
	}

	/**
	 * allow as base class for static reference
	 */
	public GaussPi () {}

}
