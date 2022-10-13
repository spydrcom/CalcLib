
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.complexnumbers.ComplexValue;

/**
 * exponential formula for computation of Bernoulli Gamma
 * @author Michael Druckman
 */
public class BernoulliGamma extends ComplexExponentComponents
{

	/**
	 * compute t^(s-1) / e^t
	 * @param s the real part of the exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public double mu (double s, double t)
	//{ return Math.pow (t, s-1) / Math.exp (t); }
	// a little algebra shows equivalent formula (perhaps faster)
	{ return Math.exp (kLnT (s-1, t) - t); }

	/**
	 * evaluate integral in fully complex arithmetic
	 * @param s complex parameter to function
	 */
	public BernoulliGamma (ComplexValue <Double> s)
	{
		super (s);
	}

	/**
	 * prepare evaluation using just real numbers
	 * @param alpha the real part of the parameter
	 * @param sigma the imag part of the parameter
	 */
	public BernoulliGamma (double alpha, double sigma)
	{
		super (alpha, sigma);
	}

	/**
	 * allow as base class for static reference
	 */
	public BernoulliGamma () {}

}
