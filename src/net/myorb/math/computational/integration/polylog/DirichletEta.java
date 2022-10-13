
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.complexnumbers.ComplexValue;

/**
 * Dirichlet eta definitive integral formula
 * @author Michael Druckman
 */
public class DirichletEta extends ComplexExponentComponents
{

	/**
	 * compute t^(s-1) / (exp(t) + 1)
	 * @param s the real part of the exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public double mu (double s, double t)
	{ return Math.pow (t, s-1) / ( Math.exp (t) + 1 ); }

	/**
	 * evaluate integral in fully complex arithmetic
	 * @param s complex parameter to function
	 */
	public DirichletEta (ComplexValue <Double> s)
	{
		super (s);
	}

	/**
	 * prepare evaluation using just real numbers
	 * @param alpha the real part of the parameter
	 * @param sigma the imag part of the parameter
	 */
	public DirichletEta (double alpha, double sigma)
	{
		super (alpha, sigma);
	}

	/**
	 * allow as base class for static reference
	 */
	public DirichletEta () {}

}
