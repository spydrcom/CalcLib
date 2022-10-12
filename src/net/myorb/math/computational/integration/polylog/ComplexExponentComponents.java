
package net.myorb.math.computational.integration.polylog;

import net.myorb.math.complexnumbers.ComplexValue;

/**
 * common base class for complex exponential formulas
 * @author Michael Druckman
 */
public abstract class ComplexExponentComponents extends CauchySchlomilch
{

	/**
	 * the real part factor of the formula
	 * @param s the real part of the exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public abstract double mu (double s, double t);

	/**
	 * compute the
	 *  real part of the integrand
	 * @param t the integration variable
	 * @param sigma the IM part of the parameter
	 * @param alpha the RE part of the parameter
	 * @return the computed value
	 */
	public double cosSigmaTmu (double t, double sigma, double alpha)
	{ return cosSigmaT (t, sigma) * mu (alpha, t); }

	/**
	 * compute the
	 *  real part of the integrand
	 * @param t the integration variable
	 * @param s the parameter to the integral
	 * @return the computed value
	 */
	public double cosSigmaTmu
		(double t, ComplexValue <Double> s)
	{ return cosSigmaT (t, s) * mu (s.Re (), t); }

	/**
	 * compute the
	 *  imag part of the integrand
	 * @param t the integration variable
	 * @param sigma the IM part of the parameter
	 * @param alpha the RE part of the parameter
	 * @return the computed value
	 */
	public double sinSigmaTmu
		(double t, double sigma, double alpha)
	{ return sinSigmaT (t, sigma) * mu (alpha, t); }

	/**
	 * compute the
	 *  imag part of the integrand
	 * @param t the integration variable
	 * @param s the parameter to the integral
	 * @return the computed value
	 */
	public double sinSigmaTmu
		(double t, ComplexValue <Double> s)
	{ return sinSigmaT (t, s) * mu (s.Re (), t); }

	/**
	 * evaluate the integrand
	 * @param t the integration variable
	 * @param s the complex parameter to the integral
	 * @return the full computed complex value
	 */
	public ComplexValue <Double> eval
		(double t, ComplexValue <Double> s)
	{
		return s.C (cosSigmaTmu (t, s), sinSigmaTmu (t, s));
	}

	/**
	 * evaluate integral in fully complex arithmetic
	 * @param s complex parameter to function
	 */
	public ComplexExponentComponents (ComplexValue <Double> s)
	{
		this.s = s;
	}
	protected ComplexValue <Double> s;

	/**
	 * evaluate integrand at point t
	 * @param t the integration variable value
	 * @return the complex result
	 */
	public ComplexValue <Double> eval (double t)
	{
		return eval (t, s);
	}

	/**
	 * prepare evaluation using just real numbers
	 * @param alpha the real part of the parameter
	 * @param sigma the imag part of the parameter
	 */
	public ComplexExponentComponents (double alpha, double sigma)
	{
		this.alpha = alpha; this.sigma = sigma;
	}
	protected double alpha, sigma;

	/**
	 * evaluate integrand at point t
	 * @param t the integration variable value
	 * @return the real part of the result
	 */
	public double evalRealPart (double t)
	{
		return cosSigmaTmu (t, sigma, alpha);
	}

	/**
	 * evaluate integrand at point t
	 * @param t the integration variable value
	 * @return the imag part of the result
	 */
	public double evalImagPart (double t)
	{
		return sinSigmaTmu (t, sigma, alpha);
	}

	/**
	 * allow as base class for static reference
	 */
	public ComplexExponentComponents () {}

}
