
package net.myorb.testing.squareRoot;

/**
 * methods needed across several implementations
 * @author Michael Druckman
 */
public class Library
{


	static double EPSILON = 0.0000000001, TOLERANCE = 0.00000000001;	// control values for accuracy and precision


	/**
	 * compute base ^ exponent
	 * @param base the base of the exponential
	 * @param exponent the exponent value
	 * @return computed result
	 */
	public static double pow (double base, int exponent)
	{
		if (exponent < 0) return 1 / pow (base, -exponent);
		if (exponent == 1) return base;
		if (exponent == 0) return 1;

		double product = base;
		for (int i = exponent; i > 1; i--) product *= base;
		return product;
	}


	/**
	 * determine value proximity to zero
	 * @param value the value being compared with zero
	 * @return TRUE = value within stated constant tolerance of zero
	 */
	public static boolean withinTolerance (double value)
	{ return abs (value) < TOLERANCE; }


	/**
	 * standard absolute value function
	 * @param x the value being compared with zero
	 * @return same value as parameter for positive values, otherwise negated value
	 */
	public static double abs (double x)
	{ return x<0? -x: x; }


}
