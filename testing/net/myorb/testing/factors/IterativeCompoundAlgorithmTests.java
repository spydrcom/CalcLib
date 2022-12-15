
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.IterationTools;
import net.myorb.math.primenumbers.Factorization;

public class IterativeCompoundAlgorithmTests extends IterativeAlgorithmTests
{

	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		FactorizationCore.init (COMPOSITE_EVALUATION_TABLE_SIZE);

		computePi ();

		FactorizationCore.display (sqrt_2, AccuracyCheck.S2_REF, "SQRT 2", DISPLAY_PRECISION);
		FactorizationCore.display (pi, AccuracyCheck.PI_REF, "PI", DISPLAY_PRECISION);

		System.out.print (run (20));
	}

	public static Factorization run (int iterations)
	{
		TaylorTest taylor = new TaylorTest ();

		Factorization Q =
			FactorizationCore.mgr.invert
				(FactorizationCore.mgr.newScalar(4));
		Factorization piOver4 = FactorizationCore.mgr.multiply (Q, pi);

		IterationTools.DerivativeComputer <Factorization>
			computer = taylor.IT.getCosDerivativeComputer ();
		taylor.initializeFunction (piOver4);

		return taylor.run (iterations, computer);
	}

}
