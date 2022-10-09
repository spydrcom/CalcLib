
package net.myorb.math.computational;

import net.myorb.math.complexnumbers.ComplexValue;

/**
 * algorithms for evaluation of the integral form t^z
 * @author Michael Druckman
 */
public class CauchySchlomilch
{

	/**
	 * compute k * ln(t)
	 * @param k the constant factor
	 * @param t the integration variable
	 * @return the computed product
	 */
	public static double
		kLnT (double k, double t)
	{ return k * Math.log (t); }

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
	public static double cosSigmaT (double t, double sigma, double alpha)
	{ return Math.cos ( kLnT (sigma, t) ) * mu (alpha, t); }

	/**
	 * compute the
	 *  real part of the integrand
	 * @param t the integration variable
	 * @param s the parameter to the integral
	 * @return the computed value
	 */
	public static double cosSigmaT
		(double t, ComplexValue <Double> s)
	{ return Math.cos ( kLnT (s.Im (), t) ) * mu (s.Re (), t); }

	/**
	 * compute the
	 *  imag part of the integrand
	 * @param t the integration variable
	 * @param sigma the IM part of the parameter
	 * @param alpha the RE part of the parameter
	 * @return the computed value
	 */
	public static double sinSigmaT
		(double t, double sigma, double alpha)
	{ return Math.sin ( kLnT (sigma, t) ) * mu (alpha, t); }

	/**
	 * compute the
	 *  imag part of the integrand
	 * @param t the integration variable
	 * @param s the parameter to the integral
	 * @return the computed value
	 */
	public static double sinSigmaT
		(double t, ComplexValue <Double> s)
	{ return Math.sin ( kLnT (s.Im (), t) ) * mu (s.Re (), t); }

	/**
	 * evaluate the integrand
	 * @param t the integration variable
	 * @param s the complex parameter to the integral
	 * @return the full computed complex value
	 */
	public static ComplexValue <Double> eval
		(double t, ComplexValue <Double> s)
	{
		return s.C (cosSigmaT (t, s), sinSigmaT (t, s));
	}

	/**
	 * evaluate integral in fully complex arithmetic
	 * @param s complex parameter to function
	 */
	public CauchySchlomilch (ComplexValue <Double> s)
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
	public CauchySchlomilch (double alpha, double sigma)
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
		return cosSigmaT (t, sigma, alpha);
	}

	/**
	 * evaluate integrand at point t
	 * @param t the integration variable value
	 * @return the imag part of the result
	 */
	public double evalImagPart (double t)
	{
		return sinSigmaT (t, sigma, alpha);
	}

}

