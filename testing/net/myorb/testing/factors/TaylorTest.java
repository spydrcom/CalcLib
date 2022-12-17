
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


	public TaylorTest ()
	{
		super (FactorizationCore.mgr);
		this.IT = new IterationTools <> (manager);
	}
	protected IterationTools <Factorization> IT;


	/**
	 * display results of test
	 * @param iterations the number to be run
	 * @return the computed result
	 */
	public Factorization computeEuler (int iterations)
	{
		return run (iterations, IT.getExpDerivativeComputer (), IT.ONE);
	}


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		FactorizationCore.init (1_000_000);

		FactorizationCore.display
		(
			new TaylorTest ().computeEuler (125),
			AccuracyCheck.E_REF, "E", 1000
		);

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

