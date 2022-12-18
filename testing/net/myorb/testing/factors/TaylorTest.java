
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.Taylor;
import net.myorb.math.computational.iterative.IterationTools;

import net.myorb.math.primenumbers.Factorization;

/**
 * evaluation of the Taylor series for computation of Eulers constant
 * - this is the entry point and driver for specification of the iteration count
 * @author Michael Druckman
 */
public class TaylorTest extends Taylor <Factorization>
{


	// choose tests to run
	public static final boolean
			RUN_EULER_TEST =  true,
			RUN_POLYLOG_TEST = false
	;


	static final int							// iteration counts for tests
			EULER_SERIES_ITERATIONS = 50,		//		converges about 1 digit per iteration
			POLYLOG_SERIES_ITERATIONS = 500		//		very slow convergence, only 2 digits for 500 iterations
	;


	public TaylorTest ()
	{
		super (FactorizationCore.mgr);
		this.IT = new IterationTools <> (manager);
	}
	protected IterationTools <Factorization> IT;


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		FactorizationCore.init (1_000_000);

		TaylorTest TT = new TaylorTest ();

		if (RUN_EULER_TEST)
		{
			FactorizationCore.display
			(
				TT.computeEuler (EULER_SERIES_ITERATIONS),
				AccuracyCheck.E_REF, "E", 1000
			);			
		}

		if (RUN_POLYLOG_TEST)
		{
			FactorizationCore.display
			(
				TT.computeLi2 (POLYLOG_SERIES_ITERATIONS, TT.IT.ONE),
				AccuracyCheck.Li2_REF, "Li2", 1000
			);
		}

	}


	/*
	 * Euler test
	 */

	/**
	 * display results of test
	 * @param iterations the number to be run
	 * @return the computed result
	 */
	public Factorization computeEuler (int iterations)
	{
		return run (iterations, IT.getExpDerivativeComputer (), IT.ONE);
	}


	/*
	 * inverse tangent integrals test
	 */

	/**
	 * inverse tangent integrals test
	 * @param iterations the number to be run for test
	 * @param Sqrt_3 the computed SQRT(3)
	 * @return the computed result
	 */
	public Factorization runInvTanTest (int iterations, Factorization Sqrt_3)
	{
		Factorization p = IT.sumOf (IT.S (2), manager.negate (Sqrt_3));
		return run (iterations, IT.getTi2DerivativeComputer (), p);
	}


	/*
	 * elliptic integrals 
	 */

	/**
	 * K elliptic integral
	 * @param iterations the number to be run for test
	 * @param PI value of PI to use in calculation
	 * @param numerator of the parameter fraction
	 * @param ref text of the reference
	 */
	public void runKTest
		(
			int iterations,
			Factorization PI, Factorization numerator,
			String ref
		)
	{
		Factorization HALF = IT.oneOver (IT.S (2));
		Factorization HN = IT.productOf (HALF, numerator);

		Factorization result = IT.productOf
			(
				run
				(
					iterations,
					IT.getKDerivativeComputer (),
					HN
				),
				IT.productOf (HALF, PI)
			);

		FactorizationCore.display
		(
			result, ref, "K", 1000
		);
	}


	/*
	 * Li2 test
	 */

	/**
	 * evaluate Li2(1/2) and check result
	 * - difference between Li2 and PI^2 is Ln2^2
	 * @param iterations the number to be run for test
	 * @param PI value of PI to use in calculation
	 */
	public void runLn2SQtest (int iterations, Factorization PI)
	{
		Factorization Li2Half = computeLi2 (iterations, IT.oneOver (IT.S (2)));
		Factorization PiSQ12 = IT.productOf (IT.POW (PI, 2), IT.oneOver (IT.S (12)));
		Factorization HalfLn2SQ = IT.sumOf (PiSQ12, manager.negate (Li2Half));
		Factorization Ln2SQ = IT.productOf (HalfLn2SQ, IT.S (2));

		FactorizationCore.display
		(
			Ln2SQ, AccuracyCheck.Ln2SQ_REF, "Ln2SQ", 100
		);
		// Li2(1/2) = PI^2/12 - Ln(2)^2/2
	}

	/**
	 * display results of test
	 * @param iterations the number to be run
	 * @param parameter the value to pass to the function
	 * @return the computed result
	 */
	public Factorization computeLi2 (int iterations, Factorization parameter)
	{
		return run (iterations, IT.getLi2DerivativeComputer (), parameter);
	}


	/*
	 * methods used in ATAN tests
	 */

	/**
	 * check for factor of PI on ATAN
	 * @param iterations the number to be run
	 * @param of the parameter to pass to ATAN function
	 * @param checkProduct anticipated factor of PI
	 * @return the computed result
	 */
	public Factorization computeAtan
		(int iterations, Factorization of, int checkProduct)
	{
		Factorization approx =
			run (iterations, IT.getAtanDerivativeComputer (), of);
		return IT.productOf (approx, IT.S (checkProduct));
	}

	/**
	 * compute TAN(PI/12)
	 * @param Sqrt_3 the computed SQRT(3)
	 * @return 2 - SQRT(3)
	 */
	public Factorization atan12 (Factorization Sqrt_3)
	{ return IT.sumOf (manager.negate (Sqrt_3), IT.S (2)); }

	/**
	 * compute TAN(PI/6)
	 * @param Sqrt_3 the computed SQRT(3)
	 * @return 1 / SQRT(3)
	 */
	public Factorization atan6 (Factorization Sqrt_3)
	{ return IT.oneOver (Sqrt_3); }


}

