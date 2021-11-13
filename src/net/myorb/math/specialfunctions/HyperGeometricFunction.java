
package net.myorb.math.specialfunctions;

/**
 * general support for Hyper-Geometric Functions
 * @author Michael Druckman
 */
public class HyperGeometricFunction extends PochhammerSymbol
{


	/**
	 * @param a real number parameter
	 * @param n the integer range of the raising factorial
	 * @return gamma(a+n) / gamma(a)
	 */
	public static double PH (double a, int n) { return eval (a, n); }


	/**
	 * @param numerator the factors of the numerator
	 * @param denominator the factors of the denominator
	 * @param n the order of the term being computed
	 * @return ratio of Pochhammer of factors
	 */
	public static double computeRatio (double[] numerator, double[] denominator, int n)
	{
		double ratio = 1;
		for (double v : numerator) ratio *= PH(v,n);
		for (double v : denominator) ratio /= PH(v,n);
		return ratio;
	}


	/**
	 * negative integral numerator factors
	 *  limit the number of significant terms
	 * @param numerator the factors of the numerator
	 * @return the number of terms to be factored into the polynomial
	 */
	public static int getTermCount (double[] numerator)
	{
		for (double v : numerator)
		{
			if (isInteger (v) && v <= 0)
			{
				return - (int) v;
			}
		}
		return DEFAULT_INFINITE_SERIES_APPROXIMATION_TERM_COUNT;
	}
	public static final int DEFAULT_INFINITE_SERIES_APPROXIMATION_TERM_COUNT = 25;


	/**
	 * @param numerator the factors of the numerator
	 * @param denominator the factors of the denominator
	 * @param x parameter value to function
	 * @return computed function value
	 */
	public static double series (double[] numerator, double[] denominator, double x)
	{
		double sum = 1, xTerm = 1, coef;
		int terms = getTermCount (numerator);

		for (int n = 1; n <= terms; n++)
		{
			xTerm *= x / n;
			coef = computeRatio (numerator, denominator, n);
			sum += coef * xTerm;
		}

		return sum;
	}


	/**
	 * @param a first numerator constant
	 * @param b second numerator constant
	 * @param c first denominator constant
	 * @param x parameter value to function
	 * @return computed function value
	 */
	public static double F (double a, double b, double c, double x)
	{
		return series (new double[]{a,b}, new double[]{c}, x);
	}


	/**
	 * @param a first numerator constant
	 * @param c first denominator constant
	 * @param x parameter value to function
	 * @return computed function value
	 */
	public static double U (double a, double c, double x)
	{
		if (c == 1) return specialCaseU (a, x);
		double terms = M (a, c, x) / (gamma (1 + a - c) * gamma (c));
		if (!isInteger (a) || a > 0) terms -= secondTermU (a, c, x);
		return Math.PI / Math.sin (c * Math.PI) * terms;
	}
	public static double specialCaseU (double a, double x)
	{
		return -1 / gamma (a) * (Math.log (x) + digamma (x));
	}
	public static double secondTermU (double a, double c, double x)
	{
		return Math.pow (x, 1 - c) * M (1 + a - c, 2 - c, x) / (gamma (a) * gamma (2 - c));
	}
	public static double M (double a, double c, double x)
	{
		return series (new double[]{a}, new double[]{c}, x);
	}


}

