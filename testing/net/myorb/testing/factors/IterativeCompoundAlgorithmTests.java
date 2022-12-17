
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


	// constants for evaluations
//	static final int PI_FRACTION = 3, CHECK_MULTIPLE = 4, CHECK_OFFSET = -1;	// 4 * cos^2(PI/3) - 1 = 0
//	static final int PI_FRACTION = 4, CHECK_MULTIPLE = 4, CHECK_OFFSET = -2;	// 4 * cos^2(PI/4) - 2 = 0
	static final int PI_FRACTION = 6, CHECK_MULTIPLE = 4, CHECK_OFFSET = -3;	// 4 * cos^2(PI/6) - 3 = 0

	// constants related to precision
	static final int TRIG_SERIES_ITERATIONS = 20, DISPLAY_PRECISION = 20;


	/**
	 * @return the DerivativeComputer to use for Taylor series
	 */
	IterationTools.DerivativeComputer <Factorization> getComputer ()
	{
		return IT.getCosDerivativeComputer ();
	}


	IterativeCompoundAlgorithmTests ()
	{
		init (COMPOSITE_EVALUATION_TABLE_SIZE);
		// Taylor test prepares series run
		taylor = new TaylorTest ();
		// import tool kit
		IT = taylor.IT;
	}
	protected IterationTools <Factorization> IT;
	protected TaylorTest taylor;


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		IterativeCompoundAlgorithmTests testScripts =
			new IterativeCompoundAlgorithmTests ();

		computePi ();		// use Ramanujan to compute PI

		// check SQRT and PI for precision of computed parameters
		display (sqrt_2, AccuracyCheck.S2_REF, "SQRT 2", DISPLAY_PRECISION);
		display (pi, AccuracyCheck.PI_REF, "PI", DISPLAY_PRECISION);

		// use computed value of PI as parameter to trig function
		testScripts.runTrigTest ();

	}


	/**
	 * run the evaluation of the approximation and compute the error
	 */
	public void runTrigTest ()
	{

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

		// format results for display
		mgr.setDisplayPrecision (DISPLAY_PRECISION);
		String display = mgr.toDecimalString (check);

		// display results
		System.out.print ("Approximation = "); System.out.println (result);
		System.out.print ("Error = "); System.out.println (display);

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
		Factorization piOverN = IT.productOf (fraction, pi);

		// run series evaluation
		// - select series and identify function parameter
		// - run the requested count of iterations and return result
		return taylor.run (iterations, getComputer (), piOverN);

	}


}

