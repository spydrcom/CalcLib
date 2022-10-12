
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.complexnumbers.ComplexValue;

/**
 * original Riemann integral for computation of zeta
 * @author Michael Druckman
 */
public class RiemannZeta extends ComplexExponentComponents
{

	/**
	 * compute t^(s-1) / (exp(t) - 1)
	 * @param s the real part of the exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public double mu (double s, double t)
	{ return Math.pow (t, s-1) / ( Math.exp (t) - 1 ); }

	/**
	 * compute t^s-1 / exp-1
	 * @param s the complex exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public double mu
		(ComplexValue <Double> s, double t)
	{ return mu (s.Re (), t); }

	/**
	 * evaluate integral in fully complex arithmetic
	 * @param s complex parameter to function
	 */
	public RiemannZeta (ComplexValue <Double> s)
	{
		super (s);
	}

	/**
	 * prepare evaluation using just real numbers
	 * @param alpha the real part of the parameter
	 * @param sigma the imag part of the parameter
	 */
	public RiemannZeta (double alpha, double sigma)
	{
		super (alpha, sigma);
	}

	/**
	 * allow as base class for static reference
	 */
	public RiemannZeta () {}

}
