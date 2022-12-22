
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.IterationTools;
import net.myorb.math.primenumbers.Factorization;

/**
 * evaluation of the Taylor series for trigonometry using chosen precision of PI
 * - this is the entry point and driver for specification of the iteration count
 * - this test combines calculations done with Taylor, Ramanujan, and Newton
 * @author Michael Druckman
 */
public class IterativeCompoundAlgorithmTests extends IterativeAlgorithmTests
{


	// choose tests to run
	public static final boolean RUN_TRIG_TEST = true, RUN_POLYLOG_TEST = true, RUN_K_TEST = true;


	// constants for evaluations
//	static final int PI_FRACTION = 3, CHECK_MULTIPLE = 4, CHECK_OFFSET = -1;	// 4 * cos^2(PI/3) - 1 = 0
//	static final int PI_FRACTION = 4, CHECK_MULTIPLE = 4, CHECK_OFFSET = -2;	// 4 * cos^2(PI/4) - 2 = 0
	static final int PI_FRACTION = 6, CHECK_MULTIPLE = 4, CHECK_OFFSET = -3;	// 4 * cos^2(PI/6) - 3 = 0

	// constants related to precision
	static final int TRIG_SERIES_ITERATIONS = 30, POLYLOG_SERIES_ITERATIONS = 300, K_SERIES_ITERATIONS = 300;
	static final int DISPLAY_PRECISION = 20;


	/**
	 * @return the DerivativeComputer to use for Taylor series cosine computation
	 */
	IterationTools.DerivativeComputer <Factorization> getComputer ()
	{
		return IT.getCosDerivativeComputer ();
	}


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		IterativeCompoundAlgorithmTests testScripts =
			new IterativeCompoundAlgorithmTests ();

		testScripts.computeSqrt ();		// prepare SQRT constants
		testScripts.computePi ();		// use Ramanujan to compute PI
		timeStamp ();

		System.out.println ();
		System.out.println ("+-+-+-+-+-+-+-+-+");
		System.out.println ();

		// check SQRT and PI for precision of computed parameters
		display (sqrt_2, AccuracyCheck.S2_REF, "SQRT 2");
		display (pi, AccuracyCheck.PI_REF, "PI");

		// use computed value of PI as parameter to trig function
		if (RUN_TRIG_TEST) testScripts.runTrigTest ();

		// use computed value of PI as test result for Li2 test
		if (RUN_POLYLOG_TEST) testScripts.runPolylogTest ();

		// use computed value of PI as test result for K test
		if (RUN_K_TEST) testScripts.runKTest ();

	}


	/**
	 * run the evaluation of the approximation and compute the error
	 * - this is a Taylor series test of computation of K elliptical integral
	 */
	public void runKTest ()
	{
		runKTest (K_SERIES_ITERATIONS, pi, IT.ONE, AccuracyCheck.K_REF);
		// runKTest (K_SERIES_ITERATIONS, pi, sqrt_3, AccuracyCheck.Ksqrt_REF);
		timeStamp ();
	}


	/**
	 * run the evaluation of the approximation and compute the error
	 * - this is a Taylor series test of computation of Li2
	 */
	public void runPolylogTest ()
	{
		runLn2SQtest (POLYLOG_SERIES_ITERATIONS, pi);
		timeStamp ();
	}


	/**
	 * run the evaluation of the approximation and compute the error
	 * - this is a Taylor series test of computation of cos(PI/n)
	 */
	public void runTrigTest ()
	{

		System.out.println ();
		System.out.println ("Trig Test"); System.out.println ();

		// run Taylor evaluation
		Factorization result = run (TRIG_SERIES_ITERATIONS);

		// compute error
		Factorization check = IT.sumOf
			(
				IT.productOf
				(
					IT.POW (result, 2),
					IT.S (CHECK_MULTIPLE)
				),
				IT.S (CHECK_OFFSET)
			);

		// format results and display
		String display = FactorizationCore.toDecimalString (check, DISPLAY_PRECISION);
		System.out.print ("Approximation = "); System.out.println (result);
		System.out.print ("Error = "); System.out.println (display);
		System.out.println (); System.out.println ("===");
		System.out.println ();
		timeStamp ();

	}


	/**
	 * prepare and run Taylor trigonometric function series
	 * @param iterations number of iteration for Taylor series
	 * @return computed result from series evaluation
	 */
	public Factorization run (int iterations)
	{
		// choose fraction of PI for parameter
		Factorization fraction = IT.oneOver (IT.S (PI_FRACTION));
		Factorization piOverN = IT.productOf (fraction, getReducedPi ());
		timeStamp ();

		// run series evaluation
		// - select series and identify function parameter
		// - run the requested count of iterations and return result
		return run (iterations, getComputer (), piOverN);
	}


	/**
	 * use Precision Manager to reduce precision of PI
	 * - this reduced the computation efforts of the series evaluation
	 * @return the reduced representation of PI
	 */
	Factorization getReducedPi ()
	{
		return FactorizationCore
			.mgr.getPrecisionManager ()
			.adjust (pi, 10, System.out)
			.getAdjustedValue ();		
	}


}

