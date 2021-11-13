
package net.myorb.testing.squareRoot;

import net.myorb.math.primenumbers.Factorization;
import net.myorb.math.primenumbers.FactorizationImplementation;

import java.util.Date;

/**
 * framework for side-by-side comparison of implementations of SQRT algorithms
 * @author Michael Druckman
 */
public class SqrtAlgorithmComparisons
{


	static final double RUNS = 100 * 1000;								// number of runs per test
	static final boolean OUTPUT_SUPPRESSED = true;						// suppress output as RUN counts increase

//	static final double RUNS = 100;										// number of runs per test
//	static final boolean OUTPUT_SUPPRESSED = false;						// suppress output as RUN counts increase


	/**
	 * display output of a computation and collect stats
	 * @param test control object to be used for running test
	 * @param x the value of the parameter for this computation
	 * @param s the SQRT value computed
	 */
	public void displayApproximationTest
	(AbstractTestingEnvironment test, double x, double s)
	{
		double chk = s*s, e = Library.abs (x-chk)/x;
		if (e < lo) lo = e; if (e > hi) hi = e;
		
		if (!OUTPUT_SUPPRESSED)
		{
			String
			display = align ("x=" + x, 25);
			display += align (" sqrt=" + s, 30);
			display += align (" iterations=" + test.iterations, 17);
			display += align (" chk=" + chk, 30);
			display += align (" e=" + e, 25);
			System.out.println (display);
		}

		totalError += e; iterationSum += test.iterations; count += 1;
	}
	static String align (String text, int max)
	{ return text + BLANKSPACE.substring (0, max-text.length()); }
	static final String BLANKSPACE  = "                                           ";


	/**
	 * add stats for pass into buffer
	 * @param start the start time stamp of the run
	 * @param finish the ending time stamp
	 * @param title name of run
	 */
	public void stats (Date start, Date finish, String title)
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
	StringBuffer buffer = new StringBuffer ();
	double totalError = 0, lo = 100, hi = 0;
	int iterationSum = 0, count = 0;													// collections of statistical counts for analysis



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
	 * run suite of tests on an abstract object
	 * @param test the implementation of the test abstraction
	 * @param flavor the optional test type
	 */
	public void runApproximationTestFor (AbstractTestingEnvironment test, int flavor)
	{
		String flavorName =
			test.getFlavorDescription (flavor);
		Date start = displayHeader (test.getHeaderText () + flavorName);
		for (int x = 2; x <= RUNS; x++) displayApproximationTest (test, x, test.sqrt (x, flavor));
		for (double x = 1/RUNS; x <= 1; x+=1/RUNS) displayApproximationTest (test, x, test.sqrt (x, flavor));
		displayApproximationTest (test, 1.0/10000.0, test.sqrt (1.0/10000.0, flavor));
		displayApproximationTest (test, 1000*1000, test.sqrt (1000*1000, flavor));
		stats (start, new Date (), test.getTitleText () + flavorName);
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

		SqrtAlgorithmComparisons tests = new SqrtAlgorithmComparisons ();

		// compute primes between 1 and 10000 and mark composites
		support.initFactorizationsWithStats ();
		System.out.println ();

		// run the tests
		TaylorSeries series = new TaylorSeries ();
		series.runCoefficientTest ();					// compute Taylor series coefficients

		// Taylor infinite series for SQRT
		tests.runApproximationTestFor (series, 0);		// domain reduction is 0.9 < X < 1.1 => abs (X - 1) < 0.1 => (X-1)^n gets small quickly

		NewtonRaphsonMethod newton =
			new NewtonRaphsonMethod ();
		// flavors of Newton-Raphson method
		tests.runApproximationTestFor (newton, 0);		// compute bit length approximations of root for each SQRT calculation
		tests.runApproximationTestFor (newton, 1);		// reduce domain to 0 < X < 1 so approximation can be 1 avoiding approximation cost
		tests.runApproximationTestFor (newton, 2);		// reduce domain to 0 < X < 1 and locate proximity to 2^n for approximation
		tests.runApproximationTestFor (newton, 3);		// reduce domain to 0 < X < 1 and locate proximity to LOG2 for approximation
		tests.runApproximationTestFor (newton, 4);		// locate proximity to 2^n for approximation

		Library.TOLERANCE /= 40 * 1000;					// bisection algorithm is very slow so tolerance is reduced to force precision
		// try polynomial root bisection method
		BisectionMethod bisection = new BisectionMethod ();
		tests.runApproximationTestFor (bisection, 0);
		tests.runApproximationTestFor (bisection, 1);
		tests.runApproximationTestFor (bisection, 2);
		Library.TOLERANCE *= 40 * 1000;					// other algorithms converge faster so tolerance is raised to provide level precision

		System.out.println ();
		System.out.println ("+++++++++++++++++++++++++++++++++++");
		System.out.println ();

		System.out.println (tests.buffer);
	}


}

