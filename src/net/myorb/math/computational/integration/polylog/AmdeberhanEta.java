
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.complexnumbers.ComplexValue;

/**
 * cosh formula for computation of Dirichlet eta
 * @author Michael Druckman
 */
public class AmdeberhanEta  extends ComplexExponentComponents
{

	/**
	 * compute t^s / cosh
	 * @param s the exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public double mu (double s, double t)
	{ return Math.pow (t, s) / COSH2SQ (t); }

	/**
	 * compute t^s / cosh
	 * @param s the complex exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public double mu
		(ComplexValue <Double> s, double t)
	{ return mu (s.Re (), t); }

	/**
	 * compute cosh^2 x
	 * @param x the integration variable value
	 * @return the computed value
	 */
	public static
		double COSH2SQ (double x)
	{ return SQ ( Math.cosh (x) ); }

	/**
	 * compute x^2 (cheaper than POW)
	 * @param x the value to square
	 * @return the computed value
	 */
	public static double SQ (double x) { return x*x; }

	/**
	 * evaluate integral in fully complex arithmetic
	 * @param s complex parameter to function
	 */
	public AmdeberhanEta (ComplexValue <Double> s)
	{
		super (s);
	}

	/**
	 * prepare evaluation using just real numbers
	 * @param alpha the real part of the parameter
	 * @param sigma the imag part of the parameter
	 */
	public AmdeberhanEta (double alpha, double sigma)
	{
		super (alpha, sigma);
	}

	/**
	 * allow as base class for static reference
	 */
	public AmdeberhanEta () {}

}

