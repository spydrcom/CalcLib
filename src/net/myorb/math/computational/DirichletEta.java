
package net.myorb.math.computational;

import net.myorb.math.complexnumbers.ComplexValue;

/**
 * cosh formula for computation of Dirichlet eta
 * @author Michael Druckman
 */
public class DirichletEta extends CauchySchlomilch
{

	/**
	 * compute t^s / cosh
	 * @param s the exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public static double mu (double s, double t)
	{ return Math.pow (t, s) / COSH2SQ (t); }

	/**
	 * compute t^s / cosh
	 * @param s the complex exponent
	 * @param t the integration variable
	 * @return the computed value
	 */
	public static double mu
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
	 * compute the
	 *  real part of the integrand
	 * @param t the integration variable
	 * @param sigma the IM part of the parameter
	 * @param alpha the RE part of the parameter
	 * @return the computed value
	 */
	public static double cosSigmaTmu (double t, double sigma, double alpha)
	{ return cosSigmaT (t, sigma) * mu (alpha, t); }

	/**
	 * compute the
	 *  real part of the integrand
	 * @param t the integration variable
	 * @param s the parameter to the integral
	 * @return the computed value
	 */
	public static double cosSigmaTmu
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
	public static double sinSigmaTmu
		(double t, double sigma, double alpha)
	{ return sinSigmaT (t, sigma) * mu (alpha, t); }

	/**
	 * compute the
	 *  imag part of the integrand
	 * @param t the integration variable
	 * @param s the parameter to the integral
	 * @return the computed value
	 */
	public static double sinSigmaTmu
		(double t, ComplexValue <Double> s)
	{ return sinSigmaT (t, s) * mu (s.Re (), t); }

	/**
	 * evaluate the integrand
	 * @param t the integration variable
	 * @param s the complex parameter to the integral
	 * @return the full computed complex value
	 */
	public static ComplexValue <Double> eval
		(double t, ComplexValue <Double> s)
	{
		return s.C (cosSigmaTmu (t, s), sinSigmaTmu (t, s));
	}

	/**
	 * evaluate integral in fully complex arithmetic
	 * @param s complex parameter to function
	 */
	public DirichletEta (ComplexValue <Double> s)
	{
		this.s = s;
	}
	ComplexValue <Double> s;

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
	public DirichletEta (double alpha, double sigma)
	{
		this.alpha = alpha; this.sigma = sigma;
	}
	double alpha, sigma;

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
	public DirichletEta () {}

}
