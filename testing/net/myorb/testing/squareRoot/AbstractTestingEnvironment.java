
package net.myorb.testing.squareRoot;

import java.math.BigInteger;

/**
 * common environment for testing SQRT algorithm implementations
 * @author Michael Druckman
 */
public abstract class AbstractTestingEnvironment
{


	int iterations = 0;


	/**
	 * common method for handling errors, commonly RuntimeException
	 * @param message text of message for cause of error
	 */
	public static void errorTermination (String message) { throw new RuntimeException (message); }
	public static final String  SMALL_DERIVATIVE  = "Derivative too small, local max/min/inflection found";
	public static final String NEGATIVE_PARAMETER = "SQRT not available for negative parameter, complex range required";
	public static final String FAILED_CONVERGENCE = "Failure to converge, too high a precision requested or too small an iteration setting";


	/**
	 * use multiples of perfect squares to transform X to optimal range
	 * @param x value of X being transformed restricted to 0 LT X LT 1
	 * @return transformer for X such that 0.9 LT X*divisor^2 LT 1.1
	 */
	public double perfectSquaresReductionFactor (double x)
	{
		double divisor;

		divisor = alterDivisor (x, 1, 100);					// 10,000
		divisor = alterDivisor (x, divisor, 10);			// 100
		divisor = alterDivisor (x, divisor, 3);				// 9

		divisor = alterDivisor (x, divisor, 3.0/2.0);		// 1.5^2		2.25		rational
		//divisor = alterDivisor (x, divisor, 9.0/8.0);		// 1.125^2		1.265625	rational
		//divisor = alterDivisor (x, divisor, 10.0/9.0);	// 1.111...^2	1.23456...	irrational
		//divisor = alterDivisor (x, divisor, 13.0/12.0);	// 1.083...^2	1.17361...	irrational
		divisor = alterDivisor (x, divisor, 17.0/16.0);		// 1.0625^2		1.12890625	rational
		
		return divisor;
	}


	/**
	 * reduction algoritm attempts to force X into range ABS (X - 1) LT 0.1
	 *  seeking the benefit of taking the power series terms down to ending with a multiplier of (0.1)^TERMS at worst
	 * @param x the value of X being reduced for improved convergence of SQRT series
	 * @param divisor compiled product of adjustments up to this point
	 * @param using attempt further adjustment using this
	 * @return new divisor having applied this attempt
	 */
	public double alterDivisor (double x, double divisor, double using)
	{
		iterations++;												// initial 4 multiplies + 1 compare counts as an iteration
		double usingSqX = using * using * x;
		while ((divisor * divisor * usingSqX) < REDUCED_DOMAIN_HI)
		{ divisor *= using; iterations++; }							// sumsequent iterations are 3 multiplies + 1 compare
		return divisor;
	}
	static final double
	REDUCED_DOMAIN_LO = 0.9,
	REDUCED_DOMAIN_HI = 1.1;		// tests between 0.9 and 1.3 show 1.1 provides lowest error average


	/**
	 * compute value half bit-width in size
	 * @param x the value seeking SQRT approximation
	 * @return computed approximation
	 */
	public double bitLengthApproximatedSqrt (double x)
	{
		BigInteger TWO = BigInteger.valueOf (2),
			bigint = BigInteger.valueOf ((long) x);
		int approximatedRootLength = bigint.bitLength () / 2;
		return TWO.pow (approximatedRootLength).doubleValue ();		// 1/2 the bit size
	}


	/**
	 * find nearest 2^n to use as approximation
	 * @param x the value seeking SQRT approximation 0 LT X LT 1
	 * @return computed approximation
	 */
	public double power2ApproximatedSqrt (double x)
	{
		double root = 0.5;
		while (x < root*root)
		{ root /= 2; iterations++; }
		return root;

//		return Library.pow (2.0, log2Approximation (x) / 2);
	}


	/**
	 * find power of 2 less than x
	 * @param x the value searching for root
	 * @return approximate log2 of X
	 */
	public int log2Approximation (double x)
	{
		int log = -2;
		double approx = 0.25;
		while (x < approx)
		{ approx /= 2; iterations++; log--; }
		return log;
	}


	/**
	 * implemented to algorithm specifics
	 * @param x parameter for sqrt computation
	 * @param flavor different for each implementation
	 * @return computed result
	 */
	public abstract double sqrtAlgorithmImplementation (double x, int flavor);


	/**
	 * display header for report
	 * @return header text
	 */
	public abstract String getHeaderText ();


	/**
	 * text of title for statistics report
	 * @return text of title
	 */
	public abstract String getTitleText ();


	/**
	 * get text of description of type of implementation
	 * @param flavor an ID value for the type of algorithm used
	 * @return a text description of the algorithm
	 */
	public abstract String getFlavorDescription (int flavor);


	/**
	 * domain has been limited,
	 *  execute algorithm with this alteration
	 * @param x the value searching for its square root
	 * @param flavor type of algorithm implementation
	 * @return the computed result
	 */
	public double operateOnLimitedDomain (double x, int flavor)
	{
		throw new RuntimeException ("Limited domain operations not implemented");
	}


	/**
	 * force function domain to 0 LT X LT 1
	 * @param x the value searching for its square root
	 * @param flavor type of algorithm implementation
	 * @return the computed result
	 */
	public double unitDomainLimitation (double x, int flavor)
	{
		if (x < 1)
			return operateOnLimitedDomain (x, flavor);								//   X already in proper domain 0 < X < 1
		return operateOnLimitedDomain (1 / x, flavor) * x;							// using identity sqrt (1 / x) * x = sqrt (x)
	}


	/**
	 * initialize approximation of sqrt
	 * @param x parameter for sqrt computation
	 * @param flavor different for each implementation
	 * @return computed result
	 */
	public double sqrt (double x, int flavor)
	{
		if (x == 0) return 0; if (x == 1) return 1;
		else if (x < 0) { errorTermination (NEGATIVE_PARAMETER); }
		return sqrtAlgorithmImplementation (x, flavor);
	}


	static final int MAXIMUM = 15;			// maximum (relatively speaking) iteration count or number of polynomial terms


}

