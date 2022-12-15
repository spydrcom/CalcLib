
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.Taylor;
import net.myorb.math.computational.iterative.IterationTools;

import net.myorb.math.primenumbers.Factorization;

/**
 * evaluation of the Taylor series for computation of e
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
	 * run the computation of e
	 * @param iterations the number to be run
	 * @return the computed result
	 */
	public Factorization run (int iterations)
	{
		return run (iterations, IT.getExpDerivativeComputer (), manager.getOne ());
	}


	/**
	 * display results of test
	 * @param iterations the number to be run
	 * @return the computed result
	 */
	public Factorization computeEuler (int iterations)
	{
		Factorization approx = run (iterations);
		FactorizationCore.display (approx, AccuracyCheck.E_REF, "E", 1000);
		return approx;
	}


	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{

		FactorizationCore.init (1000*1000);

		new TaylorTest ().computeEuler (125);

	}


}

