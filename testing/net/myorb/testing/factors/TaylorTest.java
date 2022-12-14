
package net.myorb.testing.factors;

import net.myorb.math.computational.iterative.Taylor;

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
	}

	/**
	 * @param iterations number of iterations to run
	 * @return the computed sum after specified iterations
	 */
	public Factorization run (int iterations)
	{
		initializeSummation (manager.getOne ());
		for (int i=1; i<=iterations; i++)
		{ applyIteration (manager.getOne ()); }
		return summation;
	}

	/**
	 * entry point for running the test
	 * @param a not used
	 */
	public static void main (String[] a)
	{
		FactorizationCore.init (1000*1000);

		System.out.println (FactorizationCore.toRatio
			(new TaylorTest ().run (125)));
	}

}
