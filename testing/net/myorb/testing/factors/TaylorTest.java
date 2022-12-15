
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

	TaylorTest ()
	{
		super (FactorizationCore.mgr);
		this.IT = new IterationTools <> (manager);
	}
	IterationTools <Factorization> IT;

	/**
	 * @param iterations number of iterations to run
	 * @return the computed sum after specified iterations
	 */
	public Factorization run
		(
			int iterations,
			IterationTools.DerivativeComputer <Factorization> computer
		)
	{
		initializeSummation
		(computer.nTHderivative (0));
		for (int n=1; n<=iterations; n++)
		{ applyIteration (computer.nTHderivative (n)); }
		return summation;
	}

	public Factorization run (int iterations)
	{
		IterationTools.DerivativeComputer <Factorization>
			computer = IT.getExpDerivativeComputer ();
		initializeFunction (manager.getOne ());
		return run (iterations, computer);
	}

	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{
		FactorizationCore.init (1000*1000);

		Factorization approx;
		approx = new TaylorTest ().run (125);
		FactorizationCore.display (approx, AccuracyCheck.E_REF, "E", 1000);

	}

}
