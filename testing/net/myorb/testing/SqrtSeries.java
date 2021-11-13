
package net.myorb.testing;

import net.myorb.math.*;
import net.myorb.math.primenumbers.*;
import net.myorb.math.realnumbers.DoubleFloatingFieldManager;

import java.math.BigInteger;
import java.util.Date;

/**
 * experiments with the Taylor polynomial series
 *  and other algorithms used for SQRT computation.
 * @author Michael Druckman
 */
public class SqrtSeries
{


	static final int MAXIMUM = 15;	// maximum (relatively speaking) iteration count or number of polynomial terms


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
	 * @param x value of X being transformed restricted to 0 &lt; X &lt; 1
	 * @return transformer for X such that 0.9 &lt; X*divisor^2 &lt; 1.1
	 */
	public static double reduceDomain (double x)
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
	 * reduction algorithm attempts to force X into range ABS (X - 1) &lt; 0.1
	 *  seeking the benefit of taking the power series terms down to ending with a multiplier of (0.1)^TERMS at worst
	 * @param x the value of X being reduced for improved convergence of SQRT series
	 * @param divisor compiled product of adjustments up to this point
	 * @param using attempt further adjustment using this
	 * @return new divisor having applied this attempt
	 */
	public static double alterDivisor (double x, double divisor, double using)
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
	public static double bitLengthApproximatedSqrt (double x)
	{
		BigInteger TWO = BigInteger.valueOf (2),
			bigint = BigInteger.valueOf ((long) x);
		int approximatedRootLength = bigint.bitLength () / 2;
		return TWO.pow (approximatedRootLength).doubleValue ();		// 1/2 the bit size
	}


	/**
	 * find nearest 2^n to use as approximation
	 * @param x the value seeking SQRT approximation 0 &lt; X &lt; 1
	 * @return computed approximation
	 */
	public static double power2ApproximatedSqrt (double x)
	{
		double root = 0.5;
		while (x < root*root)
		{ root /= 2; iterations++; }
		return root;
	}


	/**
	 * evaluate Taylor SQRT series at X
	 * @param x value of X for series evaluation
	 * @return computed result
	 */
	public static double taylorSqrtSeries (double x)
	{
		double powers = 1,
			result = sqrtCoef.get (0);
		for (int i = 1; i < MAXIMUM; i++)
		{ result += sqrtCoef.get (i) * (powers *= x); }
		return result;
	}
	static Polynomial.Coefficients<Double> sqrtCoef = new Polynomial.Coefficients<Double> ();


	/**
	 * apply Taylor series to compute SQRT
	 * @param x the value searching for the root
	 * @param multiplier compensation for using 1/x where x was &gt; 1
	 * @param divisor compensation for domain reduction
	 * @return computed result
	 */
	public static double sqrtUsingTaylor (double x, double multiplier, double divisor)
	{
		if (useGenericAbstraction)
			return sqrtSeriesPoly.eval ((x * divisor * divisor) - 1) * multiplier / divisor;
		return taylorSqrtSeries ((x * divisor * divisor) - 1) * multiplier / divisor;
	}
	public static final TaylorPolynomials<Double> TAYLOR = new TaylorPolynomials<Double> (new DoubleFloatingFieldManager ());
	public static final Polynomial.PowerFunction<Double> sqrtSeriesPoly = TAYLOR.getSqrtSeries (MAXIMUM);
	public static boolean useGenericAbstraction = false;


	/**
	 * compute multiplier and divisor for domain reduction
	 * @param x the value of X being transformed for domain reduction
	 * @return the final computed SQRT result
	 */
	public static double sqrtUsingTaylorReduced (double x)
	{
		double firstReduction = x, multiplier = 1;
		if (x > 1) firstReduction = 1 / (multiplier = x);						//       first reduction gives domain 0 < X < 1

		double secondReduction = reduceDomain (firstReduction);					//      second reduction domain is 0.9 < X < 1.1
		return sqrtUsingTaylor (firstReduction, multiplier, secondReduction);	// 80% narrower and centered around 1 (series uses x-1)
	}


	/**
	 * computation of SQRT using Taylor polynomial series
	 *  with domain reduction algorithm (above) which build divisors of SQRTs of perfect squares
	 * @param x parameter to SQRT function
	 * @return computed result
	 */
	public static double sqrtUsingTaylor (double x)
	{
		iterations = MAXIMUM;
		if (x == 0) return 0; else if (x == 1) return 1;
		else if (x < 0) errorTermination (NEGATIVE_PARAMETER);
		return sqrtUsingTaylorReduced (x);
	}


	/**
	 * display table of sqrt(x) for 1 &lt; x &lt;= 100 using Taylor series
	 */
	public static void runApproximationTestsUsingTaylor ()
	{
		Date start =
			displayHeader ("Table of computations of Taylor SQRT polynomial series");
		for (int x = 2; x <= RUNS; x++) displayApproximationTest (x, sqrtUsingTaylor (x));
		for (double x = 1/RUNS; x <= 1; x+=1/RUNS) displayApproximationTest (x, sqrtUsingTaylor (x));
		displayApproximationTest (1.0/10000.0, sqrtUsingTaylor (1.0/10000.0));
		displayApproximationTest (1000*1000, sqrtUsingTaylor (1000*1000));
		stats (start, new Date (), "Taylor series");
		System.out.println ("===");
	}


	/**
	 * SQRT computation loop
	 * @param x approximated root
	 * @param value parameter for sqrt computation
	 * @return computed result
	 */
	public static double newtonRaphsonSqrtLoop (double x, double value)
	{
		String errorMessage = SMALL_DERIVATIVE;
		double y, yPrime, xn, xnp1, toleranceCheck;

FOUND:	do
		{
ERROR:		do
			{
				for (int i = 2*MAXIMUM; i > 0; i--)							// more iterations needed for very small numbers
				{
					y = x * x - value;										// value of the function at the approximated root
					yPrime = x * 2;											// value of the derivative at the approximated root
			
					if (abs (yPrime) < EPSILON) break ERROR;
			
					xn = x;
					xnp1 = xn - (y / yPrime);								// compute x(n+1), next iteration of approximation
					x = xnp1;
			
					iterations++;
					toleranceCheck = abs (xnp1 - xn) / (abs (xnp1));
					if (withinTolerance (toleranceCheck)) break FOUND;
				}
				errorMessage = FAILED_CONVERGENCE; break ERROR;				// maximum iteration count reached
			} while (true); errorTermination (errorMessage);				// error loop break point
		} while (true); return x;											// root found
	}


	/**
	 * use Newton method approximation of sqrt.
	 *  root approximation is done based on bit length of parameter
	 * @param value parameter for sqrt computation
	 * @return computed result
	 */
	public static double newtonRaphsonSqrt (double value)
	{
		iterations = 0;
		double x = bitLengthApproximatedSqrt (value);						// approximated root
		return newtonRaphsonSqrtLoop (x, value);
	}


	/**
	 * use Newton method approximation of sqrt.
	 *  domain is restricted to 0 &lt; X &lt; 1 allowing use of 1 as approximation
	 * @param value the value of parameter for sqrt computation
	 * @param approximationType 1=1, 2=2^n
	 * @return computed result
	 */
	public static double newtonRaphsonRestrictedSqrt
		(double value, int approximationType)
	{
		double x = 1; iterations = 0;										// approximated root = 1, domain reduced to 0 < x < 1
		if (approximationType == 2) x = power2ApproximatedSqrt (value);		// approximated root based on 2^n
		return newtonRaphsonSqrtLoop (x, value);
	}


	/**
	 * use Newton method sqrt approximation
	 * @param x parameter for sqrt computation
	 * @param approximationType 0=bit length, 1=1, 2=2^n
	 * @return computed result
	 */
	public static double sqrtUsingNewton (double x, int approximationType)
	{
		if (x == 0) return 0; if (x == 1) return 1;
		else if (x < 0) { errorTermination (NEGATIVE_PARAMETER); }
		else if (approximationType == 0) return newtonRaphsonSqrt (x);
		else if (x > 1) return newtonRaphsonRestrictedSqrt (1 / x, approximationType) * x;
		return newtonRaphsonRestrictedSqrt (x, approximationType);
	}


	/**
	 * display table of sqrt(x)
	 *  for 1 &lt; x &lt;= 100 using Newton-Raphson method
	 * @param approximationType 0=bit length, 1=1, 2=2^n
	 */
	public static void runApproximationTestsUsingNewton (int approximationType)
	{
		String flavor = APPROXIMATION_TYPE[approximationType];
		Date start = displayHeader ("Table of computations of Newton-Raphson method for SQRT " + flavor);
		for (int x = 2; x <= RUNS; x++) displayApproximationTest (x, sqrtUsingNewton (x, approximationType));
		for (double x = 1/RUNS; x <= 1; x+=1/RUNS) displayApproximationTest (x, sqrtUsingNewton (x, approximationType));
		displayApproximationTest (1.0/10000.0, sqrtUsingNewton (1.0/10000.0, approximationType));
		displayApproximationTest (1000*1000, sqrtUsingNewton (1000*1000, approximationType));
		stats (start, new Date (), "Newton Method " + flavor);
		System.out.println ("===");
	}
	static String[] APPROXIMATION_TYPE = new String[]{"[bit length approximation]", "[restricted domain]", "[2^n approximation]"};


	/**
	 * find root using bisection.
	 *  domain of X has been reduced and
	 *  approximated root range has been set
	 * @param x the reduced value of X for which we seek a root
	 * @param lo the low side of the expected root computation
	 * @param hi the high side of expected root
	 * @return the computed root
	 */
	public static double bisectionSqrtMethod (double x, double lo, double hi)
	{
		double a /* approximated root */, f /* function value */;
FOUND:	while (true)
		{
			for (int i = 5*MAXIMUM; i > 0; i--)
			{
				iterations++;
				a = (hi + lo) / 2; f = a * a - x;
				if (withinTolerance (f)) break FOUND;
				if (f < 0) lo = a; else hi = a;
			}
			errorTermination (FAILED_CONVERGENCE);
		}
		return a;
	}


	/**
	 * find root using bisection
	 * @param x the value searching for the root
	 * @param multiplier compensation for using 1/x where x was &gt; 1
	 * @param reducedDomain reduce domain before bisection
	 * @return the value of the root found
	 */
	public static double bisectionSqrtMethod (double x, double multiplier, boolean reducedDomain)
	{
		iterations = 0;
		double lo = 0, hi = 1, divisor = 1;			// using 1/x for x > 1 allows nice bisection domain of 0 < x < 1
		if (reducedDomain)
		{
			lo = REDUCED_DOMAIN_LO; hi = REDUCED_DOMAIN_HI;		// bisection domain starts 80% narrower
			divisor = reduceDomain (x);
			x *= divisor * divisor;
		}
		return bisectionSqrtMethod (x, lo, hi) * multiplier / divisor;
	}


	/**
	 * standard SQRT domain reduction
	 * @param x value in search of root
	 * @param reducedDomain reduce domain before bisection
	 * @return the computed root
	 */
	public static double sqrtUsingBisectionMethod (double x, boolean reducedDomain)
	{
		if (x < 0) errorTermination (NEGATIVE_PARAMETER);
		else if (x == 0) return 0; else if (x == 1) return 1;
		else if (x > 1) return bisectionSqrtMethod (1 / x, x, reducedDomain);
		return bisectionSqrtMethod (x, 1, reducedDomain);
	}


	/**
	 * display table of sqrt(x) for 1 &lt; x &lt;= 100 using bisection method
	 * @param reducedDomain reduce domain before bisection
	 */
	public static void runApproximationTestsUsingBisection (boolean reducedDomain)
	{
		String flavor = reducedDomain? "[using restricted domain]": "[no domain restriction]";
		Date start = displayHeader ("Table of computations of bisection method for SQRT " + flavor);
		for (int x = 2; x <= RUNS; x++) displayApproximationTest (x, sqrtUsingBisectionMethod (x, reducedDomain));
		for (double x = 1/RUNS; x <= 1; x+=1/RUNS) displayApproximationTest (x, sqrtUsingBisectionMethod (x, reducedDomain));
		displayApproximationTest (1.0/10000.0, sqrtUsingBisectionMethod (1.0/10000.0, reducedDomain));
		displayApproximationTest (1000*1000, sqrtUsingBisectionMethod (1000*1000, reducedDomain));
		stats (start, new Date (), "Bisection Method " + flavor);
		System.out.println ("===");
	}


	/**
	 * display output of a computation and collect stats
	 * @param x the value of the parameter for this computation
	 * @param s the SQRT value computed
	 */
	public static void displayApproximationTest (double x, double s)
	{
		double chk = s*s, e = abs (x-chk)/x;
		if (e < lo) lo = e; if (e > hi) hi = e;
		
		if (!OUTPUT_SUPPRESSED)
		{
			String
			display = align ("x=" + x, 25);
			display += align (" sqrt=" + s, 30);
			display += align (" iterations=" + iterations, 17);
			display += align (" chk=" + chk, 30);
			display += align (" e=" + e, 25);
			System.out.println (display);
		}

		totalError += e; iterationSum += iterations; count += 1;
	}
	static String align (String text, int max)
	{ return text + BLANKSPACE.substring (0, max-text.length()); }
	static double totalError = 0, lo = 100, hi = 0; static int iterations = 0, iterationSum = 0, count = 0;
	static final String BLANKSPACE  = "                                           ";


	/**
	 * determine value proximity to zero
	 * @param value the value being compared with zero
	 * @return TRUE => value within stated constant tolerance of zero
	 */
	static boolean withinTolerance (double value)
	{ return abs (value) < TOLERANCE; }


	/**
	 * standard absolute value function
	 * @param x the value being compared with zero
	 * @return same value as parameter for positive values, otherwise negated value
	 */
	public static double abs (double x)
	{ return x<0? -x: x; }


	/**
	 * add stats for pass into buffer
	 * @param start the start time stamp of the run
	 * @param finish the ending time stamp
	 * @param title name of run
	 */
	static void stats (Date start, Date finish, String title)
	{
		double overallErrorAvg = totalError / count;
		int averageIterationCount = iterationSum / count;
		buffer.append ("===\r").append (title).append ("\r");
		buffer.append ("lo error = " + lo + " hi error = " + hi + "\r");				// display LO and HI error values
		buffer.append ("average computed error  = " + overallErrorAvg + "\r");			// average of computed errors
		buffer.append ("average iteration count = " + averageIterationCount + ", ");	// average of iterations
		iterationSum = 0; totalError = 0; count = 0; lo = 100; hi = 0;					// stat variables reset
		long millis = finish.getTime() - start.getTime();								// display time stamps 
		buffer.append (millis + "ms\r");												// as speed metric 
	}
	static StringBuffer buffer = new StringBuffer ();


	/**
	 * print title block for display
	 * @param title the text to be displayed
	 * @return time stamp for start of test
	 */
	static Date displayHeader (String title)
	{
		System.out.println ();
		System.out.println ("===");
		System.out.println (title);
		System.out.println ("===");
		System.out.println ();
		return new Date ();
	}


	/**
	 * compute sqrt(2) using Taylor series.
	 *  over 40 terms of the series are required to cover all displayed decimal places of accuracy.
	 *  the ratio of term/term coefficients presents an interesting pattern.
	 */
	public static void runCoefficientTest ()
	{
		Date start = displayHeader ("Computation of coefficients of Taylor SQRT polynomial series");

		// object that manage prime factorizations
		FactorizationFieldManager mgr = new FactorizationFieldManager ();
		// generic Taylor series evaluation object instanced to perform computations on prime factorizations
		TaylorPolynomials<Factorization> taylor = new TaylorPolynomials<Factorization> (mgr);

		// the polynomial evaluation object and the coefficients list for the series
		Polynomial<Double> p = new Polynomial<Double> (new DoubleFloatingFieldManager ());
		Polynomial.PowerFunction<Double> poly = p.getPolynomialFunction (sqrtCoef);
		Factorization nth = mgr.newScalar (1);

		double testValue = 2, parm = 1/testValue - 1, c = 1.0, dratio, fOfX;	// values needed to evaluate series

		for (int i = 0; i < 100; i++) 					// 100 iterations should be a large number (but VERY slow convergence)
		{
			// TaylorPolynomial object has method
			//  compute coefficient of next polynomial term
			Factorization nthP1 = taylor.sqrtCoefficient (i);

			// ratio of coefficient (N+1) to coefficient (N)
			Factorization ratio = mgr.multiply (nthP1, mgr.invert (nth));
			// convert to double value and do multiplication to get real value of coefficient (N+1)
			dratio = mgr.toNumber (ratio).doubleValue ();

			sqrtCoef.add (c = c * dratio);				// (n+1)th coefficient added to polynomial
			fOfX = poly.eval (parm) * testValue;			// evaluate series with new term in place

			System.out.print ("n = " + i);				// display results of series with new term
			System.out.print (", ratio = " + ratio);
			System.out.print (" = " + dratio);
			System.out.print (", c = " + c);
			System.out.print (", approx = " + fOfX);
			System.out.println ();

			nth = nthP1;								// prepare for next term
		}

		Date finish = new Date ();
		long millis = finish.getTime() - start.getTime();
		System.out.println ("Calculation time = " + millis + "ms");

		System.out.println
		("===\rFull factorization of last term");
		System.out.println (nth);						// this is a display of the prime factorization of the nth coefficient
		System.out.println ("===");
	}


	/**
	 * entry point
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		FactorizationImplementation support;
		// use prime factorizations to compute coefficients, this avoids NaN intermediate products in computations
		Factorization.setImplementation (support = new FactorizationImplementation (10 * 1000));

		// compute primes between 1 and 10000 and mark composites
		support.initFactorizationsWithStats ();
		System.out.println ();

		// run the tests
		runCoefficientTest ();							// compute Taylor series coefficients
		
		TOLERANCE /= 10 * 1000;							// bisection algorithm is very slow so tolerance is reduced to force precision
		// try polynomial root bisection method
		runApproximationTestsUsingBisection (false);
		runApproximationTestsUsingBisection (true);
		TOLERANCE *= 10 * 1000;							// other algorithms converge faster so tolerance is raised to provide level precision

		// flavors of Newton-Raphson method
		runApproximationTestsUsingNewton (0);			// compute bit length approximations of root for each SQRT calculation
		runApproximationTestsUsingNewton (1);			// reduce domain to 0 < X < 1 so approximation can be 1 avoiding approximation cost
		runApproximationTestsUsingNewton (2);			// reduce domain to 0 < X < 1 and locate proximity to 2^n for approximation
		
		// Taylor infinite series for SQRT
		runApproximationTestsUsingTaylor ();			// domain reduction is 0.9 < X < 1.1 => abs (X - 1) < 0.1 => (X-1)^n gets small quickly

		System.out.println ();
		System.out.println ("+++++++++++++++++++++++++++++++++++");
		System.out.println ();

		System.out.println (buffer);
	}
	static double EPSILON = 0.0000000001, TOLERANCE = 0.00000000001;
	static final boolean OUTPUT_SUPPRESSED = true;
	static final double RUNS = 100 * 1000;


}

