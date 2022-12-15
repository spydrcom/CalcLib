
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.IterationTools;
import net.myorb.math.primenumbers.Factorization;

/**
 * evaluation of the Taylor series for trigonometry using chosen precision of PI
 * - this is the entry point and driver for specification of the iteration count
 * @author Michael Druckman
 */
public class IterativeCompoundAlgorithmTests extends IterativeAlgorithmTests
{


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		// use Ramanujan to compute PI
		init (COMPOSITE_EVALUATION_TABLE_SIZE);  computePi ();

		// check SQRT and PI for precision of computed parameters
		display (sqrt_2, AccuracyCheck.S2_REF, "SQRT 2", DISPLAY_PRECISION);
		display (pi, AccuracyCheck.PI_REF, "PI", DISPLAY_PRECISION);

		// check Taylor result
		Factorization result, check;
		check = mgr.pow (result = run (20), 2);
		String display = mgr.toDecimalString (check);

		// dispaly results
		System.out.println (result);
		System.out.println (display);

	}


	/**
	 * prepare and run Taylor trigonometric function series
	 * @param iterations number of iteration for Taylor series
	 * @return computed result from series evaluation
	 */
	public static Factorization run (int iterations)
	{

		// Taylor test prepares series run
		TaylorTest taylor = new TaylorTest ();
		IterationTools <Factorization> IT = taylor.IT;

		// choose fraction of PI for parameter
		Factorization fraction = IT.oneOver (IT.S (6));
		Factorization piOverN = IT.productOf (fraction, pi);

		// select series and identify function parameter
		IterationTools.DerivativeComputer <Factorization>
			computer = IT.getCosDerivativeComputer ();
		taylor.initializeFunction (piOverN);

		// run series evaluation
		return taylor.run (iterations, computer);

	}


}

