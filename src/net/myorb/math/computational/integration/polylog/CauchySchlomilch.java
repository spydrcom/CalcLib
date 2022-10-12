
package net.myorb.math.computational.integration.polylog;

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
	 * compute the
	 *  real part of the integrand
	 * @param t the integration variable
	 * @param sigma the IM part of the parameter
	 * @return the computed value
	 */
	public static double cosSigmaT (double t, double sigma)
	{ return Math.cos ( kLnT (sigma, t) ); }

	/**
	 * compute the
	 *  real part of the integrand
	 * @param t the integration variable
	 * @param s the parameter to the integral
	 * @return the computed value
	 */
	public static double cosSigmaT
		(double t, ComplexValue <Double> s)
	{ return Math.cos ( kLnT (s.Im (), t) ); }

	/**
	 * compute the
	 *  imag part of the integrand
	 * @param t the integration variable
	 * @param sigma the IM part of the parameter
	 * @return the computed value
	 */
	public static double sinSigmaT
		(double t, double sigma)
	{ return Math.sin ( kLnT (sigma, t) ); }

	/**
	 * compute the
	 *  imag part of the integrand
	 * @param t the integration variable
	 * @param s the parameter to the integral
	 * @return the computed value
	 */
	public static double sinSigmaT
		(double t, ComplexValue <Double> s)
	{ return Math.sin ( kLnT (s.Im (), t) ); }

}

