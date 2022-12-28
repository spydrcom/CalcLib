
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
	public static final boolean RUN_TRIG_TEST = false, RUN_HTRIG_TEST = false, RUN_POLYLOG_TEST = false, RUN_EI_TESTS = true;


	// constants for evaluations
//	static final int PI_FRACTION = 3, CHECK_MULTIPLE = 4, CHECK_OFFSET = -1;	// 4 * cos^2(PI/3) - 1 = 0
//	static final int PI_FRACTION = 4, CHECK_MULTIPLE = 4, CHECK_OFFSET = -2;	// 4 * cos^2(PI/4) - 2 = 0
	static final int PI_FRACTION = 6, CHECK_MULTIPLE = 4, CHECK_OFFSET = -3;	// 4 * cos^2(PI/6) - 3 = 0

	// constants related to precision
	static final int TRIG_SERIES_ITERATIONS = 30, HTRIG_SERIES_ITERATIONS = 20,
			POLYLOG_SERIES_ITERATIONS = 30, EI_SERIES_ITERATIONS = 80;
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

//		testScripts.enableTracing ();
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

		// compute SINH and COSH as test source for ARTANH
		if (RUN_HTRIG_TEST) testScripts.runHTrigTest ();

		// use computed value of PI as test result for Li2 test
		if (RUN_POLYLOG_TEST) testScripts.runPolylogTest ();

		// use computed value of PI as test result for EI tests
		if (RUN_EI_TESTS) testScripts.runEITest ();

	}


	/**
	 * run the evaluation of the approximation and compute the error
	 * - this is a Taylor series test of computation of elliptical integrals
	 */
	public void runEITest ()
	{
		runKTest (EI_SERIES_ITERATIONS, pi, IT.ONE, AccuracyCheck.K_REF);
		runKTest (EI_SERIES_ITERATIONS, pi, reduce (sqrt_3), AccuracyCheck.Ksqrt_REF);
		runETest (EI_SERIES_ITERATIONS, pi, reduce (sqrt_2), AccuracyCheck.Esqrt_REF);
		timeStamp ();
	}
	Factorization reduce (Factorization x)
	{
		return FactorizationCore.reduce (x, 20);
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
	 * - this is a Taylor series test of computation of sinh, cosh, and artanh
	 */
	public void runHTrigTest ()
	{
		Factorization
			HALF = IT.oneOver (IT.S (2)),
			sinh = run (HTRIG_SERIES_ITERATIONS, IT.getSinhDerivativeComputer (), HALF),
			cosh = run (HTRIG_SERIES_ITERATIONS, IT.getCoshDerivativeComputer (), HALF);
		FactorizationCore.display ( sinh, "0.5210953054937473616224256264114", "sinh", 200 );
		FactorizationCore.display ( cosh, "1.1276259652063807852262251614027", "cosh", 200 );

		Factorization
			tanh = manager.multiply (sinh, manager.invert (cosh)),
			artanh = run (3*HTRIG_SERIES_ITERATIONS, IT.getArtanhDerivativeComputer (), tanh),
			test = manager.multiply (artanh, manager.newScalar (2)),
			error = manager.add (test, manager.newScalar (-1));
		System.out.println ("artanh");
		display (artanh, error);
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
		display (result, check);

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
	 * - IterativeAlternativeAlgorithmTests reduces the PI representation further
	 * - the compute time overhead is significantly altered as these tests show
	 * - also note the change to the computation precision
	 * @return the reduced representation of PI
	 */
	Factorization getReducedPi ()
	{
		return FactorizationCore
			.mgr.getPrecisionManager ()
			.adjust (pi, 10, System.out)
			.getAdjustedValue ();		
	}


	/**
	 * show test results
	 * @param result the computed result of a test
	 * @param check the normalized result to zero as a check of error
	 */
	void display (Factorization result, Factorization check)
	{
		// format results and display
		String display = FactorizationCore.toDecimalString (check, DISPLAY_PRECISION);
		System.out.print ("Approximation = "); System.out.println (result);
		System.out.print ("Error = "); System.out.println (display);
		System.out.println (); System.out.println ("===");
		System.out.println ();
		timeStamp ();
	}


}

